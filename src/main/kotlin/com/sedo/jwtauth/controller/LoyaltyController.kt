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

@RestController
@RequestMapping(API)
class LoyaltyController(private val loyaltyNotificationListener: LoyaltyNotificationListener) {

    @GetMapping("$LOYALTY/{customerUserName}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getUserLoyalty(@PathVariable customerUserName: String): ResponseEntity<LoyaltyProgramDto> {
        val loyalty = loyaltyNotificationListener.getUserLoyalty(customerUserName)
        return ResponseEntity.ok(loyalty)
    }

    @GetMapping("$LOYALTY/my-program")
    fun getMyLoyaltyProgram(authentication: Authentication): ResponseEntity<LoyaltyProgramDto> {
        val customerUserName = authentication.name
        val loyalty = loyaltyNotificationListener.getUserLoyalty(customerUserName)
        return ResponseEntity.ok(loyalty)
    }
}