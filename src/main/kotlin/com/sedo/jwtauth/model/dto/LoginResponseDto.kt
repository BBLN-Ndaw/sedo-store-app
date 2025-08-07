package com.sedo.jwtauth.model.dto

data class LoginResponseDto(
    val success: Boolean,
    val token: String? = null,
    )