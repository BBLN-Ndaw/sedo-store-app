package com.sedo.jwtauth.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(value = "jwt")
data class AuthProperties (
    val secret: String = "myDefaultSecretKeyForJwtTokenGeneration1234567890",
    val accessTokenExpiration: Long = 15 * 60 * 1000, // 15min by default
    val refreshTokenExpiration: Long = 24 * 60 * 1000, // 24 hours by default
)