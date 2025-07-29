package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "stock_movements")
data class StockMovement(
    @Id
    val id: String? = null,
    
    val productId: String,
    
    val type: MovementType,
    
    val quantity: Int,
    
    val previousStock: Int,
    
    val newStock: Int,
    
    val unitPrice: BigDecimal? = null,
    
    val reason: String? = null,
    
    val referenceId: String? = null, // ID de la commande ou vente associée
    
    @field:CreatedDate
    val createdAt: Instant? = null,
    
    val performedBy: String
)

enum class MovementType {
    STOCK_IN,     // Entrée de stock
    STOCK_OUT,    // Sortie de stock  
    ADJUSTMENT,   // Ajustement manuel
    RETURN,       // Retour
    WASTE         // Perte/déchet
}
