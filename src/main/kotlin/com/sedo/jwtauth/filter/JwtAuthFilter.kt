package com.sedo.jwtauth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_ACCESS_TOKEN_NAME
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_NAME
import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.CHECK_LOGIN
import com.sedo.jwtauth.model.dto.ErrorResponseDto
import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.LocalDateTime

@Component
class JwtAuthFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JwtUtil,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("Processing request: ${request.method} ${request.requestURI}")

        // Skip JWT validation for public endpoints
        if (isPublicEndpoint(request.servletPath)) {
            filterChain.doFilter(request, response)
            return
        }

        val token = extractTokenFromRequest(request)

        if (token == null) {
            logger.debug("No JWT token found in request")
            sendErrorResponse(
                response, 
                HttpStatus.UNAUTHORIZED, 
                "Authentication Required", 
                "Authentication token is required",
                request.requestURI
            )
            return
        }

        try {
            authenticateWithToken(token, request)
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            logger.warn("JWT authentication failed: ${ex.message}")
            sendErrorResponse(
                response, 
                HttpStatus.UNAUTHORIZED, 
                "Authentication Failed", 
                "Invalid or expired token",
                request.requestURI
            )
        }
    }

    private fun isPublicEndpoint(servletPath: String): Boolean {
        val publicEndpoints = listOf(
            API + LOGIN,
            API + LOGOUT,
            API + CHECK_LOGIN
        )
        return publicEndpoints.contains(servletPath)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        if (authHeader?.startsWith("Bearer ") == true) {
            logger.debug("JWT token found in Authorization header")
            return authHeader.removePrefix("Bearer ").trim()
        }

        val cookies = request.cookies ?: return null

        cookies.firstOrNull { it.name == JWT_ACCESS_TOKEN_NAME }?.let {
            logger.debug("JWT access token found in cookie")
            return it.value
        }

        cookies.firstOrNull { it.name == JWT_REFRESH_TOKEN_NAME }?.let {
            logger.debug("JWT refresh token found in cookie")
            return it.value
        }

        return null
    }

    private fun authenticateWithToken(token: String, request: HttpServletRequest) {
        val username = jwtUtil.validateToken(token)
        logger.debug("Valid JWT for user: ${username}")

        val userDetails = userDetailsService.loadUserByUsername(username)
        val authentication = UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.authorities
        ).apply {
            details = WebAuthenticationDetailsSource().buildDetails(request)
        }

        SecurityContextHolder.getContext().authentication = authentication
        logger.debug("Authentication set for user: ${username}")
    }

    private fun sendErrorResponse(
        response: HttpServletResponse,
        status: HttpStatus,
        error: String,
        message: String,
        path: String
    ) {
        response.status = status.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = ErrorResponseDto(
            error = error,
            message = message,
            status = status.value(),
            path = path,
            timestamp = LocalDateTime.now()
        )

        try {
            val jsonResponse = objectMapper.writeValueAsString(errorResponse)
            response.writer.write(jsonResponse)
            response.writer.flush()
        } catch (e: Exception) {
            logger.error("Error writing error response", e)
        }
    }
}
