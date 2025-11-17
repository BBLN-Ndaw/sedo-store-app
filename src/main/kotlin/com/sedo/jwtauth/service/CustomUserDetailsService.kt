package com.sedo.jwtauth.service

import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * Custom implementation of Spring Security's UserDetailsService interface.
 * 
 * This service is responsible for loading user-specific data during authentication process.
 * It integrates the application's User entity with Spring Security's authentication mechanism.
 * 
 * Business Logic:
 * - Loads user information from the database using username
 * - Converts application User entity to Spring Security UserDetails
 * - Handles user not found scenarios with appropriate exceptions
 * - Provides integration between custom user model and Spring Security
 * 
 * Security Integration:
 * - Used by Spring Security's authentication providers
 * - Called during JWT token validation
 * - Integrates with the overall authentication flow
 * 
 * Dependencies:
 * - UserRepository: Data access layer for user entities
 * - CustomUserDetails: Custom implementation of UserDetails interface
 *
 */
@Service
class CustomUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    private val logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    /**
     * Loads user details by username for Spring Security authentication.
     * 
     * This method is called by Spring Security during the authentication process
     * to retrieve user information including credentials and authorities.
     * 
     * Authentication Flow:
     * 1. Receives username from authentication request
     * 2. Queries database for user information
     * 3. Converts User entity to UserDetails object
     * 4. Returns UserDetails for further authentication processing
     * 
     * @param username The username (login identifier) of the user to load
     * @return UserDetails object containing user information and authorities
     * @throws UsernameNotFoundException if no user is found with the given username
     */
    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Loading user details for: {}", username)
        val user = userRepository.findByUserName(username)
            ?: run {
                logger.error("User not found in UserDetailsService: {}", username)
                throw UsernameNotFoundException("User not found: $username")
            }
        
        logger.debug("User details loaded successfully for: {}", username)
        return CustomUserDetails(user)
    }
}
