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
        userName: String,
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
                userName = userName,
                action = action,
                entityType = entityType,
                entityId = entityId,
                description = description,
                oldData = oldData,
                newData = newData,
                ipAddress = ipAddress,
                userAgent = userAgent,
                createdAt = Instant.now()
            )
            
            auditLogRepository.save(auditLog)
            logger.debug("Audit log created for user {} action {}", userName, action)
        } catch (e: Exception) {
            logger.error("Failed to create audit log for user {} action {}: {}", userName, action, e.message)
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
            userId != null -> auditLogRepository.findByUserName(userId)
            entityType != null && entityId != null -> auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId)
            startDate != null && endDate != null -> auditLogRepository.findByCreatedAtBetween(startDate, endDate)
            else -> auditLogRepository.findAll()
        }
    }
}
