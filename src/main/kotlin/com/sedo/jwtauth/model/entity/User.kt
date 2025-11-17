package com.sedo.jwtauth.model.entity

import com.sedo.jwtauth.model.dto.Address
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

/**
 * User entity representing system users in the Store Management System.
 *
 * This entity stores user information including authentication credentials,
 * personal details, and role-based access control information. It supports
 * the complete user lifecycle from registration to deletion.
 *
 * Features:
 * - Unique username and email constraints
 * - Role-based access control (ADMIN, EMPLOYEE, CLIENT)
 * - Account activation/deactivation
 * - Audit timestamps for creation and modification
 * - MongoDB indexing for performance optimization
 *
 * @property id Unique identifier (MongoDB ObjectId)
 * @property userName Unique username for authentication
 * @property password Encrypted password using BCrypt
 * @property email Indexed email address for user identification
 * @property firstName User's first name (indexed for search)
 * @property lastName User's last name (indexed for search)
 * @property address Physical address information
 * @property numTel Optional phone number
 * @property isActive Account status (active/inactive)
 * @property roles List of user roles for authorization
 * @property createdAt Timestamp when user was created
 * @property updatedAt Timestamp when user was last modified
 * @property version Optimistic locking version field
 *
 */
@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val userName: String,
    val password: String,
    @field:Indexed
    val email: String,
    @field:Indexed
    val firstName: String,
    @field:Indexed
    val lastName: String,
    val address: Address,
    val numTel: String? = null,
    val isActive: Boolean,
    val roles: List<String>,
    @field:CreatedDate
    var createdAt: Instant? = null,
    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version: Long? = null
)