package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Supplier
import org.springframework.data.mongodb.repository.MongoRepository

interface SupplierRepository : MongoRepository<Supplier, String> {
    fun findByIsActiveTrue(): List<Supplier>
    fun findByCategoryContainingIgnoreCase(category: String): List<Supplier>
    fun findByNameContainingIgnoreCaseAndIsActiveTrue(name: String): List<Supplier>
}
