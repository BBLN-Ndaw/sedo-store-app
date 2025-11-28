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
    val orderNumber: String? = null,

    val customerName: String? = null,

    val customerEmail: String? = null,

    val customerNumTel: String? = null,

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
    val shippingAddress: Address? = null,

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

    val paymentOrderId: String? = null, // ID de la transaction de paiement associée

    val createdAt: Instant? = null
)