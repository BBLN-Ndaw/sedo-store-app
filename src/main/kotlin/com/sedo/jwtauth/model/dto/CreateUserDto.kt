package com.sedo.jwtauth.model.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class CreateUserDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    val lastName: String,

    @field:Valid
    val address: Address,

    val numTel: String? = null,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String,

    val isActive: Boolean = true,

    @field:Size(min = 1, message = "At least one role is required")
    val roles: List<String> = listOf("CLIENT"),

    val createdAt: Instant? = null
)

/**
 * DTO pour la mise à jour du password
 */
data class UpdatePasswordDto(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,
    
    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    val newPassword: String
)
