package com.sedo.jwtauth.model.entity

import com.sedo.jwtauth.constants.Constants.Order.TAX
import com.sedo.jwtauth.model.dto.Address
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "orders")
data class Order(
    @Id
    val id: String? = null,
    @field:Indexed(unique = true)
    val orderNumber: String,
    val customerUserName: String,
    val customerEmail: String? = null,
    val customerNumTel: String? = null,
    val status: OrderStatus,
    val totalAmount: BigDecimal,
    val subtotal: BigDecimal,
    val shippingAmount: BigDecimal, // Montant des frais de livraison
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val shippingAddress: Address? = null,
    val billingAddress: Address? = null,
    val estimatedDeliveryDate: Instant? = null,
    val notes: String? = null,
    @field:Indexed(unique = true)
    val paymentOrderId: String? = null, // ID de la transaction de paiement associée
    val items: List<OrderItem>,
    val pickupDate: Instant? = null, // Date de retrait/livraison effective
    val processedByUser: String? = null, // nom de la personne qui traite la commande
    val paymentMethod: PaymentMethod = PaymentMethod.CASH_ON_DELIVERY,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    @field:CreatedDate
    var createdAt: Instant? = null,
    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version: Long? = null
)

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val image: String? = null,
    val productUnitPrice: BigDecimal,
    val productTaxRate: BigDecimal = TAX,
)

enum class OrderStatus {
    PENDING, //Commande créée mais pas encore payée (validation panier)
    CONFIRMED, // paiement validé, commande à traité par le vendeur
    PROCESSING, //Le commerçant prépare la commande
    READY_FOR_PICKUP, //Dans le cas d’un Click & Collect
    SHIPPED, //Colis remis au transporteur,
    DELIVERED, // Le client a bien reçu son colis (preuve de livraison ou confirmation client).
    CANCELLED //Annulée par le client ou par le commerçant
}

enum class PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER,
    CASH_ON_DELIVERY
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED,
    REFUNDED
}
