package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.ADMIN
import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.HELLO
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.MANAGER
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.service.AuthService
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(API)
class AuthController @Autowired constructor(
    private val authService: AuthService
) {
    
    private val logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping(LOGIN)
    fun login(@Valid @RequestBody userDto: UserDto): ResponseEntity<String> {
        logger.info("Login attempt for user: {}", userDto.username)
        return authService.authenticate(userDto.toEntity())
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping(HELLO)
    fun hello(authentication: Authentication): ResponseEntity<String> {
        logger.debug("Hello endpoint called by: {}", authentication.name)
        return authentication.name
            .let { ResponseEntity.ok("Hello $it") }
    }

    @GetMapping(ADMIN)
    @PreAuthorize("hasAuthority('ADMIN')")
    fun admin(authentication: Authentication): ResponseEntity<String> {
        logger.info("Admin area accessed by: {}", authentication.name)
        return authentication.name
            .let { ResponseEntity.ok("Hello $it") }
    }

    @GetMapping(MANAGER)
    @PreAuthorize("hasAuthority('MANAGER')")
    fun manager(authentication: Authentication): ResponseEntity<String> {
        logger.info("Manager area accessed by: {}", authentication.name)
        return authentication.name
            .let { ResponseEntity.ok("Hello $it") }
    }
}
