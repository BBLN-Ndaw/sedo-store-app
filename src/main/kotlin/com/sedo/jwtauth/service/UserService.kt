package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.DuplicateUsernameException
import com.sedo.jwtauth.exception.InvalidPasswordException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.dto.CreateUserDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder) {
    
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
        return userRepository.findByUserName(username)
            ?: run {
                logger.error("User not found: {}", username)
                throw UserNotFoundException(username)
            }
    }

    fun createUser(createUserDto: CreateUserDto): User {
        logger.info("Creating new user: {} with role: {}", createUserDto.username, createUserDto.roles.joinToString())
        
        userRepository.findByUserName(createUserDto.username)?.let {
            logger.warn("Attempt to create existing user: {}", createUserDto.username)
            throw DuplicateUsernameException(createUserDto.username)
        }
        
        val user = User(
            userName = createUserDto.username,
            password = passwordEncoder.encode(createUserDto.password), // ✅ Hash du password
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            address = createUserDto.address,
            email = createUserDto.email,
            isActive = createUserDto.isActive,
            roles = createUserDto.roles
        )
        
        val savedUser = userRepository.save(user)
        logger.info("User created successfully: {} (ID: {})", savedUser.userName, savedUser.id)
        return savedUser
    }

    fun updateUser(idOldUser: String, userName: String?, firstName: String?, lastName: String?, address: Address?, email: String?, isActive: Boolean?, roles: List<String>?): User {
        logger.info("Updating user ID: {}", idOldUser)
        val user = getUserById(idOldUser)
        val updatedUser = user.copy(
            userName = userName ?: user.userName,
            firstName = firstName ?: user.firstName,
            lastName = lastName ?: user.lastName,
            address = address ?: user.address,
            email = email ?: user.email,
            isActive = isActive ?: user.isActive,
            roles = roles ?: user.roles
        )
        userRepository.save(updatedUser)
        return updatedUser
    }

    fun updatePassword(id: String, currentPassword: String, newPassword: String) {
        logger.info("Updating password for user ID: {}", id)
        
        val user = getUserById(id)
        
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            logger.warn("Invalid current password for user ID: {}", id)
            throw InvalidPasswordException("Current password is incorrect")
        }

        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))
        userRepository.save(updatedUser)
        logger.info("Password updated successfully for user ID: {}", id)
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
