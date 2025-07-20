package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.Size

data class UserDto(
    val id: String? = null,
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    @field:Size(min = 3, message = "Password must be at least 3 characters long")
    val password: String,
    @field:Size(min = 1, message = "At least one role is required")
    val roles: List<String>
)