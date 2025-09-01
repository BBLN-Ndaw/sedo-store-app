package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.constants.Constants.Order.TAX
import java.math.BigDecimal
import java.time.Instant

data class CartDto(
    val id: String? = null,
    val items: List<CartItemDto>,
    val itemCount: Int,
    val createdAt: Instant,
    val updatedAt: Instant,
)
data class CartItemDto(
    val id: String? = null,
    val productId: String,
    val productName: String,
    val productSku: String,
    val productUnitPriceHT: BigDecimal,
    val productTaxRate: BigDecimal = TAX,
    val quantity: Int,
    val productMaxQuantity: Int,
    val imageUrl: String,
    val categoryName: String,
)
