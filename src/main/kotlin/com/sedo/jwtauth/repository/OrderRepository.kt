package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface OrderRepository : MongoRepository<Order, String> {
    fun findByCustomerIdOrderByCreatedAtDesc(customerId: String): List<Order>
    fun findByStatusOrderByCreatedAtDesc(status: OrderStatus): List<Order>
    fun findAllByOrderByCreatedAtDesc(): List<Order>
}
