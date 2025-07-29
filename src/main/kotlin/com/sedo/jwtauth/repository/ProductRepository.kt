package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.Product
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDate

interface ProductRepository : MongoRepository<Product, String> {
    fun findByIsActiveTrue(): List<Product>
    fun findByCategoryIdAndIsActiveTrue(categoryId: String): List<Product>
    fun findBySupplierIdAndIsActiveTrue(supplierId: String): List<Product>
    fun findByStockQuantityLessThanAndIsActiveTrue(threshold: Int): List<Product>
    fun findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseAndIsActiveTrue(name: String, sku: String): List<Product>
    fun findByExpirationDateBeforeAndIsActiveTrue(date: LocalDate): List<Product>
    fun findByExpirationDateBetweenAndIsActiveTrue(startDate: LocalDate, endDate: LocalDate): List<Product>
}
