package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

data class CreateProductDto(
        val id: String? = null,
        @field:NotBlank(message = "Product name is required")
    @field:Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    val name: String,

        @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

        @field:NotBlank(message = "SKU is required")
    @field:Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    val sku: String,

        @field:NotNull(message = "Category is required")
    val categoryId: String,

        @field:NotNull(message = "Supplier is required")
    val supplierId: String,

        @field:NotNull(message = "Selling price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val sellingPrice: BigDecimal,

        @field:NotNull(message = "Purchase price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val purchasePrice: BigDecimal,

        @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int = 0,

        @field:Min(value = 0, message = "Minimum stock cannot be negative")
    val minStock: Int = 0,

        val expirationDate: Instant? = null,

        @field:NotBlank(message = "Unit is required")
    val unit: String = "pièce",

        val isActive: Boolean = true,

        val imageUrls: List<String>,

        val isOnPromotion: Boolean = false,

        val promotionPrice: BigDecimal? = null,

        val promotionEndDate: LocalDateTime? = null,
)

data class UpdateProductDto(
        val id: String,
        @field:Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    val name: String? = null,

        @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

        @field:Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    val sku: String? = null,

        val categoryId: String? = null,

        val supplierId: String? = null,

        @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val sellingPrice: BigDecimal? = null,

        val taxRate: BigDecimal? = null,

        val purchasePrice: BigDecimal? = null,

        @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int? = null,

        @field:Min(value = 0, message = "Minimum stock cannot be negative")
    val minStock: Int? = null,

        val expirationDate: Instant? = null,

        val unit: String? = null,

        val imageUrls: List<String>? = null,

        val isOnPromotion: Boolean? = null,

        val promotionPrice: BigDecimal? = null,

        val promotionEndDate: Instant? = null,
)