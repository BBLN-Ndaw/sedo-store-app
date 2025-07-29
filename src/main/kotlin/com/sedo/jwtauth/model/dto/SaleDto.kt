package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.model.entity.SaleItem
import com.sedo.jwtauth.model.entity.PaymentMethod
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class SaleDto(
    val id: String? = null,
    val saleNumber: String? = null,
    val customerName: String? = null,

    @field:NotEmpty(message = "Sale must have at least one item")
    val items: List<SaleItem>,

    val paymentMethod: PaymentMethod,
    val subtotal: BigDecimal? = null,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val taxRate: BigDecimal? = null,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val totalAmount: BigDecimal? = null,
    val cashReceived: BigDecimal? = null,
    val changeAmount: BigDecimal? = null
)

data class CreateSaleRequest(
    val customerName: String? = null,

    @field:NotEmpty(message = "Sale must have at least one item")
    val items: List<SaleItemRequest>,

    val paymentMethod: PaymentMethod,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val cashReceived: BigDecimal? = null
)

data class SaleItemRequest(
    @field:NotNull(message = "Product ID is required")
    val productId: String,
    
    @field:NotNull(message = "Quantity is required")
    val quantity: Int,
    
    val discountAmount: BigDecimal = BigDecimal.ZERO
)
