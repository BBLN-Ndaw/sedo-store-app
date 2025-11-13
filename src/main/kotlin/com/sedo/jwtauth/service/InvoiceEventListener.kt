package com.sedo.jwtauth.service

import com.sedo.jwtauth.event.InvoiceGenerationRequestedEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

/**
 * listener responsible for handling invoice generation events
 */
@Service
class InvoiceEventListener(
    private val invoicePdfService: InvoicePdfService,
    private val emailService: EmailService
) {
    
    private val logger = LoggerFactory.getLogger(InvoiceEventListener::class.java)

    /**
     * Listen to invoice generation requests
     */
    @EventListener
    fun onInvoiceGenerationRequested(event: InvoiceGenerationRequestedEvent) {
        try {
            logger.info("Processing invoice generation for order: {}", event.order.orderNumber)
            
            val invoicePdf = invoicePdfService.generateInvoicePdf(event.order, event.payerFullName)
            
            emailService.sendOrderConfirmationEmail(event.order, invoicePdf)
            
            logger.info("Invoice PDF generated and sent successfully for order: {}", event.order.orderNumber)
            
        } catch (e: Exception) {
            logger.error("Failed to generate or send invoice PDF for order: {}", event.order.orderNumber, e)
        }
    }
}