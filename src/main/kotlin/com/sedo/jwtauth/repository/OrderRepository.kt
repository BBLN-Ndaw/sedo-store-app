package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Order
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String> {
    fun findByCustomerUserNameOrderByCreatedAtDesc(customerUsername: String): List<Order>
    fun findByPaymentOrderId(paymentOrderId: String): List<Order>
}
