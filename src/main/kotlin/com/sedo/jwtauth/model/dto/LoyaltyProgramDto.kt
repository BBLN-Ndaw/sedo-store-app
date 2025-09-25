package com.sedo.jwtauth.model.dto

data class LoyaltyProgramDto(
    val level: String,
    val points: Int,
    val nextLevelPoints: Int,
    val benefits: List<String>,
    val progress: Double
)