package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.PasswordResetToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenRepository : MongoRepository<PasswordResetToken, String> {
    fun findByToken(token: String): PasswordResetToken?
    fun deleteByUserId(userId: String)
}
