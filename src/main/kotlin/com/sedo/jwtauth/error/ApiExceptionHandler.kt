package com.sedo.jwtauth.error

import com.sedo.jwtauth.exception.*
import com.sedo.jwtauth.model.dto.ErrorResponseDto
import com.sedo.jwtauth.model.dto.FieldErrorDto
import com.sedo.jwtauth.model.dto.ValidationErrorResponseDto
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestCookieException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException

/**
 * Global exception handler for the application
 * Handles all exceptions and converts them to appropriate HTTP responses
 */
@RestControllerAdvice
class ApiExceptionHandler {

    private val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    // ================================
    // Authentication & Authorization Exceptions
    // ================================

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(
        ex: UserNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("User not found: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "User Not Found",
            message = ex.message ?: "User not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(InvalidCredentialsException::class, BadCredentialsException::class)
    fun handleInvalidCredentialsException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid credentials: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Credentials",
            message = "Invalid username or password",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(
        ex: UsernameNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Username not found: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Authentication Failed",
            message = "Invalid credentials",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(JwtException::class)
    fun handleJwtException(
        ex: JwtException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("JWT error: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Authentication Error",
            message = "Invalid or expired token",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AuthenticationFailedException::class)
    fun handleAuthenticationFailedException(
        ex: AuthenticationFailedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Authentication failed: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Authentication Failed",
            message = "Authentication failed",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(RefreshTokenFailedException::class)
    fun handleRefreshTokenFailedException(
        ex: RefreshTokenFailedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Refresh Token Failed : ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Refresh Token Failed",
            message = "Refresh Token Failed due to revoked token",
            status = HttpStatus.UNAUTHORIZED.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Access denied: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Access Denied",
            message = "You don't have permission to access this resource",
            status = HttpStatus.FORBIDDEN.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse)
    }

    // ================================
    // Business Logic Exceptions
    // ================================

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Resource not found: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Resource Not Found",
            message = ex.message ?: "Requested resource not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(UnAvailableProductException::class)
    fun handleInsufficientStockException(
        ex: UnAvailableProductException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn(ex.message)
        val errorResponse = ErrorResponseDto(
            error = "Produit Indisponible",
            message = ex.message ?: "Stock insuffisant pour le produit demandé",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(InvalidOperationException::class)
    fun handleInvalidOperationException(
        ex: InvalidOperationException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid operation: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Operation",
            message = ex.message ?: "Invalid operation",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(InvalidPasswordException::class)
    fun handleInvalidPasswordException(
        ex: InvalidPasswordException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid password: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Password",
            message = ex.message ?: "Invalid password",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // ================================
    // Domain-Specific Business Exceptions
    // ================================

    @ExceptionHandler(DuplicateUsernameException::class)
    fun handleDuplicateUsernameException(
        ex: DuplicateUsernameException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Duplicate username: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Duplicate Username",
            message = ex.message ?: "Username already exists",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(DuplicateEmailException::class)
    fun handleDuplicateEmailException(
        ex: DuplicateEmailException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Duplicate email: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Duplicate Email",
            message = ex.message ?: "Email already exists",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(ProductOutOfStockException::class)
    fun handleProductOutOfStockException(
        ex: ProductOutOfStockException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Product out of stock: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Product Out of Stock",
            message = ex.message ?: "Product is out of stock",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(InvalidPriceException::class)
    fun handleInvalidPriceException(
        ex: InvalidPriceException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid price: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Price",
            message = ex.message ?: "Invalid price provided",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(InvalidQuantityException::class)
    fun handleInvalidQuantityException(
        ex: InvalidQuantityException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid quantity: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Quantity",
            message = ex.message ?: "Invalid quantity provided",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(
        OrderNotFoundException::class,
        ProductNotFoundException::class,
        CategoryNotFoundException::class,
        SupplierNotFoundException::class,
        SaleNotFoundException::class
    )
    fun handleSpecificResourceNotFoundException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Specific resource not found: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Resource Not Found",
            message = ex.message ?: "Requested resource not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(InvalidOrderStatusException::class)
    fun handleInvalidOrderStatusException(
        ex: InvalidOrderStatusException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid order status: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Order Status",
            message = ex.message ?: "Invalid order status",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(OrderAlreadyProcessedException::class)
    fun handleOrderAlreadyProcessedException(
        ex: OrderAlreadyProcessedException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Order already processed: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Order Already Processed",
            message = ex.message ?: "Order has already been processed",
            status = HttpStatus.CONFLICT.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse)
    }

    @ExceptionHandler(
        InvalidCategoryOperationException::class,
        InvalidSupplierOperationException::class
    )
    fun handleInvalidBusinessOperationException(
        ex: RuntimeException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Invalid business operation: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Invalid Operation",
            message = ex.message ?: "Invalid business operation",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // ================================
    // Validation Exceptions
    // ================================

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException,
        request: HttpServletRequest
    ): ResponseEntity<ValidationErrorResponseDto> {
        logger.warn("Validation failed: ${ex.message}")

        val fieldErrors = ex.bindingResult.fieldErrors.map { fieldError: FieldError ->
            FieldErrorDto(
                field = fieldError.field,
                rejectedValue = fieldError.rejectedValue,
                message = fieldError.defaultMessage ?: "Invalid value"
            )
        }

        val errorResponse = ValidationErrorResponseDto(
            message = "Validation failed for one or more fields",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI,
            fieldErrors = fieldErrors
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }


    @ExceptionHandler(MissingRequestCookieException::class)
    fun handleMissingRequestCookieException(
        ex: MissingRequestCookieException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Missing refresh token: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Bad Request: Missing refresh token",
            message = "Required cookie is missing: ${ex.cookieName}",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Illegal argument: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Bad Request",
            message = ex.message ?: "Invalid argument provided",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Type mismatch: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Bad Request",
            message = "Invalid parameter type for '${ex.name}'. Expected ${ex.requiredType?.simpleName}",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("Malformed JSON request: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Bad Request",
            message = "Malformed JSON request",
            status = HttpStatus.BAD_REQUEST.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    // ================================
    // HTTP Exceptions
    // ================================

    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(
        ex: NoHandlerFoundException,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.warn("No handler found: ${ex.message}")
        val errorResponse = ErrorResponseDto(
            error = "Not Found",
            message = "The requested endpoint was not found",
            status = HttpStatus.NOT_FOUND.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    // ================================
    // Generic Exception Handler
    // ================================

    @ExceptionHandler(Exception::class)
    fun handleGenericException(
        ex: Exception,
        request: HttpServletRequest
    ): ResponseEntity<ErrorResponseDto> {
        logger.error("Unhandled exception occurred", ex)
        val errorResponse = ErrorResponseDto(
            error = "Internal Server Error",
            message = "An unexpected error occurred : ${ex.message}",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            path = request.requestURI
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}