package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.model.entity.OrderItem
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.PaymentMethod
import com.sedo.jwtauth.model.entity.PaymentStatus
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant


data class OrderDto(

    val id: String? = null,

    @field:NotBlank(message = "Order number cannot be blank")
    val orderNumber: String,

    @field:NotBlank(message = "Customer name is required")
    val customerName: String,

    @field:NotNull(message = "Order status is required")
    val status: OrderStatus,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be positive")
    val totalAmount: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "Subtotal must be positive")
    val subtotal: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "Shipping amount must be positive or zero")
    val shippingAmount: BigDecimal,

    @field:DecimalMin(value = "0.0", inclusive = true, message = "Tax amount must be positive or zero")
    val taxAmount: BigDecimal = BigDecimal.ZERO,

    @field:NotNull(message = "Shipping address is required")
    val shippingAddress: Address,

    val billingAddress: Address? = null,

    @field:FutureOrPresent(message = "Estimated delivery date must be in the future")
    val estimatedDeliveryDate: Instant? = null,

    @field:Size(max = 500, message = "Notes cannot exceed 500 characters")
    val notes: String? = null,

    @field:NotEmpty(message = "Order must contain at least one item")
    val items: List<OrderItem>,

    @field:PastOrPresent(message = "Pickup date cannot be in the future")
    val pickupDate: Instant? = null,

    val processedByUser: String? = null,

    @field:NotNull(message = "Payment method is required")
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,

    @field:NotNull(message = "Payment status is required")
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,

    val createdAt: Instant? = null
)

data class UpdateOrderDto(val newStatus: OrderStatus, val orderId: String)


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
