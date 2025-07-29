package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "suppliers")
data class Supplier(
    @Id
    val id: String? = null,

    @field:Indexed(unique = true)
    val name: String,

    val contactPersonName: String? = null,

    val category: String? = null,

    val email: String? = null,

    val phone: String? = null,

    val address: Address? = null,

    val isActive: Boolean = true,

    @field:CreatedDate
    val createdAt: Instant? = null,

    @field:LastModifiedDate
    val updatedAt: Instant? = null,

    val createdBy: String? = null,
    val updatedBy: String? = null
)

data class Address(
    val street: String,
    val city: String,
    val postalCode: String,
    val country: String = "France"
)
