package com.sedo.jwtauth.service

import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository
) : UserDetailsService {
    
    private val logger = LoggerFactory.getLogger(CustomUserDetailsService::class.java)

    override fun loadUserByUsername(username: String): UserDetails {
        logger.debug("Loading user details for: {}", username)
        val user = userRepository.findByUsername(username)
            ?: run {
                logger.error("User not found in UserDetailsService: {}", username)
                throw UsernameNotFoundException("User not found: $username")
            }
        
        logger.debug("User details loaded successfully for: {}", username)
        return CustomUserDetails(user)
    }
}
