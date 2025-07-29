package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Category
import org.springframework.data.mongodb.repository.MongoRepository

interface CategoryRepository : MongoRepository<Category, String> {
    fun findByIsActiveTrue(): List<Category>
    fun findByParentCategoryIdIsNull(): List<Category> // Catégories principales
    fun findByParentCategoryId(parentId: String): List<Category> // Sous-catégories
    fun findByNameContainingIgnoreCase(name: String): List<Category>
}
