package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.entity.AuditLog
import com.sedo.jwtauth.service.AuditService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.time.Instant

/**
 * REST Controller for comprehensive audit logging and compliance tracking.
 *
 * This controller provides administrative access to audit trails and system
 * activity logs, essential for security monitoring, compliance reporting,
 * and forensic analysis. The audit system tracks all significant operations
 * performed within the store management system.
 *
 * Audit logs capture detailed information about:
 * - User actions and authentication events
 * - Entity modifications (create, update, delete operations)
 * - System access patterns and security events
 * - Data changes with before/after snapshots
 * - Timestamp and user attribution for all activities
 *
 * @property auditService Service layer for audit log retrieval and analysis
 *
 */
@RestController
@RequestMapping("/api/audit")
class AuditController(
    private val auditService: AuditService
) {
    
    /**
     * Retrieves all audit logs for a specific user's activities.
     *
     * This endpoint provides comprehensive activity tracking for individual
     * users, essential for security monitoring, user behavior analysis,
     * and compliance auditing. Logs include all actions performed by the
     * specified user across the entire system.
     *
     * @param userName Username of the user whose audit logs to retrieve
     * @return ResponseEntity containing list of AuditLog objects for the user
     * @throws UserNotFoundException if user with given username doesn't exist
     *
     * Security: Requires ADMIN role for access to user audit trails
     */
    @GetMapping("/user/{userName}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByUser(@PathVariable userName: String): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(userName = userName))
    }
    
    /**
     * Retrieves audit logs for a specific entity's lifecycle.
     *
     * This endpoint tracks all operations performed on a particular entity
     * (such as products, orders, users), providing complete change history
     * and modification tracking for individual business objects.
     *
     * @param entityType Type of entity (e.g., "Product", "Order", "User")
     * @param entityId Unique identifier of the specific entity instance
     * @return ResponseEntity containing list of AuditLog objects for the entity
     * @throws EntityNotFoundException if specified entity doesn't exist
     *
     * Security: Requires ADMIN role for access to entity audit trails
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByEntity(
        @PathVariable entityType: String,
        @PathVariable entityId: String
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(entityType = entityType, entityId = entityId))
    }
    
    /**
     * Retrieves audit logs within a specified date range.
     *
     * This endpoint enables time-based audit log analysis, essential for
     * compliance reporting, security incident investigation, and periodic
     * activity reviews. Supports flexible date range queries with precise
     * timestamp filtering.
     *
     * @param startDate Beginning of the date range (ISO 8601 format)
     * @param endDate End of the date range (ISO 8601 format)
     * @return ResponseEntity containing list of AuditLog objects within the date range
     * @throws InvalidDateRangeException if date range is invalid or too broad
     *
     * Security: Requires ADMIN role for access to time-based audit queries
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAuditLogsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: Instant,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: Instant
    ): ResponseEntity<List<AuditLog>> {
        return ResponseEntity.ok(auditService.getAuditLogs(startDate = startDate, endDate = endDate))
    }
}
