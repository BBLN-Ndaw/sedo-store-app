package com.sedo.jwtauth.controller

import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.CreateProductDto
import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {
    
    @GetMapping
    fun getAllProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllProducts().map { it.toDto() })
    }

    @GetMapping("/deleted")
    fun getDeletedProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllDeletedProducts().map { it.toDto() })
    }
    
    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: String): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.getProductById(id).toDto())
    }
    
    @GetMapping("/category/{categoryId}")
    fun getProductsByCategory(@PathVariable categoryId: String): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.findByCategoryId(categoryId).map { it.toDto() })
    }
    
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getProductsBySupplier(@PathVariable supplierId: String): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.findBySupplierId(supplierId).map { it.toDto() })
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getLowStockProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getLowStockProducts().map { it.toDto() })
    }

    @GetMapping("/out-of-stock")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getOutOfStockProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getOutOfStockProducts().map { it.toDto() })
    }

    @GetMapping("/price-range")
    fun getProductsInPriceRange(@RequestParam minPrice: BigDecimal, @RequestParam maxPrice: BigDecimal): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.findBySellingPriceRange(minPrice, maxPrice).map { it.toDto() })
    }
    
    @GetMapping("/expired")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getExpiredProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getExpiredProducts().map { it.toDto() })
    }
    @GetMapping("/expired/{expiredDay}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getProductsExpiringIn(@PathVariable expiredDay: Long): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getProductsExpiringIn(expiredDay).map { it.toDto() })
    }

    @GetMapping("/search")
    fun searchProducts(@RequestParam query: String): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getProductsByNameOrSku(query).map { it.toDto() })
    }

    @GetMapping("/promotions")
    fun getProductsOnPromotion(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllProductsOnPromotion().map { it.toDto() })
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun createProduct(@Valid @RequestBody createProductDto: CreateProductDto): ResponseEntity<CreateProductDto> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(productService.createProduct(createProductDto.toEntity()).run { createProductDto })
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody createProductDto: CreateProductDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProduct(id, createProductDto.toEntity()).toDto())
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteProduct(@PathVariable id: String): ResponseEntity<ProductDto> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
    
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateStock(
        @PathVariable id: String,
        @RequestParam quantity: Int,
        @RequestParam(defaultValue = "Manual adjustment") reason: String
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateStock(id, quantity, reason).toDto())
    }
}