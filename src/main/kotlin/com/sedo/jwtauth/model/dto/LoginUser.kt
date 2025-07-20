package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.Size

data class LoginUser(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    @field:Size(min = 3, message = "Password must be at least 3 characters long")
    val password: String)
