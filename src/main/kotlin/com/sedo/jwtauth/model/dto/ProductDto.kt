package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.validation.ValidPriceMargin
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.time.LocalDate

@ValidPriceMargin(message = "Selling price must be greater than purchase price")
data class ProductDto(
    val id: String? = null,
    
    @field:NotBlank(message = "SKU is required")
    @field:Size(min = 3, max = 50, message = "SKU must be between 3 and 50 characters")
    val sku: String,
    
    @field:NotBlank(message = "Product name is required")
    @field:Size(min = 2, max = 200, message = "Product name must be between 2 and 200 characters")
    val name: String,
    
    @field:Size(max = 1000, message = "Description must not exceed 1000 characters")
    val description: String? = null,
    
    @field:NotBlank(message = "Category ID is required")
    val categoryId: String,
    
    @field:NotBlank(message = "Supplier ID is required")
    val supplierId: String,
    
    @field:NotNull(message = "Purchase price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be greater than 0")
    val purchasePrice: BigDecimal,
    
    @field:NotNull(message = "Selling price is required")
    @field:DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be greater than 0")
    val sellingPrice: BigDecimal,
    
    @field:Min(value = 0, message = "Stock quantity cannot be negative")
    val stockQuantity: Int = 0,
    
    @field:Min(value = 0, message = "Minimum stock cannot be negative")
    val minimumStock: Int = 0,
    
    val imageUrls: List<String> = emptyList(),
    val isActive: Boolean = true,
    val expirationDate: LocalDate? = null,
    
    @field:NotBlank(message = "Unit is required")
    val unit: String = "pièce",
    
    val tags: List<String> = emptyList()
)
