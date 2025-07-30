package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.Pattern

data class ValidatedTokentDto(
    val valid: Boolean,
    @field:Pattern(regexp = "ACCEPTED|REJECTED", message = "status must be ACCEPTED or REJECTED")
    val status: String,
)
