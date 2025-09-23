package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdatePasswordDto(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 8, max = 100, message = "New password must be between 8 and 100 characters")
    val newPassword: String
)