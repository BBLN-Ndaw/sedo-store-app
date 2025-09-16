package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.DuplicateUsernameException
import com.sedo.jwtauth.exception.InvalidPasswordException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.dto.CreateUserDto
import com.sedo.jwtauth.model.dto.UpdatePasswordDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val mongoTemplate: MongoTemplate,
    private val passwordEncoder: BCryptPasswordEncoder) {
    
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun searchUsers(search: String?, isActive: String?, hasOrders: String?,
                    page: Int, size: Int): Page<User> {
        logger.debug("Retrieving users with filters - search: {}, isActive: {}, hasOrders: {}, page: {}, size: {}",
            search, isActive, hasOrders, page, size)

        val query = Query()
        val criteriaList = mutableListOf<Criteria>()

        if (!search.isNullOrBlank()) {
            criteriaList.add(
                Criteria().orOperator(
                    Criteria.where("firstName").regex(search, "i"),
                    Criteria.where("lastName").regex(search, "i"),
                    Criteria.where("email").regex(search, "i")
                )
            )
        }

        if (!isActive.isNullOrBlank()) {
            criteriaList.add(Criteria.where("isActive").`is`(isActive.toBoolean()))
        }

        if (!hasOrders.isNullOrBlank()) {
            if (hasOrders.toBoolean()) {
                criteriaList.add(Criteria.where("orderCount").gt(0))
            } else {
                criteriaList.add(Criteria.where("orderCount").`is`(0))
            }
        }

        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }

        val pageable: Pageable = PageRequest.of(page, size)
        query.with(pageable)

        val users = mongoTemplate.find(query, User::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), User::class.java)

        logger.info("Number of users found: {}", total)
        return PageImpl(users, pageable, total)
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

    fun updateStatus(userId: String, action: ActionDto): User {
        logger.info("Updating status: {} for user {}", action, userId)
        val user = getUserById(userId)
        val updatedUser = user.copy(isActive = action.value == "activate")
        logger.info("Updated status: {}", updatedUser.isActive)
        logger.info("Updated user: {}", updatedUser)
        userRepository.save(updatedUser)
        return updatedUser
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

    fun updatePassword(id: String, currentPassword: String, newPassword: String): UpdatePasswordDto {
        logger.info("Updating password for user ID: {}", id)
        
        val user = getUserById(id)
        
        if (!passwordEncoder.matches(currentPassword, user.password)) {
            logger.warn("Invalid current password for user ID: {}", id)
            throw InvalidPasswordException("Current password is incorrect")
        }

        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword))
        userRepository.save(updatedUser)
        logger.info("Password updated successfully for user ID: {}", id)
        return UpdatePasswordDto(currentPassword = currentPassword, newPassword = newPassword)
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
