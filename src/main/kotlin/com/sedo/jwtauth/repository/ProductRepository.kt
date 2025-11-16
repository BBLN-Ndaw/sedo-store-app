package com.sedo.jwtauth.repository

import Product
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface ProductRepository : MongoRepository<Product, String>{
    @Query("{ \$expr: { \$lte: [ \"\$stockQuantity\", \"\$minStock\" ] } }")
    fun findLowStockProducts(): List<Product>

    @Query("{ \$expr: { \$gt: [ \"\$stockQuantity\", \"\$minStock\" ] } }")
    fun findProductsInStock(): MutableList<Product>
}
