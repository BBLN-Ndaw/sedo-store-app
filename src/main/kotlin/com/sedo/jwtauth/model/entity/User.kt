package com.sedo.jwtauth.model.entity

import com.sedo.jwtauth.model.dto.Address
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val userName: String,
    val password: String,
    val email: String,
    val firstName: String,
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