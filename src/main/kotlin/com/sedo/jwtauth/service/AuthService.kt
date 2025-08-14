package com.sedo.jwtauth.service

import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_MAX_AGE
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_NAME
import com.sedo.jwtauth.exception.AuthenticationFailedException
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
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val refreshTokenService: RefreshTokenService,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    
    private val logger = getLogger(AuthService::class.java)

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

    fun refreshToken(refreshToken: String?, response: HttpServletResponse): LoginResponseDto {
        if(refreshToken.isNullOrEmpty()) {
            return LoginResponseDto(success = false)
        }
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
