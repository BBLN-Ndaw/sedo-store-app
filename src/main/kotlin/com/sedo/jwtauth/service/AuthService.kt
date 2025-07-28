package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.InvalidCredentialsException
import com.sedo.jwtauth.exception.UserNotFoundException
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.repository.UserRepository
import com.sedo.jwtauth.util.JwtUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService @Autowired constructor(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: BCryptPasswordEncoder
) {
    
    private val logger = LoggerFactory.getLogger(AuthService::class.java)

    fun authenticate(user: LoginUserDto): String {
        logger.info("Authentication attempt for user: {}", user.username)
        
        val retrievedUser = userRepository.findByUsername(user.username)
            ?: run {
                logger.warn("User not found: {}", user.username)
                throw UserNotFoundException(user.username)
            }
        
        if (!passwordEncoder.matches(user.password, retrievedUser.password))  {
            logger.warn("Incorrect password for user: {}", user.username)
            throw InvalidCredentialsException()
        }
        
        logger.info("Authentication successful for user: {}", user.username)
        return jwtUtil.generateToken(user.username, retrievedUser.roles)
    }

}
