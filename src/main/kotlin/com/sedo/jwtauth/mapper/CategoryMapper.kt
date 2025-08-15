package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.CategoryDto
import com.sedo.jwtauth.model.entity.Category

fun Category.toDto() = CategoryDto(
    id = this.id,
    name = this.name,
    description = this.description,
    isActive = this.isActive)

fun CategoryDto.toEntity() = Category(
    id = this.id,
    name = this.name,
    description = this.description,
    isActive = this.isActive
)