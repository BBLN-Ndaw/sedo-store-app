package com.sedo.jwtauth.event

import com.sedo.jwtauth.model.entity.Order

/**
 * Événement déclenché quand une facture PDF doit être générée et envoyée par email
 * Suite à la confirmation d'une commande PayPal
 */
data class InvoiceGenerationRequestedEvent(
    val order: Order,
    val payerFullName: String,
    val payerEmail: String
)