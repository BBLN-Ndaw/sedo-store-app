package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.SupplierDto
import com.sedo.jwtauth.model.entity.Supplier

fun Supplier.toDto(): SupplierDto = SupplierDto(
        id = this.id,
        name = this.name,
        contactPersonName = this.contactPersonName,
        category = this.category,
        email = this.email,
        phone = this.phone,
        address = this.address,
        isActive = this.isActive,
)

fun SupplierDto.toEntity() = Supplier(
        name = this.name,
        contactPersonName = this.contactPersonName,
        category = this.category,
        email = this.email,
        phone = this.phone,
        address = this.address,
        isActive = this.isActive
)