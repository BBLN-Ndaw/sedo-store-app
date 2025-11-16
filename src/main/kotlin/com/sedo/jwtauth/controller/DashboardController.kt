package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
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
 * Contrôleur pour les statistiques du tableau de bord
 */
@RestController
@RequestMapping("$API/dashboard")
class DashboardController(
    private val dashboardService: DashboardService,
) {
    
    /**
     * Endpoint to recover dashboard statistics
     * 
     * @return ResponseEntity containing the dashboard statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDashboardStatistics(): ResponseEntity<StatDto> {
        val statistics = dashboardService.getDashboardStatistics()
        return ResponseEntity.ok(statistics)
    }
    
    /**
     * Endpoint to recover recent dashboard notifications
     * 
     * @return ResponseEntity containing the list of dashboard notifications
     */
    @GetMapping("/notifications")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDashboardNotifications(): ResponseEntity<List<DashboardNotificationDto>> {
        val notifications = dashboardService.generateRealTimeNotifications()
        return ResponseEntity.ok(notifications)
    }
}