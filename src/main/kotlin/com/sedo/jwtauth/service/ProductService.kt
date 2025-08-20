package com.sedo.jwtauth.service

import Product
import com.sedo.jwtauth.exception.ProductNotFoundException
import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import com.sedo.jwtauth.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

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


    fun getAllProductsOnPromotion(): List<Product> {
        logger.debug("Retrieving all products on promotion")
        return productRepository.findByIsOnPromotionTrueAndIsActiveTrue()
    }

    fun findByCategoryId(categoryId: String): List<Product> {
        logger.debug("Retrieving products for categoryId ID: {}", categoryId)
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId)
    }

    fun findBySupplierId(supplierId: String): List<Product> {
        logger.debug("Retrieving products for supplier ID: {}", supplierId)
        return productRepository.findBySupplierIdAndIsActiveTrue(supplierId)
    }

    fun findBySellingPriceRange(minPrice: BigDecimal, maxPrice: BigDecimal): List<Product> {
        logger.debug("Retrieving products with selling price between {} and {}", minPrice, maxPrice)
        return productRepository.findBySellingPriceBetween(minPrice, maxPrice)
    }

    fun getLowStockProducts(): List<Product> {
        logger.debug("Retrieving all low stock products")
        return productRepository.findLowStockProducts()
    }

    fun getOutOfStockProducts(): List<Product> {
        logger.debug("Retrieving all out of stock products")
        return productRepository.findOutOfStockProducts()
    }
    
    fun getProductById(id: String): Product {
        logger.debug("Retrieving product with ID: {}", id)
        return productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id)
    }

    fun getProductsExpiringIn(days: Long): List<Product> {
        val now = Instant.now()
        val targetDate = now.plus(days, DAYS)
        return productRepository.findByExpirationDateBetween(now, targetDate)
    }
    fun getProductsByNameOrSku(query: String): List<Product> {
        logger.debug("Searching products with query: {}", query)
        return productRepository.findByNameContainingIgnoreCaseOrSkuContainingIgnoreCaseAndIsActiveTrue(query)
    }

    fun getExpiredProducts(): List<Product> {
        val today = Instant.now()
        logger.debug("Retrieving expired products as of: {}", today)
        return productRepository.findByExpirationDateBefore(today)
    }
    
    fun createProduct(product: Product): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating product: {} by user: {}", product.name, currentUser)
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
    
    fun updateProduct(id: String, product: Product): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating product ID: {} by user: {}", id, currentUser)

        val existingProduct = getProductById(id)

        val oldData = mapOf(
            "name" to existingProduct.name,
            "sellingPrice" to existingProduct.sellingPrice.toString(),
            "stockQuantity" to existingProduct.stockQuantity.toString(),
            "minimumStock" to existingProduct.minStock.toString()
        )
        
        val updatedProduct = existingProduct.copy(
            name = product.name,
            description = product.description,
            sku = product.sku,
            categoryId = product.categoryId,
            supplierId = product.supplierId,
            purchasePrice = product.purchasePrice,
            sellingPrice = product.sellingPrice,
            stockQuantity = product.stockQuantity,
            minStock = product.minStock,
            unit = product.unit,
            expirationDate = product.expirationDate,
            images = product.images,
            isActive = product.isActive,
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
                "minimumStock" to savedProduct.minStock.toString()
            )
        )
        
        logger.info("Product updated successfully: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
    
    fun deleteProduct(id: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Deactivating product ID: {} by user: {}", id, currentUser)
        
        val existingProduct = getProductById(id)
        
        val deletedProduct = existingProduct.copy(isActive = false)
        
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

    fun getAllDeletedProducts(): List<Product> {
        logger.debug("Retrieving all deleted products")
        return productRepository.findByIsActiveFalse()
    }
    fun updateStock(productId: String, newQuantity: Int, reason: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating stock for product ID: {} to {} by user: {}", productId, newQuantity, currentUser)
        
        val existingProduct = getProductById(productId)
        val oldQuantity = existingProduct.stockQuantity
        
        val updatedProduct = existingProduct.copy(stockQuantity = newQuantity)
        
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
}
