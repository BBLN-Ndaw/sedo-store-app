package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import com.sedo.jwtauth.service.Catalogservice
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/catalog")
class CatalogController (private val catalogservice: Catalogservice) {
    @GetMapping
    fun getProductWithCategories(): ResponseEntity<List<ProductWithCategoryDto>> {
        return ResponseEntity.ok(catalogservice.getProductsWithCategories())
    }
}