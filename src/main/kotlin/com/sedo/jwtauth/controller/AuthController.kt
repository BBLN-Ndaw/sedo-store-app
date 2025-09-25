package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.constants.Constants.Endpoints.SET_PASSWORD
import com.sedo.jwtauth.constants.Constants.Endpoints.VALIDATE_TOKEN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.model.dto.SetPasswordDto
import com.sedo.jwtauth.service.AuthService
import com.sedo.jwtauth.service.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(API)
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
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

    @PostMapping(SET_PASSWORD)
    fun setPassword(@Valid @RequestBody setPasswordDto: SetPasswordDto): ResponseEntity<Map<String, String>> {
        userService.setPasswordWithToken(setPasswordDto.token, setPasswordDto.password)
        return ResponseEntity.ok(mapOf("message" to "Mot de passe définit avec succès"))
    }

    @GetMapping(VALIDATE_TOKEN)
    fun validateTokenAndRedirect(@RequestParam token: String, response: HttpServletResponse) {
        val redirectUrl = userService.validateTokenAndGetRedirectUrl(token)
        response.sendRedirect(redirectUrl)
    }
}
