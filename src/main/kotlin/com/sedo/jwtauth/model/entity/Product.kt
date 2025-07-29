package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

@Document(collection = "products")
data class Product(
    @Id
    val id: String? = null,
    
    @field:Indexed(unique = true)
    val sku: String, // Code produit unique
    
    val name: String,
    
    val description: String? = null,
    
    val categoryId: String,
    
    val supplierId: String,
    
    val purchasePrice: BigDecimal, // Prix d'achat
    
    val sellingPrice: BigDecimal, // Prix de vente
    
    val stockQuantity: Int = 0,
    
    val minimumStock: Int = 0, // Seuil d'alerte
    
    val imageUrls: List<String> = emptyList(),
    
    val isActive: Boolean = true,
    
    // Spécifique à l'alimentaire
    val expirationDate: LocalDate? = null,
    
    // Unité de mesure (kg, pièce, litre, etc.)
    val unit: String = "pièce",
    
    // Tags pour faciliter la recherche
    val tags: List<String> = emptyList(),
    
    @field:CreatedDate
    val createdAt: Instant? = null,
    
    @field:LastModifiedDate
    val updatedAt: Instant? = null,
    
    val createdBy: String? = null,
    val updatedBy: String? = null
)
