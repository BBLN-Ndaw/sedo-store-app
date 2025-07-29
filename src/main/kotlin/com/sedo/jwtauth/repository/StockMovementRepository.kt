package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.StockMovement
import com.sedo.jwtauth.model.entity.MovementType
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface StockMovementRepository : MongoRepository<StockMovement, String> {
    fun findByProductId(productId: String): List<StockMovement>
    fun findByType(type: MovementType): List<StockMovement>
    fun findByPerformedBy(performedBy: String): List<StockMovement>
    fun findByCreatedAtBetween(start: Instant, end: Instant): List<StockMovement>
}
