package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.AuditLog
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.Instant

interface AuditLogRepository : MongoRepository<AuditLog, String> {
    fun findByUserName(userId: String): List<AuditLog>
    fun findByEntityTypeAndEntityId(entityType: String, entityId: String): List<AuditLog>
    fun findByTimestampBetween(start: Instant, end: Instant): List<AuditLog>
}
