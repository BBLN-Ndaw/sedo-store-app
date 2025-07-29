package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Product
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface ProductRepository : MongoRepository<Product, String> {
    fun findByIsActiveTrue(): List<Product>
    fun findByCategoryId(categoryId: String): List<Product>
    fun findBySupplierId(supplierId: String): List<Product>
    fun findByStockQuantityLessThanMinimumStock(): List<Product> // Produits en rupture
    fun findByNameContainingIgnoreCaseOrSkuContainingIgnoreCase(name: String, sku: String): List<Product>
    fun findByExpirationDateBefore(date: LocalDate): List<Product> // Produits proches de l'expiration
    fun findByTagsContaining(tag: String): List<Product>
}
