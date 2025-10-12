package com.sedo.jwtauth.service

import Product
import com.sedo.jwtauth.exception.ProductNotFoundException
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import com.sedo.jwtauth.model.entity.User
import com.sedo.jwtauth.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault
import java.time.temporal.ChronoUnit.DAYS

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val auditService: AuditService,
    private val categoryService: CategoryService,
    private val mongoTemplate: MongoTemplate
    ) {
    
    private val logger = LoggerFactory.getLogger(ProductService::class.java)
    
    fun getAllProducts(): List<Product> {
        logger.debug("Retrieving all products")
        return productRepository.findByIsActiveTrue()
    }



    fun getProductsWithCategories(search: String?, isActive: String?,
                                  categoryId: String?, supplierId: String?,
                                  isOnPromotion: String?, minPrice:String?,
                                  maxPrice: String?, isLowStock: String?,
                                  isInStock: String?, isOutOfStock: String?,
                                  page: Int, size: Int): Page<ProductWithCategoryDto> {
        logger.debug("Retrieving products with categories and filters - search: {}, isActive: {}, categoryId: {}, supplierId: {}, isOnPromotion: {}, minPrice: {}, maxPrice: {}, isLowStock: {}, isInStock: {}, isOutOfStock: {}, page: {}, size: {}",
            search, isActive, categoryId, supplierId, isOnPromotion, minPrice, maxPrice, isLowStock, isInStock, isOutOfStock, page, size)

        val query = createSearchQuery(search, isActive, categoryId, supplierId, isOnPromotion, minPrice, maxPrice, isLowStock, isInStock, isOutOfStock)

        val pageable: Pageable = PageRequest.of(page, size)
        query.with(pageable)

        val products = mongoTemplate.find(query, Product::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Product::class.java)

        logger.info("Number of products found: {}", total)

        val categoryIds = products.map { it.categoryId }.toSet()

        val productsWithCategory = mapToProductWithCategoryDto(products, categoryIds)
        return PageImpl(productsWithCategory, pageable, total)
    }

    private fun mapToProductWithCategoryDto(products: List<Product>, categoryIds: Set<String>): List<ProductWithCategoryDto> {
        val categories = categoryService.getAllCategoriesByIdIn(categoryIds)
            .associateBy { it.id!! }
        val productsWithCategory =  products.map { product ->
            ProductWithCategoryDto(
                id = product.id!!,
                name = product.name,
                description = product.description,
                sku = product.sku,
                category = categories[product.categoryId]!!,
                supplierId = product.supplierId,
                sellingPrice = product.sellingPrice,
                purchasePrice = product.purchasePrice,
                minStock = product.minStock,
                stockQuantity = product.stockQuantity,
                isOnPromotion = product.isOnPromotion,
                isActive = product.isActive,
                promotionPrice = product.promotionPrice,
                taxRate = product.taxRate,
                promotionEndDate = product.promotionEndDate?.let { ofInstant(product.promotionEndDate, systemDefault()) },
                unit = product.unit,
                expirationDate = product.expirationDate,
                imageUrls = product.images,
            )
        }
        return productsWithCategory
    }

    private fun mapToProductWithCategoryDto(product: Product, categoryId: String): ProductWithCategoryDto {
        val categories = categoryService.getCategoryById(categoryId)
        val productsWithCategory =
            ProductWithCategoryDto(
                id = product.id!!,
                name = product.name,
                description = product.description,
                sku = product.sku,
                category = categories,
                supplierId = product.supplierId,
                sellingPrice = product.sellingPrice,
                purchasePrice = product.purchasePrice,
                minStock = product.minStock,
                stockQuantity = product.stockQuantity,
                isOnPromotion = product.isOnPromotion,
                isActive = product.isActive,
                promotionPrice = product.promotionPrice,
                taxRate = product.taxRate,
                promotionEndDate = product.promotionEndDate?.let { ofInstant(product.promotionEndDate, systemDefault()) },
                unit = product.unit,
                expirationDate = product.expirationDate,
                imageUrls = product.images,
            )
        return productsWithCategory
    }

    private fun createSearchQuery(search: String?, isActive: String?,
                          categoryId: String?, supplierId: String?,
                          isOnPromotion: String?, minPrice:String?,
                          maxPrice: String?, isLowStock: String?,
                          isInStock: String?, isOutOfStock: String?):Query {
        val query = Query()
        val criteriaList = mutableListOf<Criteria>()
        if (!search.isNullOrBlank()) {
            criteriaList.add(
                Criteria().orOperator(
                    Criteria.where("name").regex(search, "i"),
                    Criteria.where("description").regex(search, "i"),
                    Criteria.where("sku").regex(search, "i")
                )
            )
        }

        if (!isActive.isNullOrBlank()) {
            criteriaList.add(Criteria.where("isActive").`is`(isActive.toBoolean()))
        }
        if (!categoryId.isNullOrBlank()) {
            criteriaList.add(Criteria.where("categoryId").`is`(categoryId))
        }
        if (!supplierId.isNullOrBlank()) {
            criteriaList.add(Criteria.where("supplierId").`is`(supplierId))
        }
        if (!isOnPromotion.isNullOrBlank()) {
            criteriaList.add(Criteria.where("isOnPromotion").`is`(isOnPromotion.toBoolean()))
        }
        if (!minPrice.isNullOrBlank() && !maxPrice.isNullOrBlank()) {
            val min = try { BigDecimal(minPrice) } catch (e: NumberFormatException) { BigDecimal.ZERO }
            val max = try { BigDecimal(maxPrice) } catch (e: NumberFormatException) { BigDecimal.ZERO }
            criteriaList.add(Criteria.where("sellingPrice").gte(min).lte(max))
        } else if (!minPrice.isNullOrBlank()) {
            val min = try { BigDecimal(minPrice) } catch (e: NumberFormatException) { BigDecimal.ZERO }
            criteriaList.add(Criteria.where("sellingPrice").gte(min))
        } else if (!maxPrice.isNullOrBlank()) {
            val max = try { BigDecimal(maxPrice) } catch (e: NumberFormatException) { BigDecimal.ZERO }
            criteriaList.add(Criteria.where("sellingPrice").lte(max))
        }

        if (!isLowStock.isNullOrBlank() && isLowStock.toBoolean()) {
            criteriaList.add(Criteria.where("stockQuantity").lte("minStock"))
        }
        if (!isInStock.isNullOrBlank() && isInStock.toBoolean()) {
            criteriaList.add(Criteria.where("stockQuantity").gt(0))
        }
        if (!isOutOfStock.isNullOrBlank() && isOutOfStock.toBoolean()) {
            criteriaList.add(Criteria.where("stockQuantity").`is`(0))
        }

        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }
        return query
    }


    fun getProductWithCategoryById(productId: String): ProductWithCategoryDto {
        val product = getProductById(productId)
        val categoryId = product.categoryId
        return mapToProductWithCategoryDto(product, categoryId)
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

    fun updateProductStatus(id: String, action: ActionDto): Product{
        logger.info("Updating product status for ID: {} to action: {}", id, action.value)
        val existingProduct = getProductById(id)
        val updatedProduct = existingProduct.copy(isActive = action.value == "activate")
        productRepository.save(updatedProduct)
        logger.info("Product status updated successfully: {} (ID: {})", updatedProduct.name, updatedProduct.id)
        return updatedProduct
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

    // ======= MÉTHODES POUR LA GESTION DES IMAGES =======

    /**
     * Ajoute une image à un produit
     */
    fun addImageToProduct(productId: String, imageUrl: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Adding image to product ID: {} by user: {}", productId, currentUser)

        val existingProduct = getProductById(productId)
        val updatedImages = existingProduct.images.toMutableList()
        updatedImages.add(imageUrl)

        val updatedProduct = existingProduct.copy(images = updatedImages)
        val savedProduct = productRepository.save(updatedProduct)

        auditService.logAction(
            userName = currentUser,
            action = "IMAGE_ADD",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Image added to ${savedProduct.name}",
            oldData = mapOf("imageCount" to existingProduct.images.size.toString()),
            newData = mapOf("imageCount" to savedProduct.images.size.toString(), "newImageUrl" to imageUrl)
        )

        logger.info("Image added successfully to product: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }

    /**
     * Ajoute plusieurs images à un produit
     */
    fun addImagesToProduct(productId: String, imageUrls: List<String>): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Adding {} images to product ID: {} by user: {}", imageUrls.size, productId, currentUser)

        val existingProduct = getProductById(productId)
        val updatedImages = existingProduct.images.toMutableList()
        updatedImages.addAll(imageUrls)

        val updatedProduct = existingProduct.copy(images = updatedImages)
        val savedProduct = productRepository.save(updatedProduct)

        auditService.logAction(
            userName = currentUser,
            action = "IMAGES_ADD",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "${imageUrls.size} images added to ${savedProduct.name}",
            oldData = mapOf("imageCount" to existingProduct.images.size.toString()),
            newData = mapOf("imageCount" to savedProduct.images.size.toString(), "addedImages" to imageUrls.size.toString())
        )

        logger.info("{} images added successfully to product: {} (ID: {})", imageUrls.size, savedProduct.name, savedProduct.id)
        return savedProduct
    }

    /**
     * Supprime une image d'un produit
     */
    fun removeImageFromProduct(productId: String, imageUrl: String): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Removing image from product ID: {} by user: {}", productId, currentUser)

        val existingProduct = getProductById(productId)
        val updatedImages = existingProduct.images.filter { it != imageUrl }

        val updatedProduct = existingProduct.copy(images = updatedImages)
        val savedProduct = productRepository.save(updatedProduct)

        auditService.logAction(
            userName = currentUser,
            action = "IMAGE_REMOVE",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Image removed from ${savedProduct.name}",
            oldData = mapOf("imageCount" to existingProduct.images.size.toString()),
            newData = mapOf("imageCount" to savedProduct.images.size.toString(), "removedImageUrl" to imageUrl)
        )

        logger.info("Image removed successfully from product: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
}
