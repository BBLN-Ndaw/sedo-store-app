package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.RefreshToken
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository : MongoRepository<RefreshToken, String> {
    fun findByToken(token: String): RefreshToken?
    fun deleteByToken(token: String)
    fun deleteAllByUserName(userId: String)
}