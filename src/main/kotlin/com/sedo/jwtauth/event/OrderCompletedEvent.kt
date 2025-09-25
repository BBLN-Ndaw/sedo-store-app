package com.sedo.jwtauth.event

import java.math.BigDecimal

data class OrderCompletedEvent(
    val customerUserName: String,
    val orderId: String,
    val orderAmount: BigDecimal,
)
