package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "user_loyalty")
data class UserLoyalty(
    @Id
    val id: String? = null,

    @Indexed(unique = true)
    val customerUserName: String,

    val level: LoyaltyLevel = LoyaltyLevel.BRONZE,

    val points: Int = 0,

    @field:CreatedDate
    var createdAt: Instant? = null,
    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version: Long? = null

)

enum class LoyaltyLevel(
    val displayName: String,
    val minPoints: Int,
    val nextLevelPoints: Int?,
    val benefits: List<String>
) {
    BRONZE(
        displayName = "Bronze",
        minPoints = 0,
        nextLevelPoints = 100,
        benefits = listOf("Livraison gratuite à partir de 50€")
    ),

    SILVER(
        displayName = "Silver",
        minPoints = 100,
        nextLevelPoints = 500,
        benefits = listOf(
            "Livraison gratuite sur toutes les commandes",
            "Retours gratuits sous 30 jours"
        )
    ),

    GOLD(
        displayName = "Gold",
        minPoints = 500,
        nextLevelPoints = null, // Niveau maximum
        benefits = listOf(
            "Livraison gratuite sur toutes les commandes",
            "Retours gratuits sous 30 jours",
            "Accès prioritaire aux ventes privées",
            "Support client prioritaire"
        )
    );

    companion object {
        /**
         * Trouve le niveau basé sur les points
         */
        fun fromPoints(points: Int): LoyaltyLevel {
            return LoyaltyLevel.entries
                .filter { points >= it.minPoints }
                .maxByOrNull { it.minPoints }
                ?: BRONZE
        }

        /**
         * Trouve le niveau suivant
         */
        fun getNextLevel(currentLevel: LoyaltyLevel): LoyaltyLevel? {
            return LoyaltyLevel.entries.find { it.minPoints > currentLevel.minPoints }
        }
    }
}