package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ProductNotFoundException
import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.model.entity.Product
import com.sedo.jwtauth.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(ProductService::class.java)
    
    fun getAllProducts(): List<Product> {
        logger.debug("Retrieving all products")
        return productRepository.findByIsActiveTrue()
    }
    
    fun getProductById(id: String): Product {
        logger.debug("Retrieving product with ID: {}", id)
        return productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id)
    }
    
    fun getProductsByCategory(categoryId: String): List<Product> {
        logger.debug("Retrieving products for category: {}", categoryId)
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId)
    }
    
    fun getProductsBySupplier(supplierId: String): List<Product> {
        logger.debug("Retrieving products for supplier: {}", supplierId)
        return productRepository.findBySupplierIdAndIsActiveTrue(supplierId)
    }
    
    fun getLowStockProducts(threshold: Int = 10): List<Product> {
        logger.debug("Retrieving products with stock below: {}", threshold)
        return productRepository.findByStockQuantityLessThanAndIsActiveTrue(threshold)
    }
    
    fun getExpiredProducts(): List<Product> {
        val today = LocalDate.now()
        logger.debug("Retrieving expired products as of: {}", today)
        return productRepository.findByExpirationDateBeforeAndIsActiveTrue(today)
    }
    
    fun getExpiringProducts(days: Long = 7): List<Product> {
        val futureDate = LocalDate.now().plusDays(days)
        logger.debug("Retrieving products expiring before: {}", futureDate)
        return productRepository.findByExpirationDateBetweenAndIsActiveTrue(LocalDate.now(), futureDate)
    }
    
    fun createProduct(productDto: ProductDto): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating product: {} by user: {}", productDto.name, currentUser)
        
        val product = Product(
            name = productDto.name,
            description = productDto.description,
            sku = productDto.sku,
            categoryId = productDto.categoryId,
            supplierId = productDto.supplierId,
            purchasePrice = productDto.purchasePrice,
            sellingPrice = productDto.sellingPrice,
            stockQuantity = productDto.stockQuantity,
            minimumStock = productDto.minimumStock,
            unit = productDto.unit,
            expirationDate = productDto.expirationDate,
            imageUrls = productDto.imageUrls,
            tags = productDto.tags,
            isActive = productDto.isActive,
            createdAt = Instant.now()
        )
        
        val savedProduct = productRepository.save(product)
        
        auditService.logAction(
            userName = currentUser,
            action = "CREATE",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Created product: ${savedProduct.name}",
            newData = mapOf(
                "name" to savedProduct.name,
                "sku" to savedProduct.sku,
                "sellingPrice" to savedProduct.sellingPrice.toString(),
                "stockQuantity" to savedProduct.stockQuantity.toString()
            )
        )
        
        logger.info("Product created successfully: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
    
    fun updateProduct(id: String, productDto: ProductDto): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating product ID: {} by user: {}", id, currentUser)
        
        val existingProduct = getProductById(id)
        
        val oldData = mapOf(
            "name" to existingProduct.name,
            "sellingPrice" to existingProduct.sellingPrice.toString(),
            "stockQuantity" to existingProduct.stockQuantity.toString(),
            "minimumStock" to existingProduct.minimumStock.toString()
        )
        
        val updatedProduct = existingProduct.copy(
            name = productDto.name,
            description = productDto.description,
            sku = productDto.sku,
            categoryId = productDto.categoryId,
            supplierId = productDto.supplierId,
            purchasePrice = productDto.purchasePrice,
            sellingPrice = productDto.sellingPrice,
            stockQuantity = productDto.stockQuantity,
            minimumStock = productDto.minimumStock,
            unit = productDto.unit,
            expirationDate = productDto.expirationDate,
            imageUrls = productDto.imageUrls,
            tags = productDto.tags,
            isActive = productDto.isActive,
            updatedAt = Instant.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        
        auditService.logAction(
            userName = currentUser,
            action = "UPDATE",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Updated product: ${savedProduct.name}",
            oldData = oldData,
            newData = mapOf(
                "name" to savedProduct.name,
                "sellingPrice" to savedProduct.sellingPrice.toString(),
                "stockQuantity" to savedProduct.stockQuantity.toString(),
                "minimumStock" to savedProduct.minimumStock.toString()
            )
        )
        
        logger.info("Product updated successfully: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
    
    fun deleteProduct(id: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Deactivating product ID: {} by user: {}", id, currentUser)
        
        val existingProduct = getProductById(id)
        
        // Soft delete - on désactive au lieu de supprimer
        val deletedProduct = existingProduct.copy(
            isActive = false,
            updatedAt = Instant.now()
        )
        
        productRepository.save(deletedProduct)
        
        auditService.logAction(
            userName = currentUser,
            action = "DELETE",
            entityType = "Product",
            entityId = deletedProduct.id,
            description = "Deactivated product: ${deletedProduct.name}"
        )
        
        logger.info("Product deactivated successfully: {} (ID: {})", deletedProduct.name, deletedProduct.id)
        return deletedProduct
    }
    
    fun updateStock(productId: String, newQuantity: Int, reason: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating stock for product ID: {} to {} by user: {}", productId, newQuantity, currentUser)
        
        val existingProduct = getProductById(productId)
        val oldQuantity = existingProduct.stockQuantity
        
        val updatedProduct = existingProduct.copy(
            stockQuantity = newQuantity,
            updatedAt = Instant.now()
        )
        
        val savedProduct = productRepository.save(updatedProduct)
        
        auditService.logAction(
            userName = currentUser,
            action = "STOCK_UPDATE",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Stock updated for ${savedProduct.name}: $oldQuantity -> $newQuantity ($reason)",
            oldData = mapOf("stockQuantity" to oldQuantity.toString()),
            newData = mapOf("stockQuantity" to newQuantity.toString(), "reason" to reason)
        )
        
        logger.info("Stock updated successfully for product: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
    
    fun searchProducts(query: String): List<Product> {
        logger.debug("Searching products with query: {}", query)
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseAndIsActiveTrue(query, query)
    }
}
