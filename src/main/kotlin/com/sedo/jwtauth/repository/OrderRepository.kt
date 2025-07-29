package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface OrderRepository : MongoRepository<Order, String> {
    fun findByCustomerId(customerId: String): List<Order>
    fun findByStatus(status: OrderStatus): List<Order>
    fun findByCreatedAtBetween(start: Instant, end: Instant): List<Order>
    fun findByProcessedBy(processedBy: String): List<Order>
}
