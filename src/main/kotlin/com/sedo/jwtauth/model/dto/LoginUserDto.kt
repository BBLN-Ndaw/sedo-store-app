package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginUserDto(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String
)
