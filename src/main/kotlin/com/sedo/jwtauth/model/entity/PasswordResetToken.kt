package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "password_reset_tokens")
data class PasswordResetToken(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val token: String,
    val userId: String,
    @field:Indexed(expireAfterSeconds = 86400)
    val expiryDate: Instant,
    var used: Boolean = false,
)
