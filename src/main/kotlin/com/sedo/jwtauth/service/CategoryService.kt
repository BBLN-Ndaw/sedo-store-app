package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.dto.CategoryDto
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.repository.CategoryRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
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
    
    fun getCategoryById(id: String): Category {
        logger.debug("Retrieving category with ID: {}", id)
        return categoryRepository.findById(id).getOrNull()
            ?: throw ResourceNotFoundException("Category not found with ID: $id")
    }
    
    fun getMainCategories(): List<Category> {
        logger.debug("Retrieving main categories")
        return categoryRepository.findByParentCategoryIdIsNull()
    }
    
    fun getSubCategories(parentId: String): List<Category> {
        logger.debug("Retrieving subcategories for parent ID: {}", parentId)
        return categoryRepository.findByParentCategoryId(parentId)
    }
    
    fun createCategory(categoryDto: CategoryDto): Category {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        
        logger.info("Creating new category: {} by user: {}", categoryDto.name, currentUser)
        
        val category = Category(
            name = categoryDto.name,
            description = categoryDto.description,
            parentCategoryId = categoryDto.parentCategoryId,
            isActive = categoryDto.isActive,
            createdAt = Instant.now(),
            createdBy = currentUser
        )
        
        val savedCategory = categoryRepository.save(category)
        
        auditService.logAction(
            userId = currentUser,
            action = "CREATE",
            entityType = "Category",
            entityId = savedCategory.id,
            description = "Created category: ${savedCategory.name}",
            newData = mapOf(
                "name" to savedCategory.name,
                "description" to (savedCategory.description ?: ""),
                "parentCategoryId" to (savedCategory.parentCategoryId ?: "")
            )
        )
        
        logger.info("Category created successfully: {} (ID: {})", savedCategory.name, savedCategory.id)
        return savedCategory
    }
    
    fun updateCategory(id: String, categoryDto: CategoryDto): Category {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val existingCategory = getCategoryById(id)
        
        logger.info("Updating category ID: {} by user: {}", id, currentUser)
        
        val oldData = mapOf(
            "name" to existingCategory.name,
            "description" to (existingCategory.description ?: ""),
            "parentCategoryId" to (existingCategory.parentCategoryId ?: "")
        )
        
        val updatedCategory = existingCategory.copy(
            name = categoryDto.name,
            description = categoryDto.description,
            parentCategoryId = categoryDto.parentCategoryId,
            isActive = categoryDto.isActive,
            updatedAt = Instant.now(),
            updatedBy = currentUser
        )
        
        val savedCategory = categoryRepository.save(updatedCategory)
        
        auditService.logAction(
            userId = currentUser,
            action = "UPDATE",
            entityType = "Category",
            entityId = savedCategory.id,
            description = "Updated category: ${savedCategory.name}",
            oldData = oldData,
            newData = mapOf(
                "name" to savedCategory.name,
                "description" to (savedCategory.description ?: ""),
                "parentCategoryId" to (savedCategory.parentCategoryId ?: "")
            )
        )
        
        logger.info("Category updated successfully: {}", savedCategory.name)
        return savedCategory
    }
    
    fun deleteCategory(id: String) {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        val category = getCategoryById(id)
        
        logger.info("Deleting category ID: {} by user: {}", id, currentUser)
        
        // Soft delete - marque comme inactif au lieu de supprimer
        val deletedCategory = category.copy(
            isActive = false,
            updatedAt = Instant.now(),
            updatedBy = currentUser
        )
        
        categoryRepository.save(deletedCategory)
        
        auditService.logAction(
            userId = currentUser,
            action = "DELETE",
            entityType = "Category",
            entityId = deletedCategory.id,
            description = "Deleted category: ${deletedCategory.name}"
        )
        
        logger.info("Category deleted successfully ID: {}", id)
    }
    
    fun searchCategories(query: String): List<Category> {
        logger.debug("Searching categories with query: {}", query)
        return categoryRepository.findByNameContainingIgnoreCase(query)
    }
}
