package com.sedo.jwtauth.service

import Product
import com.sedo.jwtauth.constants.Constants.Order.FREE_SHIPPING_AMOUNT
import com.sedo.jwtauth.exception.UnAvailableProductException
import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.mapper.toOrderItem
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.dto.CartDto
import com.sedo.jwtauth.model.dto.PaypalCapturedResponse
import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.OrderStatus.*
import com.sedo.jwtauth.model.entity.PaymentMethod
import com.sedo.jwtauth.model.entity.PaymentStatus
import com.sedo.jwtauth.repository.OrderRepository
import com.sedo.jwtauth.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Duration
import java.time.Instant
import java.util.UUID

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val productRepository: ProductRepository,
    private val auditService: AuditService,
    private val payPalService: PayPalService
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

    fun createOrder(cart: CartDto): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.debug("Creating order with {} items for customer: {}", cart.items.size, currentUser)
        checkStockAvailability(cart)
        val validatedCart = validateCartItemsPricesWithBase(cart)

        val orderSubtotal = validatedCart.items.sumOf { BigDecimal(it.quantity) * it.productUnitPriceHT }
        val orderTotalTva = validatedCart.items.sumOf { item ->
            val ligneHT = item.productUnitPriceHT.multiply(BigDecimal(item.quantity))
            ligneHT.multiply(item.productTaxRate)
        }

        val totalBeforeShippingAmount = orderSubtotal.add(orderTotalTva)
        val shippingAmount = shippingAmount(totalBeforeShippingAmount)
        val totalTTCOrder = orderSubtotal.add(orderTotalTva).add(shippingAmount).setScale(2, RoundingMode.DOWN)

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
            processedByUser = currentUser,
            paymentMethod = PaymentMethod.PAYPAL,
            paymentStatus = PaymentStatus.PENDING,
        )

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
        val updatedOrder = order.copy(
            customerEmail = captureResponse.payer.email_address,
            status = CONFIRMED,
            shippingAddress = Address(
                street = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.address_line_1 ?: "N/A",
                city = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.admin_area_2 ?: "N/A",
                postalCode = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.postal_code ?: "N/A",
                country = captureResponse.purchase_units.firstOrNull()?.shipping?.address?.country_code ?: "N/A",
            ),
            billingAddress = Address(
                street = captureResponse.payer.address.address_line_1 ?: "N/A",
                city = captureResponse.payer.address.admin_area_2 ?: "N/A",
                postalCode = captureResponse.payer.address.postal_code ?: "N/A",
                country = captureResponse.payer.address.country_code
            ),
            paymentStatus = PaymentStatus.COMPLETED)

        orderRepository.save(updatedOrder)

        auditService.logAction(
            userName = "SYSTEM",
            action = "PAYMENT_CAPTURED",
            entityType = "Order",
            entityId = updatedOrder.id,
            description = "Captured PayPal payment for order #${updatedOrder.orderNumber}",
            oldData = mapOf("status" to PENDING.name, "paymentStatus" to PaymentStatus.PENDING.name),
            newData = mapOf("status" to CONFIRMED.name, "paymentStatus" to PaymentStatus.COMPLETED.name)
        )
        return captureResponse

    }
    
    fun getOrdersByStatus(status: OrderStatus): List<Order> {
        logger.debug("Retrieving orders with status: {}", status)
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
    }
    
    fun getOrdersByCustomerUserName(): List<Order> {
        val customerName = SecurityContextHolder.getContext().authentication.name
        logger.debug("Retrieving orders for customer: {}", customerName)
        return orderRepository.findByCustomerUserNameOrderByCreatedAtDesc(customerName)
    }
    
    fun getPendingOrders(): List<Order> {
        return getOrdersByStatus(PENDING)
    }
    
    fun getReadyForPickupOrders(): List<Order> {
        return getOrdersByStatus(READY_FOR_PICKUP)
    }

    fun updateOrderStatus(orderId: String, newOrderStatus: OrderStatus): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating order {} status to {} by user: {}", orderId, newOrderStatus.name, currentUser)
        
        val existingOrder = getOrderById(orderId)
        val oldStatus = existingOrder.status
        val updatedOrder =  existingOrder.copy(status = newOrderStatus)
        
        val savedOrder = orderRepository.save(updatedOrder)
        
        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Order",
            entityId = savedOrder.id,
            description = "Updated order #${savedOrder.orderNumber} status: $oldStatus -> ${newOrderStatus.name}",
            oldData = mapOf("status" to oldStatus.name),
            newData = mapOf("status" to newOrderStatus.name)
        )
        
        logger.info("Order status updated successfully: #{} -> {}", savedOrder.orderNumber, newOrderStatus.name)
        return savedOrder
    }
    
    fun cancelOrder(orderId: String ): Order {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Cancelling order {} status to {} by user: {}", orderId, CANCELLED, currentUser)

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

        logger.info("Order status cancelled successfully: #{} -> {}", savedOrder.orderNumber, CANCELLED)
        return savedOrder
    }
    
    fun getOrderStats(): Map<String, Any> {
        val allOrders = orderRepository.findAll().toList()
        
        val statusCounts = OrderStatus.entries.associateWith { status ->
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
            "pendingOrders" to (statusCounts[PENDING] ?: 0),
            "readyForPickup" to (statusCounts[READY_FOR_PICKUP] ?: 0)
        )
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
        return if (cartTotalAmount >= FREE_SHIPPING_AMOUNT) BigDecimal.ZERO else BigDecimal.TEN
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
}
