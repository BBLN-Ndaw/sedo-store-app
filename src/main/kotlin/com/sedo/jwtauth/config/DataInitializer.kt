package com.sedo.jwtauth.config

import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.CategoryRepository
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
    private val passwordEncoder: BCryptPasswordEncoder
) : CommandLineRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    override fun run(vararg args: String?) {
        logger.info("Initializing default data...")
        
        initializeUsers()
        initializeCategories()
        
        logger.info("Data initialization completed")
    }
    
    private fun initializeUsers() {
        logger.info("Initializing default users...")

        val defaultUsers = listOf(
            User(
                userName = "owner",
                password = passwordEncoder.encode("password"),
                roles = listOf("OWNER")
            ),
            User(
                userName = "employee",
                password = passwordEncoder.encode("password"),
                roles = listOf("EMPLOYEE")
            ),
            User(
                userName = "client",
                password = passwordEncoder.encode("password"),
                roles = listOf("CLIENT")
            )
        )
        
        defaultUsers.forEach { user ->
            if (userRepository.findByUsername(user.userName) == null) {
                userRepository.save(user)
                logger.info("Default {} user created", user.roles.joinToString())
            } else {
                logger.info("{} user already exists", user.roles.joinToString())
            }
        }
    }
    
    private fun initializeCategories() {
        logger.info("Initializing default categories...")
        
        // Vérifier si des catégories existent déjà
        if (categoryRepository.count() > 0) {
            logger.info("Categories already exist, skipping initialization")
            return
        }
        
        val now = Instant.now()
        
        // Catégories principales
        val alimentaire = categoryRepository.save(Category(
            name = "Alimentaire",
            description = "Produits alimentaires et boissons",
            isActive = true,
            createdAt = now,
            createdBy = "system"
        ))
        
        val electronique = categoryRepository.save(Category(
            name = "Électronique",
            description = "Appareils électroniques et accessoires",
            isActive = true,
            createdAt = now,
            createdBy = "system"
        ))
        
        val hygiene = categoryRepository.save(Category(
            name = "Hygiène & Beauté",
            description = "Produits d'hygiène et cosmétiques",
            isActive = true,
            createdAt = now,
            createdBy = "system"
        ))
        
        val maison = categoryRepository.save(Category(
            name = "Maison & Jardin",
            description = "Articles pour la maison et le jardin",
            isActive = true,
            createdAt = now,
            createdBy = "system"
        ))
        
        // Sous-catégories Alimentaire
        val sousCategories = listOf(
            Category(
                name = "Fruits & Légumes",
                description = "Fruits et légumes frais",
                parentCategoryId = alimentaire.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            Category(
                name = "Viandes & Poissons",
                description = "Produits carnés et poissons",
                parentCategoryId = alimentaire.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            Category(
                name = "Produits Laitiers",
                description = "Lait, fromages, yaourts",
                parentCategoryId = alimentaire.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            Category(
                name = "Boissons",
                description = "Boissons alcoolisées et non alcoolisées",
                parentCategoryId = alimentaire.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            // Sous-catégories Électronique
            Category(
                name = "Smartphones & Tablettes",
                description = "Téléphones et tablettes",
                parentCategoryId = electronique.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            Category(
                name = "Ordinateurs",
                description = "PC, laptops et accessoires",
                parentCategoryId = electronique.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            ),
            Category(
                name = "Électroménager",
                description = "Appareils électroménagers",
                parentCategoryId = electronique.id,
                isActive = true,
                createdAt = now,
                createdBy = "system"
            )
        )
        
        categoryRepository.saveAll(sousCategories)
        
        logger.info("Default categories created successfully")
    }
}
