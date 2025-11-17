package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

/**
 * Service class responsible for managing product categories in the Store Management System.
 * 
 * This service provides comprehensive category management operations including:
 * - Category CRUD operations (Create, Read, Update, Delete)
 * - Category status management (active/inactive)
 * - Category retrieval by various criteria
 * - Integration with audit logging system
 * 
 * Business Logic:
 * - Categories are used to organize products into logical groups
 * - Each category has a name, description, and active status
 * - All category operations are audited for compliance and tracking
 * - Security context is maintained for all operations
 * 
 * Dependencies:
 * - CategoryRepository: Data access layer for category entities
 * - AuditService: Logging service for tracking category operations
 * - Spring Security: For user authentication and authorization
 *
 */
@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(CategoryService::class.java)
    
    /**
     * Retrieves all categories from the system.
     * 
     * @return List of all Category entities
     */
    fun getAllCategories(): List<Category> {
        logger.debug("Retrieving all categories")
        return categoryRepository.findAll()
    }

    /**
     * Retrieves multiple categories by their IDs.
     * Used for batch operations and product category validation.
     * 
     * @param ids Set of category IDs to retrieve
     * @return List of Category entities matching the provided IDs
     */
    fun getAllCategoriesByIdIn(ids: Set<String>): List<Category> {
        logger.debug("Retrieving categories by IDs: {}", ids)
        return categoryRepository.findAllByIdIn(ids)
    }
    
    /**
     * Retrieves a single category by its unique identifier.
     * 
     * @param id The unique identifier of the category
     * @return Category entity
     * @throws ResourceNotFoundException if category with given ID is not found
     */
    fun getCategoryById(id: String): Category {
        logger.debug("Retrieving category with ID: {}", id)
        return categoryRepository.findById(id).getOrNull()
            ?: throw ResourceNotFoundException("Category not found with ID: $id")
    }
    
    /**
     * Creates a new category in the system.
     * 
     * Business Rules:
     * - Category name must be unique
     * - All operations are audited with user information
     * - New categories are active by default
     * 
     * @param category The category data to create
     * @return The created Category entity with generated ID
     */
    fun createCategory(category: Category): Category {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        
        logger.info("Creating new category: {} by user: {}", category.name, currentUser)
        
        val category = Category(
            name = category.name,
            description = category.description,
            isActive = category.isActive,
        )
        
        val savedCategory = categoryRepository.save(category)
        
        auditService.logAction(
            userName = currentUser,
            action = "CREATE",
            entityType = "Category",
            entityId = savedCategory.id,
            description = "Created category: ${savedCategory.name}",
            newData = mapOf(
                "name" to savedCategory.name,
                "description" to (savedCategory.description ?: ""),
            )
        )
        
        logger.info("Category created successfully: {} (ID: {})", savedCategory.name, savedCategory.id)
        return savedCategory
    }
    
    /**
     * Updates an existing category with new information.
     * 
     * Business Rules:
     * - Category must exist before update
     * - All changes are tracked with old and new values
     * - Audit trail is maintained for compliance
     * 
     * @param id The unique identifier of the category to update
     * @param category The updated category data
     * @return The updated Category entity
     * @throws ResourceNotFoundException if category with given ID is not found
     */
    fun updateCategory(id: String, category: Category): Category {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val existingCategory = getCategoryById(id)
        
        logger.info("Updating category ID: {} by user: {}", id, currentUser)
        
        val oldData = mapOf(
            "name" to existingCategory.name,
            "description" to (existingCategory.description ?: ""),
        )
        
        val updatedCategory = existingCategory.copy(
            name = category.name,
            description = category.description,
            isActive = category.isActive,
        )
        
        val savedCategory = categoryRepository.save(updatedCategory)
        
        auditService.logAction(
            userName = currentUser,
            action = "UPDATE",
            entityType = "Category",
            entityId = savedCategory.id,
            description = "Updated category: ${savedCategory.name}",
            oldData = oldData,
            newData = mapOf(
                "name" to savedCategory.name,
                "description" to (savedCategory.description ?: ""),
            )
        )
        
        logger.info("Category updated successfully: {}", savedCategory.name)
        return savedCategory
    }

    /**
     * Updates the active status of a category.
     * 
     * Business Logic:
     * - Inactive categories cannot be assigned to new products
     * - Existing products keep their category assignments
     * - Status changes are audited for tracking
     * 
     * @param id The unique identifier of the category
     * @param action ActionDto containing the status action ("activate" or "deactivate")
     * @return The updated Category entity with new status
     * @throws ResourceNotFoundException if category with given ID is not found
     */
    fun updateCategoryStatus(id: String, action: ActionDto): Category {
        val status = action.value == "activate"
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val existingCategory = getCategoryById(id)

        logger.info("Updating status of category ID: {} to {} by user: {}", id, status, currentUser)

        val oldData = mapOf(
            "isActive" to existingCategory.isActive,
        )

        val updatedCategory = existingCategory.copy(
            isActive = status,
        )

        val savedCategory = categoryRepository.save(updatedCategory)

        auditService.logAction(
            userName = currentUser,
            action = "UPDATE_STATUS",
            entityType = "Category",
            entityId = savedCategory.id,
            description = "Updated status of category: ${savedCategory.name} to $status",
            oldData = oldData,
            newData = mapOf(
                "isActive" to savedCategory.isActive,
            )
        )

        logger.info("Category status updated successfully ID: {} to {}", id, status)
        return savedCategory
    }
}
