package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.model.entity.OrderItem
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.PaymentMethod
import com.sedo.jwtauth.model.entity.PaymentStatus
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.Instant

data class OrderDto(
    val id: String? = null,
    val orderNumber: String? = null,
    
    @field:NotNull(message = "Customer ID is required")
    val customerId: String,
    
    @field:NotEmpty(message = "Order must have at least one item")
    val items: List<OrderItem>,
    
    val status: OrderStatus = OrderStatus.PENDING,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val subtotal: BigDecimal? = null,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val totalAmount: BigDecimal? = null,
    val notes: String? = null,
    val pickupDate: Instant? = null
)

data class CreateOrderRequest(
    @field:NotEmpty(message = "Order must have at least one item")
    val items: List<OrderItemRequest>,
    
    val paymentMethod: PaymentMethod,
    val notes: String? = null,
    val pickupDate: Instant? = null
)

data class OrderItemRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: String,
    
    @field:NotNull(message = "Quantity is required")
    val quantity: Int
)
