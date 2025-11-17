package com.sedo.jwtauth.service

import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_MAX_AGE
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_NAME
import com.sedo.jwtauth.exception.InvalidCredentialsException
import com.sedo.jwtauth.exception.RefreshTokenFailedException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

/**
 * Service class for handling authentication operations.
 *
 * This service manages user authentication, JWT token generation and validation,
 * refresh token operations, and user logout functionality. It integrates with
 * the JWT utility and refresh token service to provide secure authentication.
 *
 * @property userRepository Repository for user data access
 * @property refreshTokenService Service for managing refresh tokens
 * @property jwtUtil Utility for JWT token operations
 * @property passwordEncoder Encoder for password verification
 *
 */
@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    
    private val logger = getLogger(AuthService::class.java)

    /**
     * Authenticates a user with username and password.
     *
     * Verifies user credentials and generates JWT tokens upon successful authentication.
     * Sets refresh token as HTTP-only cookie for security.
     *
     * @param user Login credentials containing username and password
     * @param response HTTP response for setting cookies
     * @return LoginResponseDto containing access token and user information
     * @throws InvalidCredentialsException if password is incorrect
     * @throws UserNotFoundException if user doesn't exist
     */
    fun authenticate(user: LoginUserDto, response: HttpServletResponse): LoginResponseDto {
        logger.info("Authentication attempt for user: {}", user.username)

        val retrievedUser = retrieveUserOrThrow(user.username)

        if (!passwordEncoder.matches(user.password, retrievedUser.password))  {
            logger.warn("Incorrect password for user: {}", user.username)
            throw InvalidCredentialsException()
        }
        logger.info("Authentication successful for user: {}", user.username)
        return issueTokens(retrievedUser, response)
    }

    /**
     * Refreshes JWT access token using a valid refresh token.
     *
     * Validates the provided refresh token and issues new JWT tokens if valid.
     * Invalidates the old refresh token for security.
     *
     * @param refreshToken The refresh token to validate
     * @param response HTTP response for setting new cookies
     * @return LoginResponseDto containing new access token and user information
     * @throws RefreshTokenFailedException if refresh token is invalid or expired
     */
    fun refreshToken(refreshToken: String, response: HttpServletResponse): LoginResponseDto {
        return if(refreshTokenService.isValidateToken(refreshToken)) {
            val userName = jwtUtil.validateToken(refreshToken)
            refreshTokenService.deleteAllByUserName(userName)
            val retrievedUser = retrieveUserOrThrow(userName)
            logger.info("Refresh token valid. Issuing new tokens for user: {}", retrievedUser.userName)
             issueTokens(retrievedUser, response)
        }
        else{
            logger.warn("revoked refresh token provided: {}", refreshToken)
            throw RefreshTokenFailedException("revoked refresh token provided")
        }
    }

    fun logout(refreshToken: String, response: HttpServletResponse): LoginResponseDto {
            logger.info("Logging out user by invalidating refresh token and clearing cookies")
            refreshTokenService.deleteByToken(refreshToken)
            val refreshCookie = buildCookie(JWT_REFRESH_TOKEN_NAME, "", 0)
            response.addCookie(refreshCookie)
            return LoginResponseDto(success = true)
    }

    private fun retrieveUserOrThrow(username: String): User {
        return userRepository.findByUserName(username)
            ?: run {
                logger.warn("User not found: {}", username)
                throw UserNotFoundException(username)
            }
    }

    private fun issueTokens(user: User, response: HttpServletResponse): LoginResponseDto {
        val accessToken = jwtUtil.generateAccessToken(user.userName, user.roles)
        val refreshToken = jwtUtil.generateRefreshToken(user.userName, user.roles)
        refreshTokenService.saveToken(token = refreshToken, userName = user.userName)

        response.addCookie(buildCookie(JWT_REFRESH_TOKEN_NAME, refreshToken, JWT_REFRESH_TOKEN_MAX_AGE))

        return LoginResponseDto(success = true, token = accessToken)
    }

    private fun buildCookie(name: String, value: String, maxAge: Int): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            secure = false // Explicitement désactivé pour le développement local
            path = "/"
            this.maxAge = maxAge
            setAttribute("SameSite", "Lax")
        }
    }
}
