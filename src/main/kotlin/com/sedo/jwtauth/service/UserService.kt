package com.sedo.jwtauth.service

import com.sedo.jwtauth.constants.Constants.Endpoints.FRONT_END_CREATE_PASSWORD
import com.sedo.jwtauth.constants.Constants.Roles.CUSTOMER
import com.sedo.jwtauth.event.CreateUserRequestEvent
import com.sedo.jwtauth.exception.DuplicateUsernameException
import com.sedo.jwtauth.exception.InvalidPasswordException
import com.sedo.jwtauth.exception.UserEmailNotFoundException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.dto.PasswordCreationResponseDto
import com.sedo.jwtauth.model.dto.UpdatePasswordDto
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

/**
 * Service class for managing user-related business logic.
 *
 * This service handles all user operations including user creation, updates,
 * password management, user search with filtering, status management,
 * and password reset functionality via email.
 *
 * @property userRepository Repository for user data access
 * @property mongoTemplate MongoDB template for custom queries
 * @property passwordEncoder Encoder for password security
 * @property emailService Service for sending emails
 * @property createPasswordTokenService Service for managing password reset tokens
 *
 */
@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val mongoTemplate: MongoTemplate,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val emailService: EmailService,
    private val createPasswordTokenService: CreatePasswordTokenService,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    @Value("\${app.frontend.url:http://localhost:4200}")
    private lateinit var frontendUrl: String

    /**
     * Searches users with optional filtering criteria.
     *
     * @param search Optional search term to match against user fields (username, firstName, lastName, email)
     * @param isActive Optional filter by user active status
     * @param hasOrders Optional filter by users who have placed orders
     * @param page Page number for pagination (0-based)
     * @param size Number of items per page
     * @return Page containing filtered users
     */
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

    /**
     * Retrieves all users.
     *
     * @return List of all User entities
     */
    fun getAllUsers(): List<User> {
        logger.debug("Retrieving all users")
        return userRepository.findAll()
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id ID of the user to retrieve
     * @return User entity
     * @throws UserNotFoundException if user doesn't exist
     */
    fun getUserById(id: String): User {
        logger.debug("Retrieving user with ID: {}", id)
        return userRepository.findById(id).getOrNull()
            ?: run {
                logger.error("User not found with ID: {}", id)
                throw UserNotFoundException("ID: $id")
            }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username Username of the user to retrieve
     * @return User entity
     * @throws UserNotFoundException if user doesn't exist
     */
    fun getUserByUsername(username: String): User {
        logger.debug("Retrieving user: {}", username)
        return userRepository.findByUserName(username)
            ?: run {
                logger.error("User not found: {}", username)
                throw UserNotFoundException(username)
            }
    }

    /**
     * Creates a new user in the system.
     *
     * @param createUserDto User data for creation
     * @return Created User entity
     * @throws DuplicateUsernameException if username already exists
     */
    fun createUser(createUserDto: UserDto): User {
        logger.info("Creating new user: {} with role: {}", createUserDto.userName, createUserDto.roles.joinToString())
        
        userRepository.findByUserName(createUserDto.userName)?.let {
            logger.warn("Attempt to create existing user: {}", createUserDto.userName)
            throw DuplicateUsernameException(createUserDto.userName)
        }
        
        val user = User(
            userName = createUserDto.userName,
            password = passwordEncoder.encode(generateRandomPassword()),
            firstName = createUserDto.firstName,
            lastName = createUserDto.lastName,
            address = createUserDto.address,
            email = createUserDto.email,
            numTel = createUserDto.numTel,
            isActive = createUserDto.isActive,
            roles = createUserDto.roles
        )
        
        val savedUser = userRepository.save(user)
        logger.info("User created successfully: {} (ID: {})", savedUser.userName, savedUser.id)

        // Publish event to send email for setting password
        publishCreateUserEvent(savedUser)
        return savedUser
    }

    /**
     * Registers a new user with default role and inactive status.
     *
     * @param user User entity containing registration details
     * @return Registered User entity
     * @throws DuplicateUsernameException if username already exists
     */
    fun registerUser(user: User): User {
        logger.info("Registering new user: {}", user.userName)

        userRepository.findByUserName(user.userName)?.let {
            logger.warn("Attempt to register existing user: {}", user.userName)
            throw DuplicateUsernameException(user.userName)
        }

        val user = User(
            userName = user.userName,
            password = passwordEncoder.encode(generateRandomPassword()),
            firstName = user.firstName,
            lastName = user.lastName,
            address = user.address,
            email = user.email,
            numTel = user.numTel,
            isActive = false, // inactive until email verification
            roles = listOf(CUSTOMER) // customer role by default because self-registration
        )

        val savedUser = userRepository.save(user)
        logger.info("User registered successfully: {} (ID: {})", savedUser.userName, savedUser.id)
        // Publish event to send email for setting password
        publishCreateUserEvent(savedUser)
        return savedUser
    }

    /**
     * Sends a password reset email to the user.
     *
     * @param email Email address of the user requesting password reset
     * @return PasswordCreationResponseDto indicating email sent status
     * @throws UserEmailNotFoundException if email doesn't exist
     */
    fun sendEmailToResetPassword(email: String): PasswordCreationResponseDto {
        logger.info("Initiating password reset for email: {}", email)
        val user = userRepository.findByEmail(email)
            ?: run {
                logger.warn("Password reset requested for non-existent email: {}", email)
                throw UserEmailNotFoundException(email)
            }

        try {
            val token = createPasswordTokenService.createPasswordToken(user.id!!)
            emailService.sendEmailPasswordReset(
                user.email,
                user.firstName,
                user.lastName,
                token
            )
            logger.info("Password reset email sent to: {}", email)
        return PasswordCreationResponseDto("If the email exists in our system, a password reset link has been sent.");
        } catch (e: Exception) {
            logger.error("Failed to send password reset email to: {}", email, e)
            throw e
        }
    }

    /**
     * Updates the active status of a user.
     *
     * @param userId ID of the user to update
     * @param action ActionDto containing the desired status action
     * @return Updated User entity
     * @throws UserNotFoundException if user doesn't exist
     */
    fun updateStatus(userId: String, action: ActionDto): User {
        logger.info("Updating status: {} for user {}", action, userId)
        val user = getUserById(userId)
        val updatedUser = user.copy(isActive = action.value == "activate")
        userRepository.save(updatedUser)
        logger.info("User {} status updated to {}", userId, updatedUser.isActive)
        return updatedUser
    }

    /**
     * Updates user information.
     *
     * @param idOldUser ID of the user to update
     * @param userName New username (optional)
     * @param firstName New first name (optional)
     * @param lastName New last name (optional)
     * @param address New address (optional)
     * @param email New email (optional)
     * @param isActive New active status (optional)
     * @param roles New roles (optional)
     * @return Updated User entity
     * @throws UserNotFoundException if user doesn't exist
     */
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

    /**
     * Updates the password for a user.
     *
     * @param id ID of the user to update
     * @param currentPassword Current password for verification
     * @param newPassword New password to set
     * @return UpdatePasswordDto containing the password update details
     * @throws InvalidPasswordException if current password is incorrect
     * @throws UserNotFoundException if user doesn't exist
     */
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

    fun deleteByUserName(userName: String) {
        logger.info("Deleting user: {}", userName)
        val user = getUserByUsername(userName)
        userRepository.deleteById(user.id!!)
        logger.info("User deleted successfully: {}", userName)
    }

    /**
     * Sets a new password for a user using a valid token.
     *
     * @param token Password reset token
     * @param newPassword New password to set
     * @return Updated User entity
     * @throws InvalidPasswordException if token is invalid or expired
     */
    fun setPasswordWithToken(token: String, newPassword: String): User {
        logger.info("Setting password with token")

        val userId = createPasswordTokenService.validatePasswordCreationToken(token)
            ?: throw InvalidPasswordException("Token invalide ou expiré")

        val user = getUserById(userId)
        val updatedUser = user.copy(password = passwordEncoder.encode(newPassword), isActive = true)
        userRepository.save(updatedUser)

        createPasswordTokenService.markTokenAsUsed(token)

        logger.info("Password set successfully for user ID: {}", userId)
        return updatedUser
    }

    /**
     * Validates a password reset token and generates the appropriate redirect URL.
     *
     * @param token Password reset token
     * @return Redirect URL for setting password or error page
     */
    fun validateTokenAndGetRedirectUrl(token: String): String {
        logger.info("Validating token and generating redirect URL")

        val userId = createPasswordTokenService.validatePasswordCreationToken(token)

        return if (userId != null) {
            val user = getUserById(userId)
            buildSetPasswordUrl(frontendUrl, user, token)
        } else {
            buildErrorUrl(frontendUrl)
        }
    }

    /**
     *  Helper method to build the password set URL with query parameters.
     */
    private fun buildSetPasswordUrl(frontendUrl: String, user: User, token: String): String {
        val params = mapOf(
            "valid" to "true",
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "userName" to user.userName,
            "email" to user.email,
            "token" to token
        )

        val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return "$frontendUrl$FRONT_END_CREATE_PASSWORD?$queryString"
    }

    /**
     *  Helper method to build the error URL with query parameters.
     */
    private fun buildErrorUrl(frontendUrl: String): String {
        val params = mapOf(
            "valid" to "false",
            "message" to "Token+invalide+ou+expire"
        )

        val queryString = params.entries.joinToString("&") { "${it.key}=${it.value}" }
        return "$frontendUrl$FRONT_END_CREATE_PASSWORD?$queryString"
    }

    /**
     * Deletes a user by their ID.
     *
     * @param id ID of the user to delete
     * @return Deleted User entity
     * @throws UserNotFoundException if user doesn't exist
     */
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

    /**
     * Helper method to generate a random password.
     * This is used when creating a new user to set an initial password
     * before they set their own password via email link.
     */
    private fun generateRandomPassword(): String{
        return "${UUID.randomUUID()}";
    }

    /**
     * Publishes an event to send a password creation email to the user.
     *
     * @param user User entity to send the email to
     */
    private fun publishCreateUserEvent(user: User) {
        val token = createPasswordTokenService.createPasswordToken(user.id!!)
        applicationEventPublisher.publishEvent(CreateUserRequestEvent(
                userName = user.userName,
                firstName =  user.firstName,
                lastName = user.lastName,
                email = user.email,
                token
        ))
    }
}
