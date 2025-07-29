package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.AuditLog
import com.sedo.jwtauth.repository.AuditLogRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuditService(
    private val auditLogRepository: AuditLogRepository
) {
    
    private val logger = LoggerFactory.getLogger(AuditService::class.java)
    
    fun logAction(
        userId: String,
        action: String,
        entityType: String,
        entityId: String? = null,
        description: String,
        oldData: Map<String, Any>? = null,
        newData: Map<String, Any>? = null,
        ipAddress: String? = null,
        userAgent: String? = null
    ) {
        try {
            val auditLog = AuditLog(
                userId = userId,
                action = action,
                entityType = entityType,
                entityId = entityId,
                description = description,
                oldData = oldData,
                newData = newData,
                ipAddress = ipAddress,
                userAgent = userAgent,
                timestamp = Instant.now()
            )
            
            auditLogRepository.save(auditLog)
            logger.debug("Audit log created for user {} action {}", userId, action)
        } catch (e: Exception) {
            logger.error("Failed to create audit log for user {} action {}: {}", userId, action, e.message)
        }
    }
    
    fun getAuditLogs(
        userId: String? = null,
        entityType: String? = null,
        entityId: String? = null,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): List<AuditLog> {
        return when {
            userId != null -> auditLogRepository.findByUserId(userId)
            entityType != null && entityId != null -> auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId)
            startDate != null && endDate != null -> auditLogRepository.findByTimestampBetween(startDate, endDate)
            else -> auditLogRepository.findAll()
        }
    }
}
