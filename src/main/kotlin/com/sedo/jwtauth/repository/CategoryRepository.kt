package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Category
import org.springframework.data.mongodb.repository.MongoRepository

interface CategoryRepository : MongoRepository<Category, String> {
    fun findByIsActiveTrue(): List<Category>
    fun findByIsActiveFalse(): List<Category>
    fun findByNameContainingIgnoreCase(name: String): Category?
    fun findAllByIdIn(ids: Collection<String>): List<Category>
}
