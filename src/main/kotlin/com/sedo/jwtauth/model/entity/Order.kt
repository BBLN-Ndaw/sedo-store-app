package com.sedo.jwtauth.model.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "orders")
data class Order(
    @Id
    val id: String? = null,
    
    val orderNumber: String, // Numéro de commande unique
    
    val customerId: String, // ID du client (User)
    
    val items: List<OrderItem>,
    
    val status: OrderStatus,
    
    val paymentMethod: PaymentMethod,
    
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    
    val subtotal: BigDecimal,
    
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    
    val totalAmount: BigDecimal,
    
    val notes: String? = null,
    
    val pickupDate: Instant? = null, // Date de retrait prévue
    
    val actualPickupDate: Instant? = null, // Date de retrait effective
    
    @field:CreatedDate
    val createdAt: Instant? = null,
    
    @field:LastModifiedDate
    val updatedAt: Instant? = null,
    
    val processedBy: String? = null // ID de l'employé qui traite la commande
)

data class OrderItem(
    val productId: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal
)

enum class OrderStatus {
    PENDING,      // En attente
    CONFIRMED,    // Confirmée
    PREPARING,    // En préparation
    READY,        // Prête pour retrait
    COMPLETED,    // Terminée
    CANCELLED     // Annulée
}

enum class PaymentMethod {
    CASH,         // Espèces
    CARD,         // Carte bancaire
    PAYPAL,       // PayPal
    BANK_TRANSFER // Virement
}

enum class PaymentStatus {
    PENDING,      // En attente
    PAID,         // Payé
    FAILED,       // Échec
    REFUNDED      // Remboursé
}
