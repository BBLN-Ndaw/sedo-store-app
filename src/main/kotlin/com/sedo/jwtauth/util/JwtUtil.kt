package com.sedo.jwtauth.util

import com.sedo.jwtauth.exception.JwtException
import com.sedo.jwtauth.exception.NoTokenInAuthHeaderException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException as JJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory.getLogger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey
import kotlin.text.startsWith
import kotlin.text.substring

@Component
class JwtUtil {
    private val logger = getLogger(JwtUtil::class.java)
    
    @Value("\${jwt.secret:myDefaultSecretKeyForJwtTokenGeneration1234567890}")
    private lateinit var secretKey: String
    
    @Value("\${jwt.expiration:3600000}")
    private var expiration: Long = 3600000 // 1h by default
    
    private val secretKeyBytes: SecretKey by lazy {
        Keys.hmacShaKeyFor(secretKey.toByteArray())
    }

    fun generateToken(username: String, roles: List<String>): String {
        return try {
            Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + expiration))
                .signWith(secretKeyBytes, SignatureAlgorithm.HS256)
                .compact()
        } catch (e: Exception) {
            throw JwtException("Error while generating token", e)
        }
    }

    fun validateToken(token: String): String {
        return try {
            val claims: Claims = Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
                .body
            claims.subject
        } catch (e: JJwtException) {
            throw JwtException("Invalid JWT Token", e)
        } catch (e: Exception) {
            throw JwtException("Error while validating token", e)
        }
    }
    fun isValidToken(token: String): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKeyBytes)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: JJwtException) {
            logger.warn("Invalid JWT Token: {}", e.message)
            false
        } catch (e: Exception) {
            logger.error("Error while validating token: {}", e.message)
            false
        }
    }
}
