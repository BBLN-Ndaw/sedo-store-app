package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "categories")
data class Category(
    @Id
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    @field:CreatedDate
    val createdAt: Instant? = null,
    @field:LastModifiedDate
    val updatedAt: Instant? = null,
)
