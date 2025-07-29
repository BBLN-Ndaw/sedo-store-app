package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.entity.AuditLog
import com.sedo.jwtauth.service.AuditService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@RequestMapping("/api/audit")
class AuditController(
    private val auditService: AuditService
) {
    
    @GetMapping
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAllAuditLogs(): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs())
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAuditLogsByUser(@PathVariable userId: String): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(userId = userId))
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAuditLogsByEntity(
        @PathVariable entityType: String,
        @PathVariable entityId: String
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(entityType = entityType, entityId = entityId))
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAuditLogsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Instant
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(startDate = startDate, endDate = endDate))
    }
    
    @GetMapping("/actions")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAvailableActions(): ResponseEntity<List<String>> {
        val actions = listOf(
            "CREATE", "UPDATE", "DELETE", "LOGIN", "LOGOUT",
            "STOCK_UPDATE", "STATUS_UPDATE", "SALE", "ORDER"
        )
        return ResponseEntity.ok(actions)
    }
    
    @GetMapping("/entity-types")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAvailableEntityTypes(): ResponseEntity<List<String>> {
        val entityTypes = listOf(
            "User", "Category", "Supplier", "Product", 
            "Sale", "Order", "StockMovement"
        )
        return ResponseEntity.ok(entityTypes)
    }
}
