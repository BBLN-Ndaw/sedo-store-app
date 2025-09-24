package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.PasswordResetToken
import com.sedo.jwtauth.repository.PasswordResetTokenRepository
import org.slf4j.LoggerFactory
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class PasswordResetTokenService(
    private val passwordResetTokenRepository: PasswordResetTokenRepository
) {

    private val logger = getLogger(PasswordResetTokenService::class.java)

    companion object {
        private const val TOKEN_EXPIRY_HOURS = 24L
    }

    fun createPasswordResetToken(userId: String): String {
        logger.info("Creating password reset token for user ID: {}", userId)

        passwordResetTokenRepository.deleteByUserId(userId)

        val token = UUID.randomUUID().toString()
        val expiryDate = Instant.now().plusSeconds(TOKEN_EXPIRY_HOURS * 3600)

        val passwordResetToken = PasswordResetToken(
            token = token,
            userId = userId,
            expiryDate = expiryDate
        )

        passwordResetTokenRepository.save(passwordResetToken)
        logger.info("Password reset token created successfully for user ID: {}", userId)

        return token
    }

    fun validateToken(token: String): String? {
        logger.debug("Validating password reset token: {}", token)

        val passwordResetToken = passwordResetTokenRepository.findByToken(token)
            ?: run {
                logger.warn("Password reset token not found: {}", token)
                return null
            }
        if (passwordResetToken.used) {
            logger.warn("Password reset token already used: {}", token)
            return null
        }
        logger.info("Password reset token validated successfully: {}", token)
        return passwordResetToken.userId
    }

    fun markTokenAsUsed(token: String) {
        logger.info("Marking password reset token as used: {}", token)

        val passwordResetToken = passwordResetTokenRepository.findByToken(token)
        passwordResetToken?.let {
            val updatedToken = it.copy(used = true)
            passwordResetTokenRepository.save(updatedToken)
            logger.info("Password reset token marked as used: {}", token)
        }
    }
}
