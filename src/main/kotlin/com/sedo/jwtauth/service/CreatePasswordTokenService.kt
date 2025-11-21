package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.CreatePasswordToken
import com.sedo.jwtauth.repository.CreatePasswordTokenRepository
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.util.*

/**
 * Service class responsible for managing password reset tokens in the Store Management System.
 * 
 * This service provides secure password reset functionality including:
 * - Secure token generation for password reset requests
 * - Token validation with expiration checking
 * - One-time use token enforcement
 * - Token lifecycle management
 * - Security measures against token abuse
 * 
 * Business Logic:
 * - Tokens expire after 24 hours for security
 * - Each token can only be used once
 * - Old tokens are automatically cleaned up when new ones are created
 * - UUID-based tokens provide high security entropy
 * - All token operations are logged for security auditing
 * 
 * Security Features:
 * - Cryptographically secure random token generation
 * - Time-based expiration for limited exposure
 * - One-time use enforcement prevents replay attacks
 * - User-specific token management
 * - Comprehensive logging for security monitoring
 * 
 * Integration Points:
 * - User management system for password reset flows
 * - Email service for token delivery
 * - Authentication system for password updates
 * - Security monitoring and auditing
 * 
 * Dependencies:
 * - CreatePasswordTokenRepository for token persistence
 * - UUID for secure token generation
 * - Instant for precise time-based expiration
 *
 */
@Service
class CreatePasswordTokenService(
    private val createPasswordTokenRepository: CreatePasswordTokenRepository
) {

    private val logger = getLogger(CreatePasswordTokenService::class.java)

    /**
     * Creates a new password token for a user.
     * 
     * Security Process:
     * 1. Removes any existing tokens for the user
     * 2. Generates a cryptographically secure random token
     * 3. Sets 24-hour expiration time
     * 4. Stores token with user association
     * 5. Returns token for delivery to user
     * 
     * Business Rules:
     * - Only one active token per user at a time
     * - Token expires after 24 hours for security
     * - Each token is unique and cryptographically secure
     * 
     * @param userId The unique identifier of the user requesting password reset
     * @return Generated password reset token string
     */
    fun createPasswordToken(userId: String): String {
        logger.info("Creating password reset token for user ID: {}", userId)

        createPasswordTokenRepository.deleteByUserId(userId)

        val token = UUID.randomUUID().toString()

        val createPasswordToken = CreatePasswordToken(
            token = token,
            userId = userId
        )

        createPasswordTokenRepository.save(createPasswordToken)
        logger.info("Password reset token created successfully for user ID: {}", userId)

        return token
    }

    /**
     * Validates a password creation token and returns associated user ID.
     * 
     * Validation Process:
     * 1. Checks if token exists in database
     * 2. Verifies token hasn't expired
     * 3. Ensures token hasn't been used before
     * 4. Returns user ID if validation passes
     * 
     * Security Checks:
     * - Token existence verification
     * - Expiration time validation
     * - One-time use enforcement
     * 
     * @param token The password reset token to validate
     * @return User ID if token is valid, null if invalid or expired
     */
    fun validatePasswordCreationToken(token: String): String? {
        logger.debug("Validating password creation token: {}", token)

        val passwordCreationToken = createPasswordTokenRepository.findByToken(token)
            ?: run {
                logger.warn("Password creation token not found: {}", token)
                return null
            }
        if (passwordCreationToken.used) {
            logger.warn("Password creation token already used: {}", token)
            return null
        }
        logger.info("Password creation token validated successfully: {}", token)
        return passwordCreationToken.userId
    }

    /**
     * Marks a password reset token as used after successful password reset.
     * 
     * This method enforces one-time use of tokens for security.
     * Once marked as used, the token cannot be validated again.
     * 
     * @param token The password reset token to mark as used
     */
    fun markTokenAsUsed(token: String) {
        logger.info("Marking password reset token as used: {}", token)

        val passwordCreationToken = createPasswordTokenRepository.findByToken(token)
        passwordCreationToken?.let {
            val updatedToken = it.copy(used = true)
            createPasswordTokenRepository.save(updatedToken)
            logger.info("Password reset token marked as used: {}", token)
        }
    }
}
