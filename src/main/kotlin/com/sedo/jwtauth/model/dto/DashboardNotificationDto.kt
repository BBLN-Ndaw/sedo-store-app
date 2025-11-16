package com.sedo.jwtauth.model.dto

import java.math.BigDecimal

/**
 * DTO to represent a notification on the dashboard
 */
data class DashboardNotificationDto(
    val id: Long,
    val type: NotificationType,
    val message: String,
    val time: String,
    val amount: BigDecimal? = null,
    val userId: Long? = null,
    val userName: String? = null
)

/**
 * Notification types for dashboard notifications
 */
enum class NotificationType(val value: String) {
    SALE("sale"),
    ORDER("order"),
    STOCK("stock"),
    CUSTOMER("customer")
}