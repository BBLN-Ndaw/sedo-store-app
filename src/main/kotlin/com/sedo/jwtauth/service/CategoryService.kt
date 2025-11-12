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

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(CategoryService::class.java)
    
    fun getAllCategories(): List<Category> {
        logger.debug("Retrieving all categories")
        return categoryRepository.findAll()
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
