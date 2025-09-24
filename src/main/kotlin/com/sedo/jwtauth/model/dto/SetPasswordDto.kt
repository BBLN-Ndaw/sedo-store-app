package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SetPasswordDto(
    @field:NotBlank(message = "Token is required")
    val token: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String
)
