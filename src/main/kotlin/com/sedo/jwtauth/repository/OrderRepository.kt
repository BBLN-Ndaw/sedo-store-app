package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Order
import com.sedo.jwtauth.model.entity.OrderStatus
import com.sedo.jwtauth.model.entity.PaymentMethod
import org.springframework.data.mongodb.repository.MongoRepository

interface OrderRepository : MongoRepository<Order, String> {
    fun findByCustomerUserNameOrderByCreatedAtDesc(customerUsername: String): List<Order>
    fun findByStatusOrderByCreatedAtDesc(status: OrderStatus): List<Order>
    fun findAllByOrderByCreatedAtDesc(): List<Order>
    fun findByPaymentOrderId(paymentOrderId: String): List<Order>
    fun findByProcessedByUserOrderByUpdatedAtDesc(processedByUser: String): List<Order>
    fun findByPaymentMethodOrderByCreatedAtDesc(paymentMethod: PaymentMethod): List<Order>
}
