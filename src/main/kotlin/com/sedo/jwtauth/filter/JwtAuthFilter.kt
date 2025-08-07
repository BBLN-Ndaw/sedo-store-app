package com.sedo.jwtauth.filter

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.exception.AuthenticationFailedException
import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JwtUtil,
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
            throw AuthenticationFailedException("No JWT token found in request")
        }

        try {
            authenticateWithToken(token, request)
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            logger.warn("JWT authentication failed: ${ex.message}")
            throw AuthenticationFailedException("JWT authentication failed: ${ex.message}")
        }
    }

    private fun isPublicEndpoint(servletPath: String): Boolean {
        val publicEndpoints = listOf(
            API + LOGIN,
            API + LOGOUT,
            API + REFRESH_TOKEN,
        )
        return publicEndpoints.contains(servletPath)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader("Authorization")
        return if (!authHeader.isNullOrBlank() && authHeader.startsWith("Bearer ")) {
            val token = authHeader.removePrefix("Bearer ").trim()
            logger.debug("JWT token found in header: $token")
            token
        } else {
            logger.warn("No valid Bearer token found in Authorization header")
            null
        }

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
}
