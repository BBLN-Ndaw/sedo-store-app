package com.sedo.jwtauth.model.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

/**
 * Data Transfer Object for user information.
 *
 * This DTO represents user data for API operations including creation,
 * updates, and responses. It includes validation constraints to ensure
 * data integrity and proper formatting.
 *
 * @property id Unique identifier for the user (auto-generated)
 * @property userName Unique username for login (3-50 characters)
 * @property firstName User's first name (2-50 characters)
 * @property lastName User's last name (2-50 characters)
 * @property email Valid email address (max 100 characters)
 * @property address User's physical address information
 * @property numTel Optional phone number
 * @property isActive Whether the user account is active
 * @property roles List of user roles (at least one required, defaults to CLIENT)
 * @property createdAt Timestamp when the user was created
 *
 */

data class UserDto(
    val id: String? = null,
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val userName: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    val lastName: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max =   40, message = "Email must not exceed 40 characters")
    val email: String,

    val address : Address,

    val numTel: String? = null,

    val isActive: Boolean = false,

    @field:Size(min = 1, message = "At least one role is required")
    val roles: List<String> = listOf("CLIENT"),

    val createdAt: Instant? = null,
)

data class RegisterUserDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    val userName: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters")
    val lastName: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @field:Size(max =   40, message = "Email must not exceed 100 characters")
    val email: String,

    @field:Valid
    val address : Address,

    @field:NotBlank(message = "numTel is required")
    val numTel: String,
)

/**
 * Data class representing a user's physical address.
 *
 * @property street Street address line
 * @property city City name
 * @property postalCode Postal/ZIP code
 * @property country Country name (defaults to France)
 */
data class Address(
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String = "France",
)