package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository : MongoRepository<User, String> {
    fun findByUsername(username: String): User?
}
