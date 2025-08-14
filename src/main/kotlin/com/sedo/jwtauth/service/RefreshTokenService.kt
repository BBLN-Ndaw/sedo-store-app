package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.RefreshToken
import com.sedo.jwtauth.repository.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(private val refreshTokenRepository: RefreshTokenRepository) {
    private val logger = LoggerFactory.getLogger(RefreshTokenService::class.java)

    fun saveToken(token: String, userName: String) {
        logger.info("Saving refresh token for user: {}", userName)
        val refreshToken = RefreshToken(token = token, userName = userName)
        refreshTokenRepository.save(refreshToken)
    }

    fun isValidateToken(token: String): Boolean {
        logger.info("Validating refresh token: {}", token)
        return refreshTokenRepository.findByToken(token) != null
    }


    fun deleteByToken(token: String) {
        logger.info("Deleting refresh token: {}", token)
        refreshTokenRepository.deleteByToken(token)
    }

    fun deleteAllByUserName(userName: String) {
        logger.info("Deleting all refresh tokens for user: {}", userName)
        refreshTokenRepository.deleteAllByUserName(userName)
    }
}