package com.sedo.jwtauth.service

import Product
import com.sedo.jwtauth.constants.Constants.Order.FREE_SHIPPING_AMOUNT
import com.sedo.jwtauth.event.OrderCompletedEvent
import com.sedo.jwtauth.event.InvoiceGenerationRequestedEvent
import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.exception.UnAvailableProductException
import com.sedo.jwtauth.mapper.toOrderItem
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.dto.CartDto
import com.sedo.jwtauth.model.dto.DailySalesResponseDto
import com.sedo.jwtauth.model.dto.DailySalesRequestDto
import com.sedo.jwtauth.model.dto.OrderStatusUpdateRequest
import com.sedo.jwtauth.model.dto.PaypalCapturedResponse
import com.sedo.jwtauth.model.dto.TopSellingProductDto
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderItem
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.OrderStatus.CANCELLED
import com.sedo.jwtauth.model.entity.OrderStatus.CONFIRMED
import com.sedo.jwtauth.model.entity.OrderStatus.DELIVERED
import com.sedo.jwtauth.model.entity.OrderStatus.PENDING
import com.sedo.jwtauth.model.entity.OrderStatus.PROCESSING
import com.sedo.jwtauth.model.entity.OrderStatus.READY_FOR_PICKUP
import com.sedo.jwtauth.model.entity.OrderStatus.SHIPPED
import com.sedo.jwtauth.model.entity.PaymentMethod
import com.sedo.jwtauth.model.entity.PaymentStatus
import com.sedo.jwtauth.repository.OrderRepository
import com.sedo.jwtauth.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest.of
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalQueries.zone
import java.util.UUID

/**
 * Service class responsible for comprehensive order management in the Store Management System.
 * 
 * This service provides complete order lifecycle management including:
 * - Order creation and processing workflows
 * - Payment integration with PayPal
 * - Order status tracking and updates
 * - Inventory management and stock validation
 * - Financial calculations and reporting
 * - Advanced search and filtering capabilities
 * - Integration with audit logging and event publishing
 * 
 * Business Logic:
 * - Handles complete order workflow from creation to delivery
 * - Integrates with PayPal for secure payment processing
 * - Manages inventory levels and product availability
 * - Calculates pricing, taxes, and shipping costs
 * - Supports various order statuses and state transitions
 * - Provides comprehensive reporting and analytics
 * 
 * Order Lifecycle:
 * 1. PENDING - Initial order creation
 * 2. CONFIRMED - Payment confirmed
 * 3. PROCESSING - Order being prepared
 * 4. READY_FOR_PICKUP - Ready for customer pickup
 * 5. SHIPPED - Order shipped to customer
 * 6. DELIVERED - Order delivered successfully
 * 7. CANCELLED - Order cancelled
 * 
 * Financial Features:
 * - Automatic tax calculation
 * - Shipping cost computation
 * - Free shipping threshold management
 * - Revenue tracking and reporting
 * - Average order value calculations
 * - Daily and monthly sales summaries
 * 
 * Integration Points:
 * - PayPal service for payment processing
 * - Product service for inventory management
 * - Email service for order notifications
 * - Audit service for comprehensive logging
 * - Event publishing for order state changes
 * 
 * Dependencies:
 * - OrderRepository for order data persistence
 * - ProductRepository for inventory validation
 * - PayPalService for payment processing
 * - AuditService for operation tracking
 * - ApplicationEventPublisher for event-driven architecture
 * - MongoTemplate for advanced querying capabilities
 *
 */
