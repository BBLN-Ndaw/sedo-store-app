package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import org.springframework.stereotype.Service
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault

@Service
class Catalogservice(
    private val categoryService: CategoryService,
    private val productService: ProductService) {

    fun getProductsWithCategories(): List<ProductWithCategoryDto> {
        val products = productService.getAllProducts()
        val categoryIds = products.map { it.categoryId }.toSet()
        val categories = categoryService.getAllCategoriesByIdIn(categoryIds)
            .associateBy { it.id!! }
        println("Categories fetched: "+categories)
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
                imageUrls = product.images,
            )
        }
    }
}