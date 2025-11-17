package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.DASHBOARD
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.model.dto.DashboardNotificationDto
import com.sedo.jwtauth.model.dto.StatDto
import com.sedo.jwtauth.service.DashboardService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for dashboard analytics and real-time monitoring.
 *
 * This controller provides comprehensive dashboard functionality for the store
 * management system, including business statistics, key performance indicators (KPIs),
 * and real-time notifications for administrative users.
 *
 * The dashboard serves as the central monitoring hub for business operations,
 * providing insights into sales, inventory, customer activity, and system health.
 *
 * @property dashboardService Service layer for dashboard data aggregation and analytics
 *
 * @author Store Management System
 * @since 1.0
 */
@RestController
@RequestMapping(DASHBOARD)
class DashboardController(
    private val dashboardService: DashboardService,
) {
    
    /**
     * Retrieves comprehensive dashboard statistics and key performance indicators.
     *
     * This endpoint provides aggregated business metrics including:
     * - Total sales and revenue figures
     * - Product inventory levels
     * - Customer activity metrics
     * - Order processing statistics
     * - Performance trends and comparisons
     *
     * @return ResponseEntity containing StatDto with comprehensive dashboard metrics
     *
     * Security: Requires ADMIN or EMPLOYEE role for access to business analytics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDashboardStatistics(): ResponseEntity<StatDto> {
        val statistics = dashboardService.getDashboardStatistics()
        return ResponseEntity.ok(statistics)
    }
    
    /**
     * Retrieves real-time dashboard notifications and alerts.
     *
     * This endpoint provides up-to-date notifications about:
     * - Low inventory warnings
     * - Pending order alerts
     * - System status notifications
     * - Customer service requests
     * - Performance anomalies
     *
     * Notifications are dynamically generated based on current system state
     * and configured business rules.
     *
     * @return ResponseEntity containing list of DashboardNotificationDto objects
     *
     * Security: Requires ADMIN or EMPLOYEE role for access to system notifications
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDashboardNotifications(): ResponseEntity<List<DashboardNotificationDto>> {
        val notifications = dashboardService.generateRealTimeNotifications()
        return ResponseEntity.ok(notifications)
    }
}