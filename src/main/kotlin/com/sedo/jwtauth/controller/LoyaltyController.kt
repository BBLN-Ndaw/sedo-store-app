package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOYALTY
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.model.dto.LoyaltyProgramDto
import com.sedo.jwtauth.eventListener.LoyaltyNotificationListener
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST Controller for managing customer loyalty programs and rewards.
 *
 * This controller provides comprehensive loyalty program functionality,
 * enabling customers to view their loyalty status, points balance, and
 * reward history. The loyalty system incentivizes customer retention
 * through points accumulation and reward redemption.
 *
 * The controller serves both customer-facing endpoints for personal
 * loyalty information and administrative endpoints for customer service
 * and loyalty program management.
 *
 * @property loyaltyNotificationListener Service component for loyalty program operations
 *
 */
@RestController
@RequestMapping(API)
class LoyaltyController(private val loyaltyNotificationListener: LoyaltyNotificationListener) {

    /**
     * Retrieves loyalty program information for a specific customer.
     *
     * This administrative endpoint allows staff to view any customer's
     * loyalty program status, including points balance, tier level,
     * recent activity, and available rewards. Used for customer service
     * and loyalty program administration.
     *
     * @param customerUserName Username of the customer whose loyalty info is requested
     * @return ResponseEntity containing LoyaltyProgramDto with customer loyalty details
     *
     * Security: Requires ADMIN or EMPLOYEE role for accessing customer loyalty data
     */
    @GetMapping("$LOYALTY/{customerUserName}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getUserLoyalty(@PathVariable customerUserName: String): ResponseEntity<LoyaltyProgramDto> {
        val loyalty = loyaltyNotificationListener.getUserLoyalty(customerUserName)
        return ResponseEntity.ok(loyalty)
    }

    /**
     * Retrieves the authenticated customer's personal loyalty program information.
     *
     * This customer-facing endpoint provides access to the user's own loyalty
     * program status, including:
     * - Current points balance
     * - Loyalty tier and benefits
     * - Points earning history
     * - Available rewards and redemption options
     * - Progress toward next tier
     *
     * @param authentication Spring Security authentication object containing user details
     * @return ResponseEntity containing LoyaltyProgramDto with user's loyalty information
     *
     * Security: Accessible to any authenticated customer for their own data
     */
    @GetMapping("$LOYALTY/my-program")
    fun getMyLoyaltyProgram(authentication: Authentication): ResponseEntity<LoyaltyProgramDto> {
        val customerUserName = authentication.name
        val loyalty = loyaltyNotificationListener.getUserLoyalty(customerUserName)
        return ResponseEntity.ok(loyalty)
    }
}