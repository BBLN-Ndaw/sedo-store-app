package com.sedo.jwtauth.error

import com.sedo.jwtauth.exception.*
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class, UsernameNotFoundException::class)
    fun handleUserNotFound(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(NOT_FOUND)
            .body(ErrorResponse("USER_NOT_FOUND", ex.message ?: "User not found"))
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(NOT_FOUND)
            .body(ErrorResponse("RESOURCE_NOT_FOUND", ex.message ?: "Resource not found"))
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ErrorResponse("INVALID_CREDENTIALS", ex.message ?: "Invalid credentials"))
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(UNAUTHORIZED)
            .body(ErrorResponse("JWT_ERROR", "Invalid JWT Token: ${ex.message}"))
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun handleInsufficientStock(ex: InsufficientStockException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(BAD_REQUEST)
            .body(ErrorResponse("INSUFFICIENT_STOCK", ex.message ?: "Insufficient stock"))
    }

    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperation(ex: InvalidOperationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(BAD_REQUEST)
            .body(ErrorResponse("INVALID_OPERATION", ex.message ?: "Invalid operation"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val errors = ex.bindingResult.allErrors.map { error ->
            val fieldName = (error as FieldError).field
            val message = error.defaultMessage ?: "Invalid value"
            ValidationError(fieldName, message)
        }
        
        return ResponseEntity.status(BAD_REQUEST)
            .body(ValidationErrorResponse("VALIDATION_ERROR", "Validation failed", errors))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body(ErrorResponse("INTERNAL_ERROR", "Internal server error: ${ex.message}"))
    }
}

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ValidationErrorResponse(
    val code: String,
    val message: String,
    val errors: List<ValidationError>,
    val timestamp: Long = System.currentTimeMillis()
)

data class ValidationError(
    val field: String,
    val message: String
)