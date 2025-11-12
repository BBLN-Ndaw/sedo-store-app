package com.sedo.jwtauth.model.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SupplierDto(
    val id: String? = null,

    @field:NotBlank(message = "Supplier name is required")
    @field:Size(min = 2, max = 100, message = "Supplier name must be between 2 and 100 characters")
    val name: String,

    @field:Size(max = 100, message = "Contact person name must not exceed 100 characters")
    val contactPersonName: String? = null,

    @field:Size(max = 50, message = "Category must not exceed 50 characters")
    val category: String? = null,

    @field:Email(message = "Email must be valid")
    @field:NotBlank(message = "Email must not be blank")
    val email: String,

    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    @field:NotBlank
    val phone: String,

    val address: Address,
    val isActive: Boolean = true
)
