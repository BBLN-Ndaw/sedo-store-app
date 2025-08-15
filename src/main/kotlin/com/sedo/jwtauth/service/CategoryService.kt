package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(CategoryService::class.java)
    
    fun getAllCategories(): List<Category> {
        logger.debug("Retrieving all categories")
        return categoryRepository.findByIsActiveTrue()
    }

    fun getAllCategoriesByIdIn(ids: Set<String>): List<Category> {
        logger.debug("Retrieving categories by IDs: {}", ids)
        return categoryRepository.findAllByIdIn(ids)
    }
    
    fun getCategoryById(id: String): Category {
        logger.debug("Retrieving category with ID: {}", id)
        return categoryRepository.findById(id).getOrNull()
            ?: throw ResourceNotFoundException("Category not found with ID: $id")
    }

    fun getCategoryByName(name: String): Category? {
        logger.debug("Searching categories by name: {}", name)
        return categoryRepository.findByNameContainingIgnoreCase(name)
    }
    
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
    
    fun deleteCategory(id: String) {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val category = getCategoryById(id)
        
        logger.info("Deleting category ID: {} by user: {}", id, currentUser)
        
        // Soft delete
        val deletedCategory = category.copy(
            isActive = false,
        )
        
        categoryRepository.save(deletedCategory)
        
        auditService.logAction(
            userName = currentUser,
            action = "DELETE",
            entityType = "Category",
            entityId = deletedCategory.id,
            description = "Deleted category: ${deletedCategory.name}"
        )
        
        logger.info("Category deleted successfully ID: {}", id)
    }
    
    fun getAllDeletedCategories(): List<Category> {
        logger.debug("Retrieving all deleted categories")
        return categoryRepository.findByIsActiveFalse()
    }
}
