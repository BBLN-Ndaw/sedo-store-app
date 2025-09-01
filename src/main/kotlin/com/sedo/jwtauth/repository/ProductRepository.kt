package com.sedo.jwtauth.repository

import Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import java.math.BigDecimal
import java.time.Instant

interface ProductRepository : MongoRepository<Product, String> {
    fun findByIsActiveTrue(): List<Product>
    fun findByIsActiveFalse(): List<Product>
    fun findAllByIdIn(ids: Collection<String>): List<Product>
    fun findByIsOnPromotionTrueAndIsActiveTrue(): List<Product>
    fun findByCategoryIdAndIsActiveTrue(categoryId: String): List<Product>
    fun findBySupplierIdAndIsActiveTrue(supplierId: String): List<Product>
    fun findBySellingPriceBetween(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product>
    @Query("{ 'isActive': true, \$expr: { \$lte: ['\$stockQuantity', '\$minStock'] } }")
    fun findLowStockProducts(): List<Product>
    @Query("{ 'stockQuantity': 0, 'isActive': true }")
    fun findOutOfStockProducts(): List<Product>
    fun findByExpirationDateBetween(start: Instant, end: Instant): List<Product>
    fun findByExpirationDateBefore(now: Instant): List<Product>
    fun findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseAndIsActiveTrue(query: String): List<Product>
}
