package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.ADMIN
import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.HELLO
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.EMPLOYEE
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
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
    fun login(@Valid @RequestBody userDto: LoginUserDto): ResponseEntity<LoginResponseDto> {
        logger.info("Login attempt for user: {}", userDto.username)
        return authService.authenticate(userDto)
            .let { ResponseEntity.ok(LoginResponseDto(it)) }
    }
}
