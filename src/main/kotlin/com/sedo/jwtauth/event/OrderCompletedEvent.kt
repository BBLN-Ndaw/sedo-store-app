package com.sedo.jwtauth.event

import com.sedo.jwtauth.model.entity.OrderItem
import java.math.BigDecimal

/**
 * Event published when an order is successfully completed.
 *
 * This event triggers various business processes including loyalty point
 * calculation, invoice generation, and notification sending. It carries
 * essential order information needed by event listeners.
 *
 * @property customerUserName Username of the customer who placed the order
 * @property orderId Unique identifier of the completed order
 * @property orderAmount Total amount of the order for loyalty calculations
 * @property orderItems List of items in the order for detailed processing
 *
 */
data class OrderCompletedEvent(
    val customerUserName: String,
    val orderId: String,
    val orderAmount: BigDecimal,
    val orderItems: List<OrderItem>
)
