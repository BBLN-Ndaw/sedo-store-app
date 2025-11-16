package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.dto.DailySalesRequestDto
import com.sedo.jwtauth.model.dto.DashboardNotificationDto
import com.sedo.jwtauth.model.dto.NotificationType
import com.sedo.jwtauth.model.dto.StatDto
import com.sedo.jwtauth.model.entity.OrderStatus.CONFIRMED
import com.sedo.jwtauth.model.entity.OrderStatus.DELIVERED
import com.sedo.jwtauth.model.entity.OrderStatus.SHIPPED
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.collections.forEach

@Service
class DashboardService(
    private val orderService: OrderService,
    private val productService: ProductService,
    private val mongoTemplate: MongoTemplate
) {
    
    private val logger = LoggerFactory.getLogger(DashboardService::class.java)
    
    /**
     * get dashboard statistics
     */
    fun getDashboardStatistics(): StatDto {
        logger.debug("Generating dashboard statistics")
        
        val today = LocalDate.now()
        val startOfMonth = today.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val endOfMonth = today.plusMonths(1).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()

        // Calculate current metrics
        val dailySales = orderService.getDailySalesSummary(DailySalesRequestDto(Instant.now())).value
        val processingOrders = orderService.getAllOrderInProcessingStatus().size
        val productsInStock = productService.getProductsInStock()
        val monthlyRevenue = orderService.getMonthlyRevenue(startOfMonth, endOfMonth)
        val revenuePerMonthInCurrentYear = getRevenuePerMonth(today.year)
        val averageOrderValue = orderService.getAverageOrderValue()
        val monthlyCancelledOrders = orderService.getMonthlyCancelledOrdersCount(startOfMonth, endOfMonth)

        val stats = StatDto(
                dailySales = dailySales,
                processingOrders = processingOrders,
                productsInStock = productsInStock,
                monthlyRevenue = monthlyRevenue,
                revenuePerMonthInCurrentYear = revenuePerMonthInCurrentYear,
                averageOrderValue = averageOrderValue,
                monthlyCancelledOrders = monthlyCancelledOrders
        )
        
        logger.info("Dashboard statistics generated successfully")
        return stats
    }

    fun getRevenuePerMonth(year: Int): Map<String, BigDecimal> {
        logger.debug("Generating revenue per month for year: $year")
        val revenuePerMonth = mutableMapOf<String, BigDecimal>()

        for (month in 1..12) {
            val startOfMonth = LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            val endOfMonth = if (month == 12) {
                LocalDate.of(year + 1, 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            } else {
                LocalDate.of(year, month + 1, 1).atStartOfDay(ZoneId.systemDefault()).toInstant()
            }

            val monthlyRevenue = orderService.getMonthlyRevenue(startOfMonth, endOfMonth)
            revenuePerMonth[startOfMonth.atZone(ZoneId.systemDefault()).month.name] = monthlyRevenue
        }

        logger.info("Revenue per month generated successfully for year: $year")
        return revenuePerMonth
    }

    /**
     * Generate real-time notifications
     *
     * @return List of recent notifications
     */
    fun generateRealTimeNotifications(): List<DashboardNotificationDto> {
        logger.debug("Generating real-time dashboard notifications")

        val notifications = mutableListOf<DashboardNotificationDto>()
        var notificationId = 1L

        try {
            // 1. Notifications for recent delivered sales
            val recentDeliveredSales = getRecentDeliveredSalesNotifications()
            recentDeliveredSales.forEach { notification ->
                notifications.add(notification.copy(id = notificationId++))
            }

            // 2. Notifications for recent confirmed orders
            val recentOrders = getRecentOrdersNotifications()
            recentOrders.forEach { notification ->
                notifications.add(notification.copy(id = notificationId++))
            }

            // 3. Notifications for low stock products
            val lowStockNotifications = getLowStockNotifications()
            lowStockNotifications.forEach { notification ->
                notifications.add(notification.copy(id = notificationId++))
            }

            // 4. Notifications for new customers
            val newCustomerNotifications = getNewCustomerNotifications()
            newCustomerNotifications.forEach { notification ->
                notifications.add(notification.copy(id = notificationId++))
            }

        } catch (e: Exception) {
            logger.error("Error generating real-time notifications", e)
            emptyList<DashboardNotificationDto>()
        }
        return notifications.sortedByDescending { it.id }.take(20)
    }

    private fun getRecentDeliveredSalesNotifications(): List<DashboardNotificationDto> {
        return try {
            // Récupérer les ventes recentes
            val oneDayAgo = Instant.now().minus(24, ChronoUnit.HOURS)
            val recentDeliveredOrders = orderService.getOrderByStatus(listOf(SHIPPED, DELIVERED))
            .filter { it.updatedAt?.isAfter(oneDayAgo) == true }
                    .take(5)

            recentDeliveredOrders.map { order ->
                DashboardNotificationDto(
                        id = 0, // ID sera assigné plus tard
                        type = NotificationType.SALE,
                        message = "Commande Livrée #${order.orderNumber}",
                        time = calculateRelativeTime(order.updatedAt ?: order.createdAt ?: Instant.now()),
                        amount = order.totalAmount,
                        userId = null,
                        userName = order.customerUserName
                )
            }
        } catch (e: Exception) {
            logger.warn("Failed to get recent sales notifications", e)
            emptyList()
        }
    }

    private fun getRecentOrdersNotifications(): List<DashboardNotificationDto> {
        return try {
            // get recent orders confirmed in last 6 hours
            val sixHoursAgo = Instant.now().minus(6, ChronoUnit.HOURS)
            val recentOrders = orderService.getOrderByStatus(
                    listOf(CONFIRMED)
            ).filter { it.updatedAt?.isAfter(sixHoursAgo) == true }
                    .take(5)
            recentOrders.map { order ->
                DashboardNotificationDto(
                        id = 0, // ID will be assigned later
                        type = NotificationType.ORDER,
                        message = "Nouvelle Commande confirmée - #${order.orderNumber}",
                        time = calculateRelativeTime(order.createdAt ?: Instant.now()),
                        amount = order.totalAmount,
                        userId = null,
                        userName = order.customerUserName
                )
            }
        } catch (e: Exception) {
            logger.warn("Failed to get recent orders notifications", e)
            emptyList()
        }
    }

    private fun getLowStockNotifications(): List<DashboardNotificationDto> {
        return try {
            val lowStockProducts = productService.getLowStockProducts()
            lowStockProducts.take(5).map { product ->
                DashboardNotificationDto(
                        id = 0, // ID will be assigned later
                        type = NotificationType.STOCK,
                        message = "Faible stock alert: ${product.name}",
                        time = calculateRelativeTime(LocalDateTime.now().minusHours(1))
                )
            }
        } catch (e: Exception) {
            logger.warn("Failed to get low stock notifications", e)
            emptyList()
        }
    }

    private fun getNewCustomerNotifications(): List<DashboardNotificationDto> {
        return try {
            // get users created in last 2 days excluding ADMIN and EMPLOYEE roles
            val twoDaysAgo = Instant.now().minus(2, ChronoUnit.DAYS)

            val query = Query(Criteria.where("createdAt").gte(twoDaysAgo))
            query.limit(3)

            val recentUsers = mongoTemplate.find(query, com.sedo.jwtauth.model.entity.User::class.java)
                    .filter { !it.roles.contains("ADMIN") && !it.roles.contains("EMPLOYEE") }

            recentUsers.map { user ->
                DashboardNotificationDto(
                        id = 0, // ID will be assigned later
                        type = NotificationType.CUSTOMER,
                        message = "Nouveau client: ${user.firstName} ${user.lastName}",
                        time = calculateRelativeTime(user.createdAt ?: Instant.now()),
                        userId = null,
                        userName = user.userName
                )
            }
        } catch (e: Exception) {
            logger.warn("Failed to get new customer notifications", e)
            emptyList()
        }
    }

    private fun calculateRelativeTime(instant: Instant): String {
        val now = Instant.now()
        val duration = java.time.Duration.between(instant, now)
        val minutes = duration.toMinutes()
        val hours = duration.toHours()
        val days = duration.toDays()

        return when {
            minutes < 1 -> "just now"
            minutes < 60 -> "$minutes minute${if (minutes != 1L) "s" else ""} ago"
            hours < 24 -> "$hours hour${if (hours != 1L) "s" else ""} ago"
            days == 1L -> "1 day ago"
            days < 7 -> "$days days ago"
            else -> {
                val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                localDate.format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            }
        }
    }

    private fun calculateRelativeTime(dateTime: LocalDateTime): String {
        return calculateRelativeTime(dateTime.atZone(ZoneId.systemDefault()).toInstant())
    }
}