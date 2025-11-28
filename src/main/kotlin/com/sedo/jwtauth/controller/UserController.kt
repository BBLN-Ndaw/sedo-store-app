package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.REGISTER
import com.sedo.jwtauth.constants.Constants.Endpoints.REQUEST_PASSWORD_RESET
import com.sedo.jwtauth.constants.Constants.Endpoints.USER
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toUser
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.PasswordCreationRequestDto
import com.sedo.jwtauth.model.dto.PasswordCreationResponseDto
import com.sedo.jwtauth.model.dto.RegisterUserDto
import com.sedo.jwtauth.model.dto.UpdatePasswordDto
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for user management operations.
 *
 * This controller handles CRUD operations for users, including user search,
 * profile management, password updates, and user status management.
 * Most operations require ADMIN or EMPLOYEE roles for security.
 *
 * @property userService Service for handling user business logic
 *
 */
@RestController
@RequestMapping(USER)
class UserController @Autowired constructor(
    private val userService: UserService
) {

    /**
     * Searches users with optional filtering criteria.
     *
     * @param search Optional search term for user fields
     * @param isActive Optional filter by active status
     * @param hasOrders Optional filter by users with orders
     * @param page Page number for pagination (default: 0)
     * @param size Number of items per page (default: 20)
     * @return ResponseEntity containing paginated list of users
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun searchUsers(@RequestParam(required = false) search: String?,
                    @RequestParam(required = false) isActive: String?,
                    @RequestParam(required = false) hasOrders: String?,
                    @RequestParam(defaultValue = "0") page: Int,
                    @RequestParam(defaultValue = "20") size: Int): ResponseEntity<Page<UserDto>> {
        return userService.searchUsers(search, isActive, hasOrders, page, size)
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }
    }
    /**
     * Retrieves all users.
     * @return ResponseEntity containing list of all users
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return userService.getAllUsers()
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }
    }

    /**
     * Creates a new user in the system.
     *
     * @param createUserDto User data for creation
     * @return ResponseEntity containing the created user
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createUser(@Valid @RequestBody createUserDto: UserDto): ResponseEntity<UserDto> {
        return userService.createUser(createUserDto)
            .toDto()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PostMapping(REGISTER)
    fun registerUser(@Valid @RequestBody registerUserDto: RegisterUserDto): ResponseEntity<UserDto> {
        return userService.registerUser(registerUserDto.toUser())
                .toDto()
                .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    /**
     * Retrieves the current user's profile information.
     *
     * @param authentication Current user's authentication context
     * @return ResponseEntity containing user profile data
     */
    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): ResponseEntity<UserDto> {
        return userService.getUserByUsername(authentication.name)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    /**
     * Retrieves a specific user by their ID.
     *
     * @param id User ID to retrieve
     * @return ResponseEntity containing the user data
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.getUserById(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    /**
     * Updates a user's active status (activate/deactivate).
     *
     * @param id User ID to update
     * @param action Action DTO containing the new status
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateUserStatus(@PathVariable id: String, @RequestBody action: ActionDto): ResponseEntity<UserDto> {
        return userService.updateStatus(id, action)
            .toDto().let { ResponseEntity.ok(it) }
    }

    /**
     * Updates user information.
     *
     * @param id User ID to update
     * @param updateRequest Updated user data
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateUser(
        @PathVariable id: String, 
        @Valid @RequestBody updateRequest: UserDto
    ): ResponseEntity<UserDto> {
        return userService.updateUser(
            id,
           updateRequest.userName,
            updateRequest.firstName,
            updateRequest.lastName,
            updateRequest.address,
            updateRequest.email,
            updateRequest.isActive,
            updateRequest.roles
        ).toDto().let { ResponseEntity.ok(it) }
    }

    /**
     * Updates a user's password after logged in.
     *
     * @param id User ID whose password to update
     * @param passwordUpdate DTO containing current and new password
     * @return ResponseEntity containing password update confirmation
     */
    @PutMapping("/{id}/password")
    fun updateUserPassword(
        @PathVariable id: String,
        @Valid @RequestBody passwordUpdate: UpdatePasswordDto
    ): ResponseEntity<UpdatePasswordDto> {
        return userService.updatePassword(id, passwordUpdate.currentPassword, passwordUpdate.newPassword)
            .let { ResponseEntity.ok(it) }
    }

    /**
     * Initiates password reset process by sending reset email before login.
     *
     * @param PasswordCreationRequestDto containing email of the user requesting password
     */
    @PostMapping(REQUEST_PASSWORD_RESET)
    fun sendEmailToResetPassword(@RequestBody passwordCreationRequestDto: PasswordCreationRequestDto): PasswordCreationResponseDto {
        return userService.sendEmailToResetPassword(passwordCreationRequestDto.email)
    }
    /**
     * Deletes a user from the system.
     *
     * @param id User ID to delete
     * @return ResponseEntity containing the deleted user data
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteUser(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.deleteUser(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }
}
