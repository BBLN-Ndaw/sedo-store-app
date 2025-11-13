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
    
    @GetMapping("/user/{userName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByUser(@PathVariable userName: String): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(userName = userName))
    }
    
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByEntity(
        @PathVariable entityType: String,
        @PathVariable entityId: String
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(entityType = entityType, entityId = entityId))
    }
    
    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Instant
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(startDate = startDate, endDate = endDate))
    }
}
