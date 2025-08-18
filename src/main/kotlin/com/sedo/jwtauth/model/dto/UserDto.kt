package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

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
    @field:Size(max = 100, message = "Email must not exceed 100 characters")
    val email: String,

    val address : Address,

    val numTel: String? = null,

    val isActive: Boolean = true,

    @field:Size(min = 1, message = "At least one role is required")
    val roles: List<String> = listOf("CLIENT"),
)

data class Address(
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String = "France",
)