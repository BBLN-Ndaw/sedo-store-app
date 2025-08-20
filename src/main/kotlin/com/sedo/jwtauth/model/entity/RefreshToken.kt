package com.sedo.jwtauth.model.entity

import com.sedo.jwtauth.constants.Constants.Cookie.JWT_REFRESH_TOKEN_MAX_AGE
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "refresh_tokens")
data class RefreshToken(
    @Id val id: String? = null,
    val userName: String,
    val token: String,
    @field:Indexed(expireAfterSeconds = JWT_REFRESH_TOKEN_MAX_AGE)
    var createdAt: Instant = Instant.now()
)

