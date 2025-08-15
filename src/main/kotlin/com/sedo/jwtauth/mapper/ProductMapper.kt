package com.sedo.jwtauth.mapper

import Product
import com.sedo.jwtauth.model.dto.CreateProductDto
import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import java.math.BigDecimal
import java.time.LocalDateTime.ofInstant
import java.time.ZoneId.systemDefault

fun Product.toDto() = ProductDto(
    id = this.id,
    name = this.name,
    description = this.description,
    sku = this.sku,
    categoryId = this.categoryId,
    supplierId = this.supplierId,
    sellingPrice = this.sellingPrice,
    stockQuantity = this.stockQuantity,
    minStock = this.minStock,
    expirationDate = this.expirationDate,
    unit = this.unit,
    isActive = this.isActive,
    imageUrls = this.images,
    isOnPromotion = this.isOnPromotion,
    promotionPrice = this.promotionPrice,
    promotionEndDate = ofInstant(this.promotionEndDate, systemDefault()))


fun CreateProductDto.toEntity(): Product = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    sku = this.sku,
    categoryId = this.categoryId,
    supplierId = this.supplierId,
    sellingPrice = this.sellingPrice,
    purchasePrice = this.sellingPrice/ (BigDecimal.ONE + this.margin),
    stockQuantity = this.stockQuantity,
    minStock = this.minStock,
    unit = this.unit,
    expirationDate = this.expirationDate,
    images = this.imageUrls,
    isActive = this.isActive,
    isOnPromotion = this.isOnPromotion,
    promotionPrice = this.promotionPrice,
    promotionEndDate = promotionEndDate?.atZone(systemDefault())?.toInstant()
)