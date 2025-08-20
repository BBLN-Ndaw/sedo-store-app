package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.OrderStatus.*
import com.sedo.jwtauth.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(OrderService::class.java)
    
    fun getAllOrders(): List<Order> {
        logger.debug("Retrieving all orders")
        return orderRepository.findAllByOrderByCreatedAtDesc()
    }
    
    fun getOrderById(id: String): Order {
        logger.debug("Retrieving order with ID: {}", id)
        return orderRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Order not found with ID: $id")
    }
    
    fun getOrdersByStatus(status: OrderStatus): List<Order> {
        logger.debug("Retrieving orders with status: {}", status)
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
    }
    
    fun getOrdersByCustomerName(): List<Order> {
        val customerName = SecurityContextHolder.getContext().authentication.name
        logger.debug("Retrieving orders for customer: {}", customerName)
        return orderRepository.findByCustomerNameOrderByCreatedAtDesc(customerName)
    }
    
    fun getPendingOrders(): List<Order> {
        return getOrdersByStatus(OrderStatus.PENDING)
    }
    
    fun getReadyForPickupOrders(): List<Order> {
        return getOrdersByStatus(READY_FOR_PICKUP)
    }
    
    fun createOrder(order: Order): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating order with {} items for customer: {}", order.items.size, order.customerName)
        
        // Vérifier le stock pour tous les articles
//        val orderItems = mutableListOf<OrderItem>()
//        var subtotal = BigDecimal.ZERO
//
//        for (itemDto in order.items) {
//            val product = productService.getProductById(itemDto.productId)
//
//            // Vérifier le stock disponible
//            if (product.stockQuantity < itemDto.quantity) {
//                throw InsufficientStockException(
//                    "Insufficient stock for product ${product.name}. Available: ${product.stockQuantity}, Requested: ${itemDto.quantity}"
//                )
//            }
//
//            val unitPrice = product.sellingPrice
//            val itemTotal = unitPrice.multiply(BigDecimal(itemDto.quantity))
//
//            val orderItem = OrderItem(
//                productId = itemDto.productId,
//                productName = product.name,
//                quantity = itemDto.quantity,
//                unitPrice = unitPrice,
//                totalPrice = itemTotal
//            )
//
//            orderItems.add(orderItem)
//            subtotal = subtotal.add(itemTotal)
//        }
//
//        // Calculer les taxes
//        val taxAmount = subtotal.multiply(BigDecimal("0.20")) // 20% TVA
//        var totalAmount = subtotal.add(taxAmount)
//        val shippingAmount = if(totalAmount> BigDecimal("50.00")) BigDecimal.ZERO else BigDecimal("5.00") // Livraison gratuite au-dessus de 50€
//        totalAmount = totalAmount.add(shippingAmount)
//        // Créer la commande
//        val order = Order(
//            orderNumber = generateOrderNumber(),
//            customerName = order.customerName,
//            items = orderItems,
//            subtotal = subtotal,
//            taxAmount = taxAmount,
//            totalAmount = totalAmount,
//            paymentMethod = order.paymentMethod,
//            paymentStatus = order.paymentStatus,
//            notes = order.notes,
//            pickupDate = order.pickupDate,
//            status = OrderStatus.PENDING,
//            shippingAmount = shippingAmount,
//            shippingAddress = order.shippingAddress,
//            billingAddress = order.billingAddress,
//            processedByUser = null,
//        )
        
        val savedOrder = orderRepository.save(order)
        
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
                "customerName" to savedOrder.customerName,
                "status" to savedOrder.status.name
            )
        )
        
        logger.info("Order created successfully: #{} for {}€", savedOrder.orderNumber, savedOrder.totalAmount)
        return savedOrder
    }

    fun updateOrder(orderId: String, newOrder: Order): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating order {} status to {} by user: {}", orderId, newOrder.status, currentUser)
        
        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        val updatedOrder =  existingOrder.copy(
            orderNumber = newOrder.orderNumber,
            customerName = newOrder.customerName,
            items = newOrder.items,
            subtotal = newOrder.subtotal,
            taxAmount = newOrder.taxAmount,
            totalAmount = newOrder.totalAmount,
            paymentMethod = newOrder.paymentMethod,
            paymentStatus = newOrder.paymentStatus,
            notes = newOrder.notes,
            pickupDate = newOrder.pickupDate,
            status = newOrder.status,
            shippingAmount = newOrder.shippingAmount,
            shippingAddress = newOrder.shippingAddress,
            billingAddress = newOrder.billingAddress,
            processedByUser = currentUser,
            estimatedDeliveryDate = newOrder.estimatedDeliveryDate,

        )
//        val updatedOrder = when (newStatus) {
//            CONFIRMED, PROCESSING, READY_FOR_PICKUP, SHIPPED, DELIVERED, CANCELLED -> existingOrder.copy(
//                status = newStatus,
//                notes = notes ?: existingOrder.notes,
//                processedByUser = currentUser
//            )
//            PENDING -> existingOrder.copy(
//                status = newStatus,
//                notes = notes ?: existingOrder.notes,
//                processedByUser = "SYSTEM"
//            )
//        }
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Updated order #${savedOrder.orderNumber} status: $oldStatus -> ${newOrder.status}",
            oldData = mapOf("status" to oldStatus.name),
            newData = mapOf("status" to newOrder.status.name, "notes" to (newOrder.notes ?: "")),
        )
        
        logger.info("Order status updated successfully: #{} -> {}", savedOrder.orderNumber, newOrder.status)
        return savedOrder
    }
    
    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "ORD-$timestamp-$random"
    }
    
    fun cancelOrder(orderId: String ): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating order {} status to {} by user: {}", orderId, CANCELLED, currentUser)

        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        val updatedOrder =  existingOrder.copy(status = CANCELLED)
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

        logger.info("Order status updated successfully: #{} -> {}", savedOrder.orderNumber, CANCELLED)
        return savedOrder
    }
    
    fun getOrderStats(): Map<String, Any> {
        val allOrders = orderRepository.findAll()
        
        val statusCounts = OrderStatus.values().associateWith { status ->
            allOrders.count { it.status == status }
        }
        
        val totalOrders = allOrders.size
        val totalRevenue = allOrders.filter { it.status == DELIVERED }
            .sumOf { it.totalAmount }
        
        val averageOrderValue = if (totalOrders > 0) {
            allOrders.sumOf { it.totalAmount }.divide(BigDecimal(totalOrders))
        } else BigDecimal.ZERO
        
        return mapOf(
            "totalOrders" to totalOrders,
            "totalRevenue" to totalRevenue,
            "averageOrderValue" to averageOrderValue,
            "statusBreakdown" to statusCounts,
            "pendingOrders" to (statusCounts[OrderStatus.PENDING] ?: 0),
            "readyForPickup" to (statusCounts[READY_FOR_PICKUP] ?: 0)
        )
    }
}
