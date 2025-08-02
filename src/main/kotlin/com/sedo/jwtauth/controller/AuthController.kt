package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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

    @PostMapping("/refresh-token")
    fun refreshToken(@CookieValue refresh_token: String?, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        if(refresh_token != null )
            return authService.createRefreshToken( refresh_token, response)
                .let { ResponseEntity.ok(it) }
        return ResponseEntity.ok(LoginResponseDto(false, "REJECTED"))
    }
}
