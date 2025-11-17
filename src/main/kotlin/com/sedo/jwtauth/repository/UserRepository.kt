package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.User
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Repository interface for User entity data access operations.
 *
 * This repository provides data access methods for user management operations
 * using Spring Data MongoDB. It extends MongoRepository to provide standard
 * CRUD operations and includes custom query methods for user-specific operations.
 *
 * Features:
 * - Standard CRUD operations (inherited from MongoRepository)
 * - Custom username-based user lookup
 * - MongoDB integration for user data persistence
 *
 */
interface UserRepository : MongoRepository<User, String> {
    
    /**
     * Finds a user by their username.
     *
     * @param username The username to search for
     * @return User entity if found, null otherwise
     */
    fun findByUserName(username: String): User?
}
