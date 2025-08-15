package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CategoryDto(
    val id: String? = null,
    
    @field:NotBlank(message = "Category name is required")
    @field:Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    val name: String,
    @field:Size(max = 500, message = "Description must not exceed 500 characters")
    val description: String? = null,

    val isActive: Boolean = true
)
