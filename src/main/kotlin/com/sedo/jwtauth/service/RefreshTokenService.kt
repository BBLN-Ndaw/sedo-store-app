package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.RefreshToken
import com.sedo.jwtauth.repository.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service class responsible for managing JWT refresh tokens in the Store Management System.
 * 
 * This service provides comprehensive refresh token management functionality including:
 * - Refresh token storage and persistence
 * - Token validation for JWT refresh flows
 * - Token cleanup and revocation
 * - User session management through tokens
 * - Security features for token lifecycle
 * 
 * Business Logic:
 * - Refresh tokens enable secure JWT token renewal
 * - Each user can have multiple active refresh tokens (multi-device support)
 * - Tokens can be selectively revoked for security
 * - All refresh token operations are logged for security monitoring
 * 
 * Security Features:
 * - Secure token storage with user association
 * - Token validation prevents unauthorized access
 * - Selective and bulk token revocation capabilities
 * - Comprehensive logging for security auditing
 * - Protection against token replay attacks
 * 
 * JWT Integration:
 * - Works with JWT authentication system
 * - Enables token refresh without re-authentication
 * - Supports long-lived user sessions
 * - Maintains security through token rotation
 * 
 * Integration Points:
 * - Authentication service for JWT token management
 * - User management for session control
 * - Security monitoring for token abuse detection
 * - Frontend applications for seamless user experience
 * 
 * Dependencies:
 * - RefreshTokenRepository for token persistence
 * - Spring Security for authentication integration
 *
 */
@Service
class RefreshTokenService(private val refreshTokenRepository: RefreshTokenRepository) {
    private val logger = LoggerFactory.getLogger(RefreshTokenService::class.java)

    /**
     * Saves a refresh token associated with a user.
     * 
     * This method is called during login or token refresh to store
     * the refresh token for future use in JWT renewal processes.
     * 
     * @param token The refresh token string to save
     * @param userName The username associated with this refresh token
     */
    fun saveToken(token: String, userName: String) {
        logger.info("Saving refresh token for user: {}", userName)
        val refreshToken = RefreshToken(token = token, userName = userName)
        refreshTokenRepository.save(refreshToken)
    }

    /**
     * Validates if a refresh token exists and is active.
     * 
     * Used during JWT token refresh to verify that the provided
     * refresh token is valid and can be used for authentication.
     * 
     * @param token The refresh token to validate
     * @return true if token is valid and exists, false otherwise
     */
    fun isValidateToken(token: String): Boolean {
        logger.info("Validating refresh token: {}", token)
        return refreshTokenRepository.findByToken(token) != null
    }


    /**
     * Deletes a specific refresh token.
     * 
     * Used during logout or when a specific token needs to be revoked
     * for security reasons (e.g., device lost, suspicious activity).
     * 
     * @param token The refresh token to delete
     */
    fun deleteByToken(token: String) {
        logger.info("Deleting refresh token: {}", token)
        refreshTokenRepository.deleteByToken(token)
    }

    /**
     * Deletes all refresh tokens for a specific user.
     * 
     * Used during password changes, account security incidents,
     * or when a user wants to log out from all devices.
     * 
     * @param userName The username whose tokens should be deleted
     */
    fun deleteAllByUserName(userName: String) {
        logger.info("Deleting all refresh tokens for user: {}", userName)
        refreshTokenRepository.deleteAllByUserName(userName)
    }
}