package com.sedo.jwtauth.config

import Product
import com.sedo.jwtauth.model.dto.Address
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.CategoryRepository
import com.sedo.jwtauth.repository.ProductRepository
import com.sedo.jwtauth.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class DataInitializer @Autowired constructor(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
        logger.info("Initializing default data...")
        
        initializeUsers()
        initializeCategories()
        initializeProducts()
        
        logger.info("Data initialization completed")
    }
    
    private fun initializeUsers() {
        logger.info("Initializing default users...")

        val defaultUsers = listOf(
            User(
                userName = "owner",
                password = passwordEncoder.encode("password"),
                email = "Dupond@gmail.com",
                firstName = "Dupond",
                lastName = "MARCEL",
                numTel = "0723456789",
                address = Address("1 rue de la Paix", "Paris", "75001", "France"),
                isActive = true,
                roles = listOf("ADMIN")
            ),
            User(
                userName = "employee",
                password = passwordEncoder.encode("password"),
                email = "Olivier@gmail.com",
                firstName = "Olivier",
                lastName = "Dupont",
                numTel = "0623456789",
                address = Address("1 rue Marcelin Berthelot", "Aubervilliers", "93300", "France"),
                isActive = true,
                roles = listOf("EMPLOYEE")
            ),
            User(
                userName = "customer",
                password = passwordEncoder.encode("password"),
                email = "marie@gmail.com",
                firstName = "Marie",
                lastName = "CLAIRE",
                numTel = "0789456702",
                address = Address("15 rue de la Mouet", "Paris", "75013", "France"),
                isActive = true,
                roles = listOf("CUSTOMER")
            )
        )
        
        defaultUsers.forEach { user ->
            if (userRepository.findByUserName(user.userName) == null) {
                userRepository.save(user)
                logger.info("Default {} user created", user.roles.joinToString())
            } else {
                logger.info("{} user already exists", user.roles.joinToString())
            }
        }

    }

    private fun initializeCategories() {
        mockCategories.forEach { category ->
            if (categoryRepository.findByNameContainingIgnoreCase(category.name) == null) {
                categoryRepository.save(category)
                logger.info("Default category '{}' created", category.name)
            } else {
                logger.info("Category '{}' already exists", category.name)
            }
        }
    }

    fun initializeProducts() {
        mockProducts.forEach { product ->
            if (productRepository.findById(product.id!!).isEmpty) {
                productRepository.save(product)
                logger.info("Default product '{}' created", product.name)
            } else {
                logger.info("Product '{}' already exists", product.name)
            }

        }
    }
}
