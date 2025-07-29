package com.sedo.jwtauth.model.dto

import com.sedo.jwtauth.model.entity.Address
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
    val email: String? = null,

    @field:Size(max = 20, message = "Phone number must not exceed 20 characters")
    val phone: String? = null,

    val address: Address? = null,
    val isActive: Boolean = true
)
