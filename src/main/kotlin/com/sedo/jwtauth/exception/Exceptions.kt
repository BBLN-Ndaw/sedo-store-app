package com.sedo.jwtauth.exception

// ================================
// Authentication & Security Exceptions
// ================================

class JwtException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UserNotFoundException(username: String) : RuntimeException("User Not Found: $username")

class InvalidCredentialsException(message: String = "Invalid credentials") : RuntimeException(message)

class InvalidPasswordException(message: String = "Invalid password") : RuntimeException(message)

class NoTokenException(message: String = "No token provided") : RuntimeException(message)

class AuthenticationFailedException(message: String = "Authentication failed") : RuntimeException(message)

// ================================
// Business Logic Exceptions
// ================================

class ResourceNotFoundException(message: String) : RuntimeException(message)

class InsufficientStockException(message: String) : RuntimeException(message)

class InvalidOperationException(message: String) : RuntimeException(message)

// ================================
// Domain-Specific Business Exceptions
// ================================

class DuplicateUsernameException(username: String) : RuntimeException("Username '$username' already exists")

class DuplicateEmailException(email: String) : RuntimeException("Email '$email' already exists")

class ProductOutOfStockException(productName: String) : RuntimeException("Product '$productName' is out of stock")

class InvalidPriceException(price: Double) : RuntimeException("Invalid price: $price. Price must be positive")

class InvalidQuantityException(quantity: Int) : RuntimeException("Invalid quantity: $quantity. Quantity must be positive")

class OrderNotFoundException(orderId: String) : RuntimeException("Order not found with ID: $orderId")

class ProductNotFoundException(productId: String) : RuntimeException("Product not found with ID: $productId")

class CategoryNotFoundException(categoryId: String) : RuntimeException("Category not found with ID: $categoryId")

class SupplierNotFoundException(supplierId: String) : RuntimeException("Supplier not found with ID: $supplierId")

class SaleNotFoundException(saleId: String) : RuntimeException("Sale not found with ID: $saleId")

class InvalidOrderStatusException(status: String) : RuntimeException("Invalid order status: $status")

class OrderAlreadyProcessedException(orderId: String) : RuntimeException("Order $orderId has already been processed")

class InvalidCategoryOperationException(message: String) : RuntimeException(message)

class InvalidSupplierOperationException(message: String) : RuntimeException(message)