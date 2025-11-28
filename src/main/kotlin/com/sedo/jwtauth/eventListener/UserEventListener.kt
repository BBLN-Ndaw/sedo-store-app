package com.sedo.jwtauth.eventListener

import com.sedo.jwtauth.event.CreateUserRequestEvent
import com.sedo.jwtauth.service.EmailService
import com.sedo.jwtauth.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

/**
 * Listener for user-related events
 */
@Service
class UserEventListener(private val emailService: EmailService,
                        private val userService: UserService,) {
    private val logger = LoggerFactory.getLogger(UserEventListener::class.java)

   /**
     * Handle user registration email sending
     */
    @EventListener
    fun handleUserRegistrationEmail(createUserRequestEvent: CreateUserRequestEvent) {
        try {
            emailService.sendPasswordCreationEmail(
                    userName = createUserRequestEvent.userName,
                    email = createUserRequestEvent.email,
                    firstName = createUserRequestEvent.firstName,
                    lastName = createUserRequestEvent.lastName,
                    token = createUserRequestEvent.token
            )
        }
        catch (e: Exception) {
            logger.warn(e.message)
            userService.deleteByUserName(createUserRequestEvent.userName)
            throw e // Rethrow to indicate failure
        }
    }

}