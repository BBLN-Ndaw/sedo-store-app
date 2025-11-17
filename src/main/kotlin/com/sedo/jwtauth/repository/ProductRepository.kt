package com.sedo.jwtauth.repository

import Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

/**
 * Repository interface for Product entity data access operations.
 *
 * This repository provides data access methods for product inventory management
 * using Spring Data MongoDB. It includes custom MongoDB queries for stock level
 * monitoring and inventory analysis.
 *
 * Features:
 * - Standard CRUD operations (inherited from MongoRepository)
 * - Low stock products identification using MongoDB aggregation
 * - In-stock products filtering for availability checks
 * - Custom MongoDB queries for inventory management
 *
 */
interface ProductRepository : MongoRepository<Product, String>{
    
    /**
     * Finds products with low stock levels.
     * Uses MongoDB expression to compare stockQuantity with minStock threshold.
     *
     * @return List of products where stock quantity is at or below minimum stock level
     */
    @Query("{ \$expr: { \$lte: [ \"\$stockQuantity\", \"\$minStock\" ] } }")
    fun findLowStockProducts(): List<Product>

    /**
     * Finds products that are currently in stock.
     * Uses MongoDB expression to identify products above minimum stock level.
     *
     * @return List of products with adequate stock levels
     */
    @Query("{ \$expr: { \$gt: [ \"\$stockQuantity\", \"\$minStock\" ] } }")
    fun findProductsInStock(): MutableList<Product>
}
