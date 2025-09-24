package com.sedo.jwtauth.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

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
                subject = "Création de votre compte Sedo Store - Définir votre mot de passe"
                text = buildPasswordCreationEmailText(firstName, lastName, token)
            }

            mailSender.send(message)
            logger.info("Password creation email sent successfully to: {}", email)

        } catch (e: Exception) {
            logger.error("Failed to send password creation email to: {}", email, e)
            throw RuntimeException("Failed to send email", e)
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
            Le gestionnaire de Sedo Store
        """.trimIndent()
    }
}
