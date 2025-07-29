package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "audit_logs")
data class AuditLog(
    @Id
    val id: String? = null,
    
    val userId: String,
    
    val action: String, // CREATE, UPDATE, DELETE, LOGIN, LOGOUT, etc.
    
    val entityType: String, // Product, Order, Sale, etc.
    
    val entityId: String? = null,
    
    val description: String,
    
    val oldData: Map<String, Any>? = null,
    
    val newData: Map<String, Any>? = null,
    
    val ipAddress: String? = null,
    
    val userAgent: String? = null,
    
    @field:CreatedDate
    val timestamp: Instant? = null
)
