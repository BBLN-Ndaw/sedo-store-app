package com.sedo.jwtauth.model.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

/**
 * Standard error response DTO for API errors
 */
data class ErrorResponseDto(
    val error: String,
    val message: String,
    val status: Int,
    val path: String? = null,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * Validation error response DTO for field validation errors
 */
data class ValidationErrorResponseDto(
    val error: String = "Validation Failed",
    val message: String,
    val status: Int,
    val path: String? = null,
    val fieldErrors: List<FieldErrorDto>,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    val timestamp: LocalDateTime = LocalDateTime.now()
)

/**
 * Field error DTO for specific field validation errors
 */
data class FieldErrorDto(
    val field: String,
    val rejectedValue: Any?,
    val message: String
)
