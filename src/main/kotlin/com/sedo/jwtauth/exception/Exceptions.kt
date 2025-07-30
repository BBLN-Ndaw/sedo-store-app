package com.sedo.jwtauth.exception

class JwtException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotFoundException(username: String) : RuntimeException("User Not Found: $username")

class InvalidCredentialsException(message: String = "Invalid credentials") : RuntimeException(message)

class InvalidRolesException(message: String = "roles must be ADMIN, EMPLOYEE or CLIENT") : RuntimeException(message)

class ResourceNotFoundException(message: String) : RuntimeException(message)

class InsufficientStockException(message: String) : RuntimeException(message)

class InvalidOperationException(message: String) : RuntimeException(message)

class InvalidPasswordException(message: String = "Invalid password") : RuntimeException(message)

class NoTokenInAuthHeaderException(message: String = "No token provided") : RuntimeException(message)