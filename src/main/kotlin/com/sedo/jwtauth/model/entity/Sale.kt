package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "sales")
data class Sale(
    @Id
    val id: String? = null,
    
    val saleNumber: String, // Numéro de vente unique
    
    val customerId: String? = null, // Client (optionnel pour vente directe)
    
    val items: List<SaleItem>,
    
    val paymentMethod: PaymentMethod,
    
    val subtotal: BigDecimal,
    
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    
    val totalAmount: BigDecimal,
    
    val cashReceived: BigDecimal? = null, // Montant reçu en espèces
    
    val changeAmount: BigDecimal? = null, // Monnaie rendue
    
    @field:CreatedDate
    val createdAt: Instant? = null,
    
    val processedBy: String // ID de l'employé qui fait la vente
)

data class SaleItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val totalPrice: BigDecimal
)