@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val auditService: AuditService,
    private val payPalService: PayPalService,
    private val userService: UserService,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val mongoTemplate: MongoTemplate
) {
    
    private val logger = LoggerFactory.getLogger(OrderService::class.java)
    
    fun getAllOrders(page: Int, size: Int):  Page<Order> {
        logger.debug("Retrieving all orders")
        val query = Query()
        val pageable: Pageable = of(page, size)
        query.with(pageable)

        val orders = mongoTemplate.find(query, Order::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Order::class.java)

        logger.info("Number of products found: {}", total)
        return PageImpl(orders, pageable, total)
    }

    fun getAllOrderInProcessingStatus(): List<Order> {
        logger.debug("Retrieving all orders in PROCESSING status")
        val processingOrdersStatus = listOf(
                CONFIRMED, PROCESSING, READY_FOR_PICKUP)
        return orderRepository.findByStatusInOrderByCreatedAtDesc(processingOrdersStatus)
    }

    fun getOrderByStatus(ordersStatus: List<OrderStatus>): List<Order> {
        logger.debug("Retrieving all orders with status: {}", ordersStatus)
        return orderRepository.findByStatusInOrderByCreatedAtDesc(ordersStatus)
    }

    fun searchOrders(search: String?, status: String?, period: String?, page: Int, size: Int): Page<Order> {
        logger.debug("Searching orders with query: search : {}, status : {} period: {} page: {} size: {}", search, status, period, page, size)
        val query = createSearchQuery(search, status, period)

        val pageable: Pageable = of(page, size)
        query.with(pageable)

            val orders = mongoTemplate.find(query, Order::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Order::class.java)

        logger.info("Number of orders found for search '{}': {}", search, total)
        return PageImpl(orders, pageable, total)
    }

    private fun createSearchQuery(search: String?, status: String?, period: String?):Query {
        val query = Query()
        val criteriaList = mutableListOf<Criteria>()
        if (!search.isNullOrBlank()) {
            criteriaList.add(
                    Criteria().orOperator(
                            where("orderNumber").regex(search, "i"),
                            where("customerUserName").regex(search, "i"),
                            where("orderNumber").regex(search, "i")
                    )
            )
        }

        if (!status.isNullOrBlank()) {
            criteriaList.add(where("status").`is`(status))
        }
        if (!period.isNullOrBlank()) {
            val now = Instant.now()
            val zone = ZoneId.systemDefault()
            val startOfDay: Instant = LocalDate.now(zone).atStartOfDay(zone).toInstant()
            val startOfTomorrow: Instant = LocalDate.now(zone).plusDays(1).atStartOfDay(zone).toInstant()
            val periodCriteria = when (period) {
                "today" -> where("createdAt").gte(startOfDay).lt(startOfTomorrow)
                "week" -> where("createdAt").gte(now.minus(Duration.ofDays(7)))
                "month" -> where("createdAt").gte(now.minus(Duration.ofDays(30)))
                "quarter" -> where("createdAt").gte(now.minus(Duration.ofDays(90)))
                else -> null
            }
            periodCriteria?.let { criteriaList.add(it) }
        }

        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }
        return query
    }
    
    fun getOrderById(id: String): Order {
        logger.debug("Retrieving order with ID: {}", id)
        return orderRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Order not found with ID: $id")
    }

    fun createOrder(cart: CartDto): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.debug("Creating order with {} items for customer: {}", cart.items.size, currentUser)
        checkStockAvailability(cart)
        val validatedCart = validateCartItemsPricesWithBase(cart)

        val orderSubtotal = validatedCart.items.sumOf { BigDecimal(it.quantity) * it.productUnitPriceHT }
            .setScale(2, RoundingMode.DOWN)
        val orderTotalTva = validatedCart.items.sumOf { item ->
            val ligneHT = item.productUnitPriceHT.multiply(BigDecimal(item.quantity))
            ligneHT.multiply(item.productTaxRate)
        }.setScale(2, RoundingMode.DOWN)

        val totalBeforeShippingAmount = orderSubtotal.add(orderTotalTva)
            .setScale(2, RoundingMode.DOWN)
        val shippingAmount = shippingAmount(totalBeforeShippingAmount)
            .setScale(2, RoundingMode.DOWN)
        val totalTTCOrder = orderSubtotal.add(orderTotalTva).add(shippingAmount)
            .setScale(2, RoundingMode.DOWN)

        val order = Order(
            orderNumber = generateUniqueOrderNumber(),
            customerUserName = currentUser,
            status = PENDING,
            totalAmount = totalTTCOrder,
            subtotal = orderSubtotal,
            shippingAmount = shippingAmount,
            taxAmount = orderTotalTva,
            estimatedDeliveryDate = Instant.now().plus(Duration.ofDays(5)), // Livraison estimée à 5 jours
            notes = "Create new order from cart with ${cart.items.size} items",
            items = validatedCart.items.map {
                it.toOrderItem()
            },
            processedByUser = "SYSTEM",
            paymentMethod = PaymentMethod.PAYPAL,
            paymentStatus = PaymentStatus.PENDING,
        ).normalizeAmounts()

        // Create PayPal order
         val paymentOrderId =  payPalService.createOrder(totalTTCOrder.toString(), order.orderNumber)

        val savedOrder = orderRepository.save(order.copy(paymentOrderId = paymentOrderId))
        auditService.logAction(
            userName = currentUser,
            action = "CREATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Created order #${savedOrder.orderNumber} for ${savedOrder.totalAmount}€",
            newData = mapOf(
                "orderNumber" to savedOrder.orderNumber,
                "totalAmount" to savedOrder.totalAmount.toString(),
                "itemCount" to savedOrder.items.size.toString(),
                "customerUserName" to savedOrder.customerUserName,
                "status" to savedOrder.status.name,
                "paymentOrderId" to savedOrder.paymentOrderId.toString()
            )
        )

        logger.info("Order created successfully: #{} for {}€", savedOrder.orderNumber, savedOrder.totalAmount)
        return savedOrder
    }

    fun captureOrder(orderId: String): PaypalCapturedResponse {
        logger.debug("Capturing PayPal order with ID: {}", orderId)
        val captureResponse = payPalService.captureOrder(orderId)
        logger.info("PayPal order captured successfully: {}", captureResponse)
        val order = orderRepository.findByPaymentOrderId(orderId).firstOrNull() ?: throw ResourceNotFoundException("Order not found with ID: $orderId")
        val user = userService.getUserByUsername(order.customerUserName)
        val updatedOrder = order.copy(
            customerEmail = user.email,
            customerNumTel = user.numTel,
            status = CONFIRMED,
            processedByUser = "SYSTEM",
            shippingAddress = Address(
                street = user.address.street,
                city = user.address.city,
                postalCode = user.address.postalCode,
                country = user.address.country,
            ),
            billingAddress = Address(
                street = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.address_line_1 ?: "N/A",
                city = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.admin_area_2 ?: "N/A",
                postalCode = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.postal_code ?: "N/A",
                country = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.country_code ?: "N/A"
            ),
            paymentStatus = PaymentStatus.COMPLETED).normalizeAmounts()

        orderRepository.save(updatedOrder)

        // Publier l'événement de commande Confirmée
        applicationEventPublisher.publishEvent(
            OrderCompletedEvent(
                customerUserName = updatedOrder.customerUserName,
                orderId = updatedOrder.id!!,
                orderAmount = updatedOrder.totalAmount,
                orderItems = updatedOrder.items
            )
        )

        // Publier l'événement de génération de facture PDF et d'envoi par email
        val payerFullName = user.firstName + " " + user.lastName
        applicationEventPublisher.publishEvent(
            InvoiceGenerationRequestedEvent(
                order = updatedOrder,
                payerFullName = payerFullName,
                payerEmail = user.email
            )
        )

        auditService.logAction(
            userName = "SYSTEM",
            action = "PAYMENT_CAPTURED",
            entityType = "Order",
            entityId = updatedOrder.id,
            description = "Captured PayPal payment for order #${updatedOrder.orderNumber}",
            oldData = mapOf("status" to PENDING.name, "paymentStatus" to PaymentStatus.PENDING.name),
            newData = mapOf("status" to CONFIRMED.name, "paymentStatus" to PaymentStatus.COMPLETED.name)
        )
        return captureResponse.copy(orderNumber = updatedOrder.orderNumber)
    }
    
    fun getOrdersByCustomerUserName(): List<Order> {
        val customerName = SecurityContextHolder.getContext().authentication.name
        logger.debug("Retrieving orders for user : {}", customerName)
        return orderRepository.findByCustomerUserNameOrderByCreatedAtDesc(customerName)
    }

    fun getCustomerOrders(customerUserName:String): List<Order> {
        logger.debug("Retrieving orders for customer: {}", customerUserName)
        return orderRepository.findByCustomerUserNameOrderByCreatedAtDesc(customerUserName)
    }

    fun updateOrderStatus(orderId: String, newOrderStatus: OrderStatusUpdateRequest): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating order {} status to {} by user: {}", orderId, newOrderStatus.orderStatus, currentUser)
        
        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        val updatedOrder =  existingOrder.copy(status = newOrderStatus.orderStatus, processedByUser = currentUser).normalizeAmounts()
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Updated order #${savedOrder.orderNumber} status: $oldStatus -> ${newOrderStatus.orderStatus}",
            oldData = mapOf("status" to oldStatus.name),
            newData = mapOf("status" to newOrderStatus.orderStatus)
        )
        
        logger.info("Order status updated successfully: #{} -> {}", savedOrder.orderNumber, newOrderStatus.orderStatus)
        return savedOrder
    }
    
    fun cancelOrder(orderId: String ): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Cancelling order {} status to {} by user: {}", orderId, CANCELLED, currentUser)

        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        val updatedOrder =  existingOrder.copy(status = CANCELLED, processedByUser = "CUSTOMER: $currentUser").normalizeAmounts()
        val savedOrder = orderRepository.save(updatedOrder)

        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Updated order #${savedOrder.orderNumber} status: $oldStatus -> ${CANCELLED}",
            oldData = mapOf("status" to oldStatus.name),
            newData = mapOf("status" to CANCELLED.name, "notes" to  "Cancelled by user $currentUser")
        )

        logger.info("Order status cancelled successfully: #{} -> {}", savedOrder.orderNumber, CANCELLED)
        return savedOrder
    }

    private fun checkStockAvailability(cart: CartDto) {
        val productIds = cart.items.map { it.productId }
        val products = productRepository.findAllById(productIds).toList()
        cart.items.forEach { item ->
            val product = products.find { it.id == item.productId }
                ?: throw UnAvailableProductException("Produit ${item.productId} introuvable")

            if (product.stockQuantity < item.quantity) {
                throw UnAvailableProductException("Produit ${product.name} en rupture de stock")
            }
        }
    }

    private fun shippingAmount(cartTotalAmount: BigDecimal): BigDecimal {
        return if (cartTotalAmount >= FREE_SHIPPING_AMOUNT) {
            BigDecimal.ZERO.setScale(2, RoundingMode.DOWN)
        } else {
            BigDecimal.TEN.setScale(2, RoundingMode.DOWN)
        }
    }

    fun validateCartItemsPricesWithBase(cart: CartDto): CartDto {
        val productIds = cart.items.map { it.productId }
        val products = productRepository.findAllById(productIds).associateBy { it.id }

        // Vérification de cohérence des prix et taux de TVA
        cart.items.forEach { item ->
            val product = products[item.productId]
                ?: throw UnAvailableProductException("Produit ${item.productId} introuvable")
            val unitPrice = product.sellingPrice
            val productTaxRate = product.taxRate
            if (isValidPromotion(product)) {
                if (item.productUnitPriceHT.compareTo(product.promotionPrice) != 0) {
                    throw IllegalArgumentException("Le prix envoyé pour le produit ${item.productId} ne correspond pas au prix promotionnel en base.")
                }
            } else {
                if (item.productUnitPriceHT.compareTo(unitPrice) != 0) {
                    throw IllegalArgumentException("Le prix envoyé pour le produit ${item.productId} ne correspond pas au prix en base.")
                }
            }
            if (item.productTaxRate.compareTo(productTaxRate) != 0) {
                throw IllegalArgumentException("Le taux de TVA envoyé pour le produit ${item.productId} ne correspond pas au taux en base.")
            }
        }
        return cart
    }

    private fun generateUniqueOrderNumber(): String {
        return "ORD-${UUID.randomUUID()}"
    }

    fun isValidPromotion(product: Product): Boolean {
        return product.isOnPromotion &&
                product.promotionPrice != null &&
                product.promotionPrice > BigDecimal.ZERO &&
                product.promotionPrice < product.sellingPrice &&
                (product.promotionEndDate == null || product.promotionEndDate > Instant.now())
    }

    fun getDailySalesSummary(dailySalesRequestDto: DailySalesRequestDto): DailySalesResponseDto {
        logger.debug("Calculating daily sales summary for date: {}", dailySalesRequestDto.date)
        
        val localDate = dailySalesRequestDto.date.atZone(java.time.ZoneOffset.UTC).toLocalDate()
        val startOfDay = localDate.atStartOfDay(java.time.ZoneOffset.UTC).toInstant()
        val endOfDay = localDate.plusDays(1).atStartOfDay(java.time.ZoneOffset.UTC).toInstant()

        val query = Query().addCriteria(
                Criteria().orOperator(
                        Criteria("updatedAt").gte(startOfDay).lt(endOfDay),
                        Criteria("createdAt").gte(startOfDay).lt(endOfDay))
                .and("status").`in`(getSalesStatuses())
        )

        val orders = mongoTemplate.find(query, Order::class.java)
        val totalSales = orders.sumOf { it.totalAmount }.setScale(2, RoundingMode.DOWN)
        
        logger.info("Daily sales summary for {}: {}€ from {} orders", localDate, totalSales, orders.size)
        return DailySalesResponseDto(totalSales)
    }

    fun getMonthlyCancelledOrdersCount(startDate: Instant, endDate: Instant): Int {
        logger.debug("Calculating monthly cancelled orders count from {} to {}", startDate, endDate)
        val query = Query()
                .addCriteria(
                        Criteria().orOperator(
                                Criteria("updatedAt").gte(startDate).lt(endDate),
                                Criteria("createdAt").gte(startDate).lt(endDate))
                                .and("status").`is`(CANCELLED)
                )

        return mongoTemplate.count(query, Order::class.java).toInt()
    }

    fun getTopSellingProducts(limit: Int = 5): List<TopSellingProductDto> {
        logger.debug("Retrieving top {} selling products", limit)
        
        val orders = getValidatedOrders()
        val productStats = aggregateProductStatistics(orders)
        val topProducts = sortAndLimitProducts(productStats, limit)
        
        logger.info("Top {} selling products retrieved successfully", topProducts.size)
        return topProducts
    }
    
    private fun getValidatedOrders(): List<Order> {
        val query = Query().addCriteria(
            where("status").`in`(getSalesStatuses())
        )
        
        val orders = mongoTemplate.find(query, Order::class.java)
        logger.info("Found {} orders for top selling products analysis", orders.size)
        return orders
    }
    
    private fun getSalesStatuses() = listOf(
            CONFIRMED, PROCESSING, SHIPPED,
            DELIVERED, READY_FOR_PICKUP)
    
    private fun aggregateProductStatistics(orders: List<Order>): Map<String, TopSellingProductDto> {
        val productStats = mutableMapOf<String, TopSellingProductDto>()
        
        orders.forEach { order ->
            order.items.forEach { item ->
                productStats[item.productId] = updateProductStats(productStats[item.productId], item)
            }
        }
        
        return productStats
    }
    
    private fun updateProductStats(existing: TopSellingProductDto?, item: OrderItem): TopSellingProductDto {
        return if (existing != null) {
            updateExistingStats(existing, item)
        } else {
            createNewStats(item)
        }
    }
    
    private fun updateExistingStats(existing: TopSellingProductDto, item: OrderItem): TopSellingProductDto {
        val currentRevenue = BigDecimal(existing.totalRevenue.replace("€", ""))
        val newRevenue = currentRevenue.add(
            item.productUnitPrice.multiply(BigDecimal(item.quantity))
        ).setScale(2, RoundingMode.DOWN)
        
        return existing.copy(
            totalQuantitySold = existing.totalQuantitySold + item.quantity,
            totalRevenue = "${newRevenue}€",
            numberOfOrders = existing.numberOfOrders + 1
        )
    }
    
    private fun createNewStats(item: OrderItem): TopSellingProductDto {
        val revenue = item.productUnitPrice.multiply(BigDecimal(item.quantity))
            .setScale(2, RoundingMode.DOWN)
        
        return TopSellingProductDto(
            productId = item.productId,
            productName = item.productName,
            totalQuantitySold = item.quantity,
            totalRevenue = "${revenue}€",
            numberOfOrders = 1
        )
    }
    
    private fun sortAndLimitProducts(productStats: Map<String, TopSellingProductDto>, limit: Int): List<TopSellingProductDto> {
        return productStats.values
            .sortedByDescending { it.totalQuantitySold }
            .take(limit)
    }

    fun getMonthlyRevenue(startDate: Instant, endDate: Instant): BigDecimal {
        val query = Query()
                .addCriteria(
                        Criteria().orOperator(
                                Criteria("updatedAt").gte(startDate).lt(endDate),
                                Criteria("createdAt").gte(startDate).lt(endDate))
                                .and("status").`in`(getSalesStatuses())
                )

        val orders = mongoTemplate.find(query, Order::class.java)
        return orders.sumOf { it.totalAmount }.setScale(2, RoundingMode.DOWN)
    }

    fun getAverageOrderValue(): BigDecimal {
        val query = Query()
                .addCriteria(Criteria.where("status").`in`(getSalesStatuses()))

        val orders = mongoTemplate.find(query, Order::class.java)

        if (orders.isEmpty()) {
            return BigDecimal.ZERO
        }

        val totalRevenue = orders.sumOf { it.totalAmount }
        return totalRevenue.divide(BigDecimal(orders.size), 2, RoundingMode.DOWN)
    }
}

fun Order.normalizeAmounts(): Order {
    return this.copy(
        totalAmount = this.totalAmount.setScale(2, RoundingMode.DOWN),
        subtotal = this.subtotal.setScale(2, RoundingMode.DOWN),
        shippingAmount = this.shippingAmount.setScale(2, RoundingMode.DOWN),
        taxAmount = this.taxAmount.setScale(2, RoundingMode.DOWN),
        items = this.items.map { item ->
            item.copy(
                productUnitPrice = item.productUnitPrice.setScale(2, RoundingMode.DOWN),
                productTaxRate = item.productTaxRate.setScale(4, RoundingMode.DOWN)
            )
        }
    )
}
