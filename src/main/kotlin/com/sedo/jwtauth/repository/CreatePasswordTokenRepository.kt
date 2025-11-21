package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.CreatePasswordToken
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface CreatePasswordTokenRepository : MongoRepository<CreatePasswordToken, String> {
    fun findByToken(token: String): CreatePasswordToken?
    fun deleteByUserId(userId: String)
}
