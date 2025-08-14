package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.service.AuthService
import jakarta.servlet.http.HttpServletResponse
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
    fun login(@Valid @RequestBody userDto: LoginUserDto, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.authenticate(userDto, response)
            .let {ResponseEntity.ok(it) }
    }

    @PostMapping(REFRESH_TOKEN)
    fun refreshToken(@CookieValue(value = "refresh_token") refreshToken: String, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.refreshToken(refreshToken, response)
            .let {ResponseEntity.ok(it) }
    }


    @PostMapping(LOGOUT)
    fun logout(@CookieValue(value = "refresh_token") refreshToken: String, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
            return authService.logout(refreshToken, response)
                .let { ResponseEntity.ok(it) }
    }
}
