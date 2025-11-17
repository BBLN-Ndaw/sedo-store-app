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

/**
 * Utility class for JWT (JSON Web Token) operations.
 *
 * This component handles all JWT-related functionality including token generation,
 * validation, and claims extraction. It uses HMAC SHA-256 algorithm for token signing
 * and provides both access and refresh token capabilities.
 *
 * Features:
 * - Access token generation with user roles
 * - Refresh token generation for token renewal
 * - Token validation and expiration checking
 * - Claims extraction (username, roles, expiration)
 * - Secure token signing with configurable secret key
 *
 * @property authProperties Configuration properties for JWT settings
 *
 */
@Component
class JwtUtil(val authProperties: AuthProperties) {
    private val logger = getLogger(JwtUtil::class.java)

    private val secretKeyBytes: SecretKey by lazy {
        Keys.hmacShaKeyFor(authProperties.secret.toByteArray())
    }

    /**
     * Generates a JWT token with specified expiration time.
     *
     * @param username The username to include in the token subject
     * @param roles List of user roles to include in token claims
     * @param expiration Token expiration time in milliseconds
     * @return Generated JWT token string
     * @throws JwtException if token generation fails
     */
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

    /**
     * Generates an access token for user authentication.
     *
     * @param username The username for token subject
     * @param roles List of user roles for authorization
     * @return Generated access token with configured expiration
     */
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
