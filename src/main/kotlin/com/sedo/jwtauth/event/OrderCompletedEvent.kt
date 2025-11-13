package com.sedo.jwtauth.event

import com.sedo.jwtauth.model.entity.OrderItem
import java.math.BigDecimal

data class OrderCompletedEvent(
    val customerUserName: String,
    val orderId: String,
    val orderAmount: BigDecimal,
    val orderItems: List<OrderItem>
)
