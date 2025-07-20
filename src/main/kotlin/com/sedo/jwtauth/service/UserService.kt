package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import kotlin.jvm.optionals.getOrNull

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun getAllUsers(): List<User> {
        logger.debug("Retrieving all users")
        val users = userRepository.findAll()
        logger.info("Number of users found: {}", users.size)
        return users
    }

    fun getUserById(id: String): User {
        logger.debug("Retrieving user with ID: {}", id)
        return userRepository.findById(id).getOrNull()
            ?: run {
                logger.error("User not found with ID: {}", id)
                throw UserNotFoundException("ID: $id")
            }
    }

    fun getUserByUsername(username: String): User {
        logger.debug("Retrieving user: {}", username)
        return userRepository.findByUsername(username)
            ?: run {
                logger.error("User not found: {}", username)
                throw UserNotFoundException(username)
            }
    }

    fun createUser(user: User): User {
        logger.info("Creating new user: {} with role: {}", user.username, user.roles.joinToString())
        userRepository.findByUsername(user.username)?.let { logger.warn("Attempt to create existing user: {}", user.username)
            throw IllegalArgumentException("A user with this username already exists")
        }
        val savedUser = userRepository.save(User(username = user.username, password = passwordEncoder.encode(user.password), roles = user.roles))
        logger.info("User created successfully: {} (ID: {})", user.username, savedUser.id)
        return savedUser
    }

    fun updateUser(id: String, username: String?, password: String?, roles: List<String>?): User {
        logger.info("Updating user ID: {}", id)
        val user = getUserById(id)
        val updatedUser = user.copy(
            username = username ?: user.username,
            password = password?.let { passwordEncoder.encode(it) } ?: user.password,
            roles = roles ?: user.roles
        )
        val savedUser = userRepository.save(updatedUser)
        logger.info("User updated successfully: {}", savedUser.username)
        return savedUser
    }

    fun deleteUser(id: String): User {
        logger.info("Deleting user ID: {}", id)
        if (!userRepository.existsById(id)) {
            logger.error("Attempt to delete non-existent user ID: {}", id)
            throw UserNotFoundException("ID: $id")
        }
        val user = getUserById(id)
        userRepository.deleteById(id)
        logger.info("User deleted successfully ID: {}", id)
        return user
    }
}
