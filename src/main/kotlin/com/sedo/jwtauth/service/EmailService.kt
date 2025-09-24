package com.sedo.jwtauth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import com.sedo.jwtauth.model.entity.Order
import java.nio.charset.StandardCharsets

@Service
class EmailService @Autowired constructor(
    private val mailSender: JavaMailSender
) {

    private val logger = LoggerFactory.getLogger(EmailService::class.java)

    @Value("\${app.mail.from:sedosebe.store@gmail.com}")
    private lateinit var fromAddress: String

    @Value("\${app.backend.url:http://localhost:8080}")
    private lateinit var backendUrl: String

    fun sendPasswordCreationEmail(email: String, firstName: String, lastName: String, userName: String, token: String) {
        logger.info("Sending password creation email to: {}", email)

        try {
            val message = SimpleMailMessage().apply {
                setFrom(fromAddress)
                setTo(email)
                subject = "Création de votre compte PAYCE - Définir votre mot de passe"
                text = buildPasswordCreationEmailText(firstName, lastName, token)
            }

            mailSender.send(message)
            logger.info("Password creation email sent successfully to: {}", email)

        } catch (e: Exception) {
            logger.error("Failed to send password creation email to: {}", email, e)
            throw RuntimeException("Failed to send email", e)
        }
    }

    fun sendOrderConfirmationEmail(order: Order, invoicePdf: ByteArray) {
        logger.info("Sending order confirmation email to: {} for order: {}", order.customerEmail, order.orderNumber)

        try {
            val mimeMessage = mailSender.createMimeMessage()
            val helper = MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name())

            helper.setFrom(fromAddress)
            helper.setTo(order.customerEmail!!)
            helper.setSubject("Confirmation de commande #${order.orderNumber} - ${order.totalAmount}€")
            helper.setText(buildOrderConfirmationEmailText(order), false)

            // Ajouter la facture PDF en pièce jointe
            val pdfResource = ByteArrayResource(invoicePdf)
            helper.addAttachment("Facture_${order.orderNumber}.pdf", pdfResource)

            mailSender.send(mimeMessage)
            logger.info("Order confirmation email sent successfully to: {} for order: {}", order.customerEmail, order.orderNumber)

        } catch (e: Exception) {
            logger.error("Failed to send order confirmation email to: {} for order: {}", order.customerEmail, order.orderNumber, e)
            throw RuntimeException("Failed to send order confirmation email", e)
        }
    }

    private fun buildPasswordCreationEmailText(firstName: String, lastName: String, token: String): String {
        val passwordSetupUrl = "$backendUrl/api/auth/validate-token?token=$token"

        return """
            Bonjour $firstName $lastName,
            
            Votre compte a été créé avec succès !
            
            Pour accéder à votre compte, vous devez d'abord définir votre mot de passe en cliquant sur le lien ci-dessous :
            
            $passwordSetupUrl
            
            Ce lien est valide pendant 24 heures. Si le lien expire, veuillez contacter votre administrateur pour obtenir un nouveau lien.
            
            Une fois votre mot de passe défini, vous pourrez vous connecter à l'application avec vos identifiants.
            
            Cordialement,
            Le gestionnaire
        """.trimIndent()
    }

    private fun buildOrderConfirmationEmailText(order: Order): String {
        val itemsText = order.items.joinToString("\n") { item ->
            "- ${item.productName} x${item.quantity} - ${item.productUnitPrice * item.quantity.toBigDecimal()}€"
        }

        return """
            Bonjour ${order.customerUserName},

            Votre commande #${order.orderNumber} a été confirmée et le paiement a été traité avec succès !

            DÉTAILS DE LA COMMANDE :
            
            Articles commandés :
            $itemsText
            
            Sous-total HT : ${order.subtotal}€
            TVA : ${order.taxAmount}€
            Frais de port : ${order.shippingAmount}€
            TOTAL TTC : ${order.totalAmount}€
            
            ADRESSE DE LIVRAISON :
            ${order.shippingAddress?.let { 
                "${it.street}\n${it.postalCode} ${it.city}\n${it.country}" 
            } ?: "Non spécifiée"}
            
            Votre facture est disponible en pièce jointe de cet email.
            
            Date de livraison estimée : ${order.estimatedDeliveryDate?.let { 
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    .format(it.atZone(java.time.ZoneId.systemDefault())) 
            } ?: "À définir"}
            
            Vous pouvez suivre l'état de votre commande en vous connectant à votre compte.
            
            Merci pour votre confiance !
            
            Cordialement,
            L'équipe SEDO Store
            
            Pour toute question : sedosebe.store@gmail.com
        """.trimIndent()
    }
}
