package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

/**
 * Data Transfer Object for product information.
 *
 * This DTO represents product data for API operations including inventory management,
 * product creation, updates, and catalog responses. It includes comprehensive validation
 * constraints to ensure data integrity for e-commerce operations.
 *
 * @property id Unique identifier for the product (auto-generated)
 * @property name Product name (2-200 characters)
 * @property description Optional product description (max 1000 characters)
 * @property sku Stock Keeping Unit - unique product identifier (3-50 characters)
 * @property categoryId Reference to the product category
 * @property supplierId Reference to the product supplier
 * @property sellingPrice Selling price (must be greater than 0)
 * @property costPrice Cost price for calculating profit margins
 * @property quantityInStock Current inventory stock level
 * @property minimumStockLevel Threshold for low stock alerts
 * @property maximumStockLevel Maximum inventory capacity
 * @property isOnPromotion Whether the product is currently on promotion
 * @property promotionPrice Special promotional price when applicable
 * @property isActive Whether the product is active and available for sale
 * @property imageUrls List of product image URLs
 * @property weight Product weight for shipping calculations
 * @property dimensions Product dimensions (length x width x height)
 * @property manufacturer Product manufacturer information
 * @property warrantyPeriod Warranty period in months
 * @property tags List of searchable tags for the product
 * @property createdAt Timestamp when the product was created
 * @property updatedAt Timestamp when the product was last modified
 * @property lastRestockedAt Timestamp when the product was last restocked
 *
 */

data class ProductDto(
    val id: String? = null,

    @field:NotBlank(message = "Product name is required")
    @field:Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:NotBlank(message = "SKU is required")
    @field:Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    val sku: String,

    @field:NotNull(message = "Category ID is required")
    val categoryId: String,

    @field:NotNull(message = "Supplier ID is required")
    val supplierId: String,

    @field:NotNull(message = "Selling price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val sellingPrice: BigDecimal,

    @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int = 0,

    @field:Min(value = 0, message = "Minimum stock cannot be negative")
    val minStock: Int = 0,

    val expirationDate: Instant? = null,

    @field:NotBlank(message = "Unit is required")
    val unit: String = "pièce",

    val isActive: Boolean = true,

    val imageUrls: List<String> = emptyList(),

    val isOnPromotion: Boolean = false,

    val promotionPrice: BigDecimal? = null,

    val promotionEndDate: LocalDateTime? = null,
)
