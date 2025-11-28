package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.constants.Constants.Endpoints.CREATE_PASSWORD
import com.sedo.jwtauth.constants.Constants.Endpoints.VALIDATE_TOKEN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.model.dto.CreatePasswordDto
import com.sedo.jwtauth.service.AuthService
import com.sedo.jwtauth.service.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for handling authentication-related operations.
 *
 * This controller manages user authentication, token operations, and password management.
 * It provides endpoints for login, logout, token refresh, and password setup operations.
 *
 * @property authService Service for handling authentication operations
 * @property userService Service for handling user-related operations
 *
 */
@RestController
@RequestMapping(API)
class AuthController(
    private val authService: AuthService,
    private val userService: UserService
) {

    /**
     * Authenticates a user and returns JWT tokens.
     *
     * @param userDto User credentials for authentication
     * @param response HTTP response for setting cookies
     * @return ResponseEntity containing login response with JWT tokens
     */
    @PostMapping(LOGIN)
    fun login(@Valid @RequestBody userDto: LoginUserDto, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.authenticate(userDto, response)
            .let {ResponseEntity.ok(it) }
    }

    /**
     * Refreshes JWT access token using refresh token.
     *
     * @param refreshToken Refresh token from cookie
     * @param response HTTP response for setting new cookies
     * @return ResponseEntity containing new JWT tokens
     */
    @PostMapping(REFRESH_TOKEN)
    fun refreshToken(@CookieValue(value = "refresh_token") refreshToken: String, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.refreshToken(refreshToken, response)
            .let {ResponseEntity.ok(it) }
    }


    /**
     * Logs out a user by invalidating the refresh token.
     *
     * @param refreshToken Refresh token from cookie to be invalidated
     * @param response HTTP response for clearing cookies
     * @return ResponseEntity containing logout confirmation
     */
    @PostMapping(LOGOUT)
    fun logout(@CookieValue(value = "refresh_token") refreshToken: String, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
            return authService.logout(refreshToken, response)
                .let { ResponseEntity.ok(it) }
    }

    /**
     * Creates or resets a user's password using a reset token.
     *
     * @param createPasswordDto DTO containing the reset token and new password
     * @return ResponseEntity containing success message
     */
    @PostMapping(CREATE_PASSWORD)
    fun createPassword(@Valid @RequestBody createPasswordDto: CreatePasswordDto): ResponseEntity<Map<String, String>> {
        userService.setPasswordWithToken(createPasswordDto.token, createPasswordDto.password)
        return ResponseEntity.ok(mapOf("message" to "Mot de passe définit avec succès"))
    }

    /**
     * Validates a password reset token and redirects to front end page for updating or setting password.
     *
     * @param token Password reset token to validate
     * @param response HTTP response for redirection
     */
    @GetMapping(VALIDATE_TOKEN)
    fun validateTokenAndRedirect(@RequestParam token: String, response: HttpServletResponse) {
        val redirectUrl = userService.validateTokenAndGetRedirectUrl(token)
        response.sendRedirect(redirectUrl)
    }
}
