package com.sedo.jwtauth.event

import com.sedo.jwtauth.model.entity.Order

/**
 *  Event emitted when an invoice generation is requested for an order after payment completion.
 *  @param order The order for which the invoice is to be generated.
 *  @param payerFullName The full name of the payer.
 *  @param payerEmail The email address of the payer.
 */
data class InvoiceGenerationRequestedEvent(
    val order: Order,
    val payerFullName: String,
    val payerEmail: String
)