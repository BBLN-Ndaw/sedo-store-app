package com.sedo.jwtauth.filter

import com.sedo.jwtauth.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
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
    
    private val jwtLogger: Logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        jwtLogger.debug("Processing request: {} {}", request.method, request.requestURI)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)
            jwtLogger.debug("JWT token found, validating...")
            
            try {
                val username = jwtUtil.validateToken(token)
                jwtLogger.debug("Valid token for user: {}", username)
                
                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.authorities
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
                
                jwtLogger.debug("Authentication set for user: {}", username)
            } catch (e: Exception) {
                jwtLogger.warn("Invalid JWT token: {}", e.message)
            }
        } else {
            jwtLogger.debug("No JWT token found in request")
        }
        
        filterChain.doFilter(request, response)
    }
}
