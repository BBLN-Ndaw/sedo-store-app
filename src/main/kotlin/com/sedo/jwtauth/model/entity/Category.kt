package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * Category entity representing product categorization in the Store Management System.
 *
 * This entity manages product categories for organizing and filtering products
 * in the store catalog. Categories help with product discovery, navigation,
 * and inventory management.
 *
 * Features:
 * - Unique category names with indexing
 * - Soft delete capability through isActive flag
 * - Audit timestamps for creation and modification
 * - Optimistic locking for concurrent updates
 *
 * @property id Unique identifier (MongoDB ObjectId)
 * @property name Unique category name (indexed)
 * @property description Optional detailed category description
 * @property isActive Category status for soft delete functionality
 * @property createdAt Timestamp when category was created
 * @property updatedAt Timestamp when category was last modified
 * @property version Optimistic locking version field
 *
 */
@Document(collection = "categories")
data class Category(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    @field:CreatedDate
    var createdAt: Instant? = null,
    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version : Long? = null
)
