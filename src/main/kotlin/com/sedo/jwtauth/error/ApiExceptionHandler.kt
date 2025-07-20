package com.sedo.jwtauth.error

import com.sedo.jwtauth.exception.InvalidCredentialsException
import com.sedo.jwtauth.exception.JwtException
import com.sedo.jwtauth.exception.UserNotFoundException
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(UserNotFoundException::class, UsernameNotFoundException::class)
    fun handleUserNotFound(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.status(NOT_FOUND)
            .body("UserNotFoundException : ${ex.message}")
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(ex: InvalidCredentialsException): ResponseEntity<String> {
        return ResponseEntity.status(UNAUTHORIZED)
            .body("InvalidCredentialsException : ${ex.message}")
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<String> {
        return ResponseEntity.status(UNAUTHORIZED)
            .body("JwtException, Invalid JWT Token: ${ex.message}")
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<String> {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
            .body("${ex.cause} : error Internal server error: ${ex.message}")
    }
}