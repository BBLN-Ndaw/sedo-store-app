package com.sedo.jwtauth.model.dto

import java.math.BigDecimal
import java.time.Instant

data class TopSellingProductDto(
    val productId: String,
    val productName: String,
    val totalQuantitySold: Int,
    val totalRevenue: String,
    val numberOfOrders: Int
)

data class DailySalesResponseDto(val value: BigDecimal)
data class DailySalesRequestDto(val date: Instant)