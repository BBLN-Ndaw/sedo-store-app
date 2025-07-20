package com.sedo.jwtauth.exception

class JwtException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotFoundException(username: String) : RuntimeException("User Not Found: $username")

class InvalidCredentialsException(message: String = "Invalid credentials") : RuntimeException(message)

class InvalidRolesException(message: String = "roles must be equals to ADMIN or USER or MANAGER") : RuntimeException(message)
