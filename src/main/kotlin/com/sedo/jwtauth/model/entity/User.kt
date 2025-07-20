package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val username: String,
    val password: String,
    val roles: List<String>,
    @field:CreatedDate
    val createdAt: java.time.Instant? = null
)