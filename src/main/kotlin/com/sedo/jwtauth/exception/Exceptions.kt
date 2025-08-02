package com.sedo.jwtauth.exception

class JwtException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotFoundException(username: String) : RuntimeException("User Not Found: $username")

class InvalidCredentialsException(message: String = "Invalid credentials") : RuntimeException(message)

class ResourceNotFoundException(message: String) : RuntimeException(message)

class InsufficientStockException(message: String) : RuntimeException(message)

class InvalidOperationException(message: String) : RuntimeException(message)

class InvalidPasswordException(message: String = "Invalid password") : RuntimeException(message)

class NoTokenException(message: String = "No token provided") : RuntimeException(message)

class AuthenticationFailedException(message: String = "Authentication failed") : RuntimeException(message)