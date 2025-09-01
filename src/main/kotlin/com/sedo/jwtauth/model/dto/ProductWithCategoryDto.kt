package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.model.entity.Category
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime

data class ProductWithCategoryDto(

    val id: String? = null,

    @field:NotBlank(message = "Product name is required")
    @field:Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    val name: String,

    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,

    @field:NotBlank(message = "SKU is required")
    @field:Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    val sku: String,

    @field:NotNull(message = "Category  is required")
    val category: Category,

    @field:NotNull(message = "Supplier ID is required")
    val supplierId: String,

    @field:NotNull(message = "Selling price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val sellingPrice: BigDecimal,

    val taxRate: BigDecimal = BigDecimal("0.20"), //20% par défaut

    @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int = 0,

    @field:Min(value = 0, message = "Minimum stock cannot be negative")
    val minStock: Int = 0,

    @field:NotNull(message = "Expiration date is required")
    val expirationDate: Instant,

    @field:NotBlank(message = "Unit is required")
    val unit: String = "pièce",

    val isActive: Boolean = true,

    val imageUrls: List<String> = emptyList(),

    val isOnPromotion: Boolean = false,

    val promotionPrice: BigDecimal? = null,

    val promotionEndDate: LocalDateTime? = null,
)
