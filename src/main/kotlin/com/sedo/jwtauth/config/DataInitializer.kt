package com.sedo.jwtauth.config

import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
        logger.info("Initializing default users...")

        val defaultUsers = listOf(
            User(
                username = "admin",
                password = passwordEncoder.encode("password"),
                roles = listOf("ADMIN", "USER")
            ),
            User(
                username = "manager",
                password = passwordEncoder.encode("password"),
                roles = listOf("MANAGER", "USER")
            ),
            User(
                username = "user",
                password = passwordEncoder.encode("password"),
                roles = listOf("USER")
            )
        )
        defaultUsers.forEach { user ->
            if (userRepository.findByUsername(user.username) == null) {
                userRepository.save(user)
                logger.info("Default {} user created", user.roles.joinToString())
            } else {
                logger.info("{} user already exists", user.roles.joinToString())
            }
        }
        logger.info("Data initialization completed")
    }
}
