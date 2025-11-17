package com.sedo.jwtauth.service

import Product
import com.sedo.jwtauth.exception.ProductNotFoundException
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import com.sedo.jwtauth.model.dto.UpdateProductDto
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
import java.net.URLDecoder
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault

/**
 * Service class for managing product-related business logic.
 *
 * This service handles all product operations including CRUD operations,
 * inventory management, product search with advanced filtering, stock level monitoring,
 * and integration with categories and image management.
 *
 * @property productRepository Repository for product data access
 * @property auditService Service for logging product operations
 * @property imageService Service for managing product images
 * @property categoryService Service for category operations
 * @property mongoTemplate MongoDB template for custom queries
 *
 */
@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val auditService: AuditService,
    private val imageService: ImageService,
    private val categoryService: CategoryService,
    private val mongoTemplate: MongoTemplate
    ) {
    
    private val logger = LoggerFactory.getLogger(ProductService::class.java)
    
    /**
     * Retrieves all products from the inventory.
     *
     * @return List of all products in the system
     */
    fun getAllProducts(): List<Product> {
        logger.debug("Retrieving all products")
        return productRepository.findAll()
    }

    /**
     * Retrieves all products with their associated category information.
     *
     * @return List of products with complete category details
     */
    fun getAllProductsWithCategories(): List<ProductWithCategoryDto> {
        val products = getAllProducts()
        val categoryIds = products.map { it.categoryId }.toSet()
        val categories = categoryService.getAllCategoriesByIdIn(categoryIds)
            .associateBy { it.id!! }
        return products.map { product ->
            ProductWithCategoryDto(
                id = product.id!!,
                name = product.name,
                description = product.description,
                sku = product.sku,
                category = categories[product.categoryId]!!,
                supplierId = product.supplierId,
                sellingPrice = product.sellingPrice,
                stockQuantity = product.stockQuantity,
                isOnPromotion = product.isOnPromotion,
                promotionPrice = product.promotionPrice,
                promotionEndDate = product.promotionEndDate?.let { ofInstant(product.promotionEndDate, systemDefault()) },
                unit = product.unit,
                expirationDate = product.expirationDate,
                imageUrls = imageService.generatePresignedUrls( product.images),
            )
        }
    }

    fun getProductWithCategoryByProductId(productId: String): ProductWithCategoryDto {
        val product = getProductById(productId)
        val categoryId = product.categoryId
        return mapToProductWithCategoryDto(product, categoryId)
    }


    fun searchProductsWithCategories(search: String?, isActive: String?,
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
                imageUrls = imageService.generatePresignedUrls(product.images),
            )
        }
        return productsWithCategory
    }

    private fun mapToProductWithCategoryDto(product: Product, categoryId: String): ProductWithCategoryDto {
        val category = categoryService.getCategoryById(categoryId)
        val productsWithCategory =
            ProductWithCategoryDto(
                id = product.id!!,
                name = product.name,
                description = product.description,
                sku = product.sku,
                category = category,
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
                imageUrls = imageService.generatePresignedUrls(product.images),
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
    
    fun getProductById(id: String): Product {
        logger.debug("Retrieving product with ID: {}", id)
        return productRepository.findById(id).orElse(null)
            ?: throw ProductNotFoundException(id)
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

    fun updateProductStockQuantity(id: String, newQuantity: Int): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating stock quantity for product ID: {} to {} by user: {}", id, newQuantity, currentUser)

        val existingProduct = getProductById(id)
        val oldQuantity = existingProduct.stockQuantity

        val updatedProduct = existingProduct.copy(stockQuantity = newQuantity)

        val savedProduct = productRepository.save(updatedProduct)

        auditService.logAction(
            userName = currentUser,
            action = "STOCK_UPDATE",
            entityType = "Product",
            entityId = savedProduct.id,
            description = "Stock updated for ${savedProduct.name}: $oldQuantity -> $newQuantity",
            oldData = mapOf("stockQuantity" to oldQuantity.toString()),
            newData = mapOf("stockQuantity" to newQuantity.toString())
        )

        logger.info("Stock quantity updated successfully for product: {} (ID: {})", savedProduct.name, savedProduct.id)
        return savedProduct
    }
    
    fun updateProduct(product: UpdateProductDto): Product {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating product ID: {} by user: {}", product.id, currentUser)

        val existingProduct = getProductById(product.id)

        val sanitizedImages = product.images?.mapNotNull { extractProductImagePath(it) }
        
        val updatedProduct = existingProduct.copy(
            name = product.name ?: existingProduct.name,
            description = product.description ?: existingProduct.description,
            sku = product.sku ?: existingProduct.sku,
            categoryId = product.categoryId ?: existingProduct.categoryId,
            supplierId = product.supplierId ?: existingProduct.supplierId,
            sellingPrice = product.sellingPrice ?: existingProduct.sellingPrice,
            taxRate = product.taxRate?: existingProduct.taxRate,
            purchasePrice = product.purchasePrice?: existingProduct.purchasePrice,
            stockQuantity = product.stockQuantity ?: existingProduct.stockQuantity,
            minStock = product.minStock ?: existingProduct.minStock,
            expirationDate = product.expirationDate ?: existingProduct.expirationDate,
            unit = product.unit ?: existingProduct.unit,
            images = sanitizedImages ?: existingProduct.images,
            isOnPromotion = product.isOnPromotion ?: existingProduct.isOnPromotion,
            promotionPrice = product.promotionPrice ?: existingProduct.promotionPrice,
            promotionEndDate = product.promotionEndDate ?: existingProduct.promotionEndDate
        )
        
        val savedProduct = productRepository.save(updatedProduct)

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

    fun getLowStockProducts(): List<Product>{
        logger.debug("Retrieving low stock products")
        return productRepository.findLowStockProducts()
    }

    fun getProductsInStock(): Int {
        return productRepository.findProductsInStock().map { it.isActive }.size
    }

    private fun extractProductImagePath(url: String): String? {
        val decoded = URLDecoder.decode(url, "UTF-8")
        val regex = Regex("products/[^?]+\\.png")
        return regex.find(decoded)?.value
    }
}
