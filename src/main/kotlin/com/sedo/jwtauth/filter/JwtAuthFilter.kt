package com.sedo.jwtauth.filter

import com.sedo.jwtauth.constants.Constants.Cookie.JWT_ACCESS_TOKEN_NAME
import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_NAME
import com.sedo.jwtauth.exception.AuthenticationFailedException
import com.sedo.jwtauth.exception.NoTokenException
import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    private val logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        logger.debug("Processing request: ${request.method} ${request.requestURI}")

        val token = extractTokenFromRequest(request)

        token?.let {
            try {
                authenticateWithToken(it, request)
            } catch (ex: Exception) {
                logger.warn("JWT authentication failed: ${ex.message}")
                throw AuthenticationFailedException()
            }
        } ?: run {
            logger.debug("No JWT token found in request")
           throw NoTokenException()
        }

        filterChain.doFilter(request, response)
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
}
