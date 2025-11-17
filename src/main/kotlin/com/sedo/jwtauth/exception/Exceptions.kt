package com.sedo.jwtauth.exception

/**
 * Custom exception classes for the Store Management System.
 *
 * This file contains all custom exceptions used throughout the application,
 * organized into logical categories for better maintainability and understanding.
 * These exceptions provide specific error handling for different business scenarios.
 *
 * @author Store Management System
 * @since 1.0
 */

// ================================
// Authentication & Security Exceptions
// ================================

/**
 * Exception thrown when JWT token operations fail.
 * 
 * @param message Descriptive error message
 * @param cause Optional underlying cause of the exception
 */
class JwtException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

/**
 * Exception thrown when a user is not found in the system.
 * 
 * @param username The username that was not found
 */
class UserNotFoundException(username: String) : RuntimeException("User Not Found: $username")

/**
 * Exception thrown when user provides invalid login credentials.
 * 
 * @param message Optional custom error message
 */
class InvalidCredentialsException(message: String = "Invalid credentials") : RuntimeException(message)

/**
 * Exception thrown when password validation fails.
 * 
 * @param message Optional custom error message
 */
class InvalidPasswordException(message: String = "Invalid password") : RuntimeException(message)

/**
 * Exception thrown when no authentication token is provided.
 * 
 * @param message Optional custom error message
 */
class NoTokenException(message: String = "No token provided") : RuntimeException(message)

/**
 * Exception thrown when user authentication fails.
 * 
 * @param message Optional custom error message
 */
class AuthenticationFailedException(message: String = "Authentication failed") : RuntimeException(message)

/**
 * Exception thrown when refresh token operation fails.
 * 
 * @param message Optional custom error message
 */
class RefreshTokenFailedException(message: String = "Refresh token failed") : RuntimeException(message)

// ================================
// Business Logic Exceptions
// ================================

/**
 * Exception thrown when a requested resource is not found.
 * 
 * @param message Descriptive error message about the missing resource
 */
class ResourceNotFoundException(message: String) : RuntimeException(message)

/**
 * Exception thrown when a product is unavailable (out of stock or inactive).
 * 
 * @param message Descriptive error message about product availability
 */
class UnAvailableProductException(message: String) : RuntimeException(message)

/**
 * Exception thrown when an invalid operation is attempted.
 * 
 * @param message Descriptive error message about the invalid operation
 */

class InvalidOperationException(message: String) : RuntimeException(message)

// ================================
// Domain-Specific Business Exceptions
// ================================

/**
 * Exception thrown when attempting to create a user with an existing username.
 * 
 * @param username The duplicate username
 */
class DuplicateUsernameException(username: String) : RuntimeException("Username '$username' already exists")

/**
 * Exception thrown when attempting to use an email that already exists.
 * 
 * @param email The duplicate email address
 */
class DuplicateEmailException(email: String) : RuntimeException("Email '$email' already exists")

/**
 * Exception thrown when a product is out of stock.
 * 
 * @param productName The name of the out-of-stock product
 */
class ProductOutOfStockException(productName: String) : RuntimeException("Product '$productName' is out of stock")

/**
 * Exception thrown when an invalid price value is provided.
 * 
 * @param price The invalid price value
 */
class InvalidPriceException(price: Double) : RuntimeException("Invalid price: $price. Price must be positive")

/**
 * Exception thrown when an invalid quantity value is provided.
 * 
 * @param quantity The invalid quantity value
 */
class InvalidQuantityException(quantity: Int) : RuntimeException("Invalid quantity: $quantity. Quantity must be positive")

/**
 * Exception thrown when an order is not found.
 * 
 * @param orderId The ID of the order that was not found
 */
class OrderNotFoundException(orderId: String) : RuntimeException("Order not found with ID: $orderId")

/**
 * Exception thrown when a product is not found.
 * 
 * @param productId The ID of the product that was not found
 */
class ProductNotFoundException(productId: String) : RuntimeException("Product not found with ID: $productId")

/**
 * Exception thrown when a category is not found.
 * 
 * @param categoryId The ID of the category that was not found
 */
class CategoryNotFoundException(categoryId: String) : RuntimeException("Category not found with ID: $categoryId")

/**
 * Exception thrown when a supplier is not found.
 * 
 * @param supplierId The ID of the supplier that was not found
 */

class SupplierNotFoundException(supplierId: String) : RuntimeException("Supplier not found with ID: $supplierId")

/**
 * Exception thrown when a sale record is not found.
 * 
 * @param saleId The ID of the sale that was not found
 */
class SaleNotFoundException(saleId: String) : RuntimeException("Sale not found with ID: $saleId")

/**
 * Exception thrown when an invalid order status is provided.
 * 
 * @param status The invalid order status
 */
class InvalidOrderStatusException(status: String) : RuntimeException("Invalid order status: $status")

/**
 * Exception thrown when attempting to process an order that has already been processed.
 * 
 * @param orderId The ID of the order that was already processed
 */
class OrderAlreadyProcessedException(orderId: String) : RuntimeException("Order $orderId has already been processed")

/**
 * Exception thrown when an invalid category operation is attempted.
 * 
 * @param message Descriptive error message about the invalid operation
 */
class InvalidCategoryOperationException(message: String) : RuntimeException(message)

/**
 * Exception thrown when an invalid supplier operation is attempted.
 * 
 * @param message Descriptive error message about the invalid operation
 */

class InvalidSupplierOperationException(message: String) : RuntimeException(message)