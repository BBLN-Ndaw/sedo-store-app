package com.sedo.jwtauth.mapper

import Product
import com.sedo.jwtauth.model.dto.CreateProductDto
import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.service.ImageService
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
    promotionEndDate = this.promotionEndDate?.let { ofInstant(it, systemDefault()) })


fun CreateProductDto.toEntity(): Product = Product(
    id = this.id,
    name = this.name,
    description = this.description,
    sku = this.sku,
    categoryId = this.categoryId,
    supplierId = this.supplierId,
    sellingPrice = this.sellingPrice,
    purchasePrice = this.purchasePrice,
    stockQuantity = this.stockQuantity,
    minStock = this.minStock,
    unit = this.unit,
    expirationDate = this.expirationDate,
    images = this.images,
    isActive = this.isActive,
    isOnPromotion = this.isOnPromotion,
    promotionPrice = this.promotionPrice,
    promotionEndDate = promotionEndDate?.atZone(systemDefault())?.toInstant()
)

fun Product.toDtoWithPresignedUrls(imageService: ImageService) = ProductDto(
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
    imageUrls = imageService.generatePresignedUrls(this.images, 24), // URLs valides 24h
    isOnPromotion = this.isOnPromotion,
    promotionPrice = this.promotionPrice,
    promotionEndDate = this.promotionEndDate?.let { ofInstant(it, systemDefault()) })
