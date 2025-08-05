package com.sedo.jwtauth.service

import com.sedo.jwtauth.constants.Constants.Cookie.ACCESS_TOKEN_MAX_AGE
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_ACCESS_TOKEN_NAME
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_MAX_AGE
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_NAME
import com.sedo.jwtauth.exception.InvalidCredentialsException
import com.sedo.jwtauth.exception.JwtException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

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

    fun createRefreshToken(refreshToken: String?, response: HttpServletResponse): LoginResponseDto {
        if(refreshToken.isNullOrEmpty()) {
            return LoginResponseDto(success = false, message = "REFRESH_TOKEN_MISSING")
        }
        val userName = jwtUtil.validateToken(refreshToken)
        val retrievedUser = retrieveUserOrThrow(userName)
        logger.info("Refresh token valid. Issuing new tokens for user: {}", retrievedUser.userName)
        return issueTokens(retrievedUser, response)
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

        response.addCookie(buildCookie(JWT_ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE))
        response.addCookie(buildCookie(JWT_REFRESH_TOKEN_NAME, refreshToken, JWT_REFRESH_TOKEN_MAX_AGE))

        return LoginResponseDto(success = true, message = "SUCCESS")
    }

    private fun buildCookie(name: String, value: String, maxAge: Int): Cookie {
        return Cookie(name, value).apply {
            isHttpOnly = true
            // secure = true // Uncomment when HTTPS is supported
            path = "/"
            this.maxAge = maxAge
            domain = "localhost"
            setAttribute("SameSite", "Strict")
        }
    }

    fun validateAccessToken(token: String): LoginResponseDto {
        return try {
            jwtUtil.validateToken(token)
            LoginResponseDto(success = true, message = "SUCCESS")
        } catch (ex: JwtException) {
            LoginResponseDto(success = false, message = "UNAUTHORIZED")
        }
    }

    fun checkLoginStatus(accessToken: String?): LoginResponseDto {
        return if (accessToken != null) {
            validateAccessToken(accessToken)
        } else {
            LoginResponseDto(success = false, message = "NO_TOKEN: USER_NOT_LOGGED_IN")
        }
    }


}
