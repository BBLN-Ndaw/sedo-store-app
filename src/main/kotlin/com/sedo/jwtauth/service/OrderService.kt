package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.InsufficientStockException
import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.dto.OrderDto
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderItem
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.repository.OrderRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productService: ProductService,
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
    
    fun getOrdersByCustomer(customerId: String): List<Order> {
        logger.debug("Retrieving orders for customer: {}", customerId)
        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
    }
    
    fun getPendingOrders(): List<Order> {
        return getOrdersByStatus(OrderStatus.PENDING)
    }
    
    fun getReadyForPickupOrders(): List<Order> {
        return getOrdersByStatus(OrderStatus.READY_FOR_PICKUP)
    }
    
    fun createOrder(orderDto: OrderDto): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating order with {} items for customer: {}", orderDto.items.size, orderDto.customerId)
        
        // Vérifier le stock pour tous les articles
        val orderItems = mutableListOf<OrderItem>()
        var subtotal = BigDecimal.ZERO
        
        for (itemDto in orderDto.items) {
            val product = productService.getProductById(itemDto.productId)
            
            // Vérifier le stock disponible
            if (product.stockQuantity < itemDto.quantity) {
                throw InsufficientStockException(
                    "Insufficient stock for product ${product.name}. Available: ${product.stockQuantity}, Requested: ${itemDto.quantity}"
                )
            }
            
            val unitPrice = product.sellingPrice
            val itemTotal = unitPrice.multiply(BigDecimal(itemDto.quantity))
            
            val orderItem = OrderItem(
                productId = itemDto.productId,
                productName = product.name,
                quantity = itemDto.quantity,
                unitPrice = unitPrice,
                totalPrice = itemTotal
            )
            
            orderItems.add(orderItem)
            subtotal = subtotal.add(itemTotal)
        }
        
        // Calculer les taxes
        val taxAmount = subtotal.multiply(BigDecimal("0.20")) // 20% TVA
        val totalAmount = subtotal.add(taxAmount)
        
        // Créer la commande
        val order = Order(
            orderNumber = generateOrderNumber(),
            customerId = orderDto.customerId,
            customerName = "Customer", // TODO: récupérer depuis User
            items = orderItems,
            subtotal = subtotal,
            taxAmount = taxAmount,
            totalAmount = totalAmount,
            paymentMethod = orderDto.paymentMethod,
            paymentStatus = orderDto.paymentStatus,
            notes = orderDto.notes,
            pickupDate = orderDto.pickupDate,
            status = OrderStatus.PENDING,
            processedBy = currentUser
        )
        
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
                "customerId" to savedOrder.customerId,
                "status" to savedOrder.status.name
            )
        )
        
        logger.info("Order created successfully: #{} for {}€", savedOrder.orderNumber, savedOrder.totalAmount)
        return savedOrder
    }
    
    fun updateOrderStatus(orderId: String, newStatus: OrderStatus, notes: String? = null): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating order {} status to {} by user: {}", orderId, newStatus, currentUser)
        
        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        
        val updatedOrder = when (newStatus) {
            OrderStatus.CONFIRMED -> existingOrder.copy(
                status = newStatus,
                confirmedAt = Instant.now(),
                confirmedBy = currentUser,
                notes = notes ?: existingOrder.notes,
                updatedAt = Instant.now()
            )
            OrderStatus.PREPARING -> existingOrder.copy(
                status = newStatus,
                preparingAt = Instant.now(),
                preparingBy = currentUser,
                notes = notes ?: existingOrder.notes,
                updatedAt = Instant.now()
            )
            OrderStatus.READY_FOR_PICKUP -> existingOrder.copy(
                status = newStatus,
                readyAt = Instant.now(),
                readyBy = currentUser,
                notes = notes ?: existingOrder.notes,
                updatedAt = Instant.now()
            )
            OrderStatus.COMPLETED -> {
                // Réserver le stock lors de la completion
                reserveStock(existingOrder)
                existingOrder.copy(
                    status = newStatus,
                    completedAt = Instant.now(),
                    completedBy = currentUser,
                    notes = notes ?: existingOrder.notes,
                    updatedAt = Instant.now()
                )
            }
            OrderStatus.CANCELLED -> {
                existingOrder.copy(
                    status = newStatus,
                    cancelledAt = Instant.now(),
                    cancelledBy = currentUser,
                    cancelReason = notes ?: "Cancelled by staff",
                    updatedAt = Instant.now()
                )
            }
            else -> existingOrder.copy(
                status = newStatus,
                notes = notes ?: existingOrder.notes,
                updatedAt = Instant.now()
            )
        }
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Updated order #${savedOrder.orderNumber} status: $oldStatus -> $newStatus",
            oldData = mapOf("status" to oldStatus.name),
            newData = mapOf("status" to newStatus.name, "notes" to (notes ?: ""))
        )
        
        logger.info("Order status updated successfully: #{} -> {}", savedOrder.orderNumber, newStatus)
        return savedOrder
    }
    
    private fun reserveStock(order: Order) {
        logger.info("Reserving stock for completed order: #{}", order.orderNumber)
        
        for (item in order.items) {
            val product = productService.getProductById(item.productId)
            val newQuantity = product.stockQuantity - item.quantity
            productService.updateStock(item.productId, newQuantity, "Order #${order.orderNumber} completed")
        }
    }
    
    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = (1000..9999).random()
        return "ORD-$timestamp-$random"
    }
    
    fun cancelOrder(orderId: String, reason: String): Order {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED, reason)
    }
    
    fun getOrderStats(): Map<String, Any> {
        val allOrders = orderRepository.findAll()
        
        val statusCounts = OrderStatus.values().associateWith { status ->
            allOrders.count { it.status == status }
        }
        
        val totalOrders = allOrders.size
        val totalRevenue = allOrders.filter { it.status == OrderStatus.COMPLETED }
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
            "readyForPickup" to (statusCounts[OrderStatus.READY_FOR_PICKUP] ?: 0)
        )
    }
}
