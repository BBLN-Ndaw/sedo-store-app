package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Supplier
import org.springframework.data.mongodb.repository.MongoRepository

interface SupplierRepository : MongoRepository<Supplier, String> {
    fun findByIsActiveTrue(): List<Supplier>
    fun findByNameContainingIgnoreCase(name: String): List<Supplier>
}
