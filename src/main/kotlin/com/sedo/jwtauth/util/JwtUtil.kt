package com.sedo.jwtauth.util

import com.sedo.jwtauth.config.AuthProperties
import com.sedo.jwtauth.exception.JwtException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey
import io.jsonwebtoken.JwtException as JJwtException

@Component
class JwtUtil(val authProperties: AuthProperties) {
    private val logger = getLogger(JwtUtil::class.java)

    private val secretKeyBytes: SecretKey by lazy {
        Keys.hmacShaKeyFor(authProperties.secret.toByteArray())
    }

    private fun generateToken(username: String, roles: List<String>, expiration: Long): String {
        return try {
            Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(Date())
                .setExpiration(Date(System.currentTimeMillis() + expiration))
                .signWith(secretKeyBytes, SignatureAlgorithm.HS256)
                .compact()
        } catch (e: Exception) {
            logger.error("Error while generating token", e)
            throw JwtException("Error while generating token", e)
        }
    }

    fun generateAccessToken(username: String, roles: List<String>): String = generateToken(
        username, roles, authProperties.accessTokenExpiration
    )

    fun generateRefreshToken(username: String, roles: List<String>): String = generateToken(
        username, roles, authProperties.refreshTokenExpiration
    )


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
}
