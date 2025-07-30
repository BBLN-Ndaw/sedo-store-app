package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.model.dto.ValidatedTokentDto
import com.sedo.jwtauth.service.AuthService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(API)
class AuthController @Autowired constructor(
    private val authService: AuthService
) {

    @PostMapping(LOGIN)
    fun login(@Valid @RequestBody userDto: LoginUserDto): ResponseEntity<LoginResponseDto> {
        return authService.authenticate(userDto)
            .let { ResponseEntity.ok(LoginResponseDto(it)) }
    }

    @GetMapping("/validate-token")
    fun validateToken(@RequestHeader("Authorization") authHeader: String): ResponseEntity<ValidatedTokentDto> {
        val token = authHeader.removePrefix("Bearer ").trim()
        return authService.getValidatedToken(token)
            .let { ResponseEntity.ok(it) }
    }
}
