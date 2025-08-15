package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import org.springframework.stereotype.Service

@Service
class Catalogservice(
    private val categoryService: CategoryService,
    private val productService: ProductService) {

    fun getProductsWithCategories(): List<ProductWithCategoryDto> {
        val products = productService.getAllProducts()
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
                unit = product.unit,
                expirationDate = product.expirationDate,
                imageUrls = product.images,
            )
        }
    }
}