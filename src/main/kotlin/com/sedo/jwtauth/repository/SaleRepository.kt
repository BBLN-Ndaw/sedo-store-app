package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Sale
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface SaleRepository : MongoRepository<Sale, String> {
    fun findByProcessedBy(processedBy: String): List<Sale>
    fun findByCreatedAtBetween(start: Instant, end: Instant): List<Sale>
    fun findByCustomerName(customerId: String): List<Sale>
}
