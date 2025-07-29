package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.model.entity.Product
import com.sedo.jwtauth.service.ProductService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE', 'CLIENT')")
    fun getAllProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getAllProducts())
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE', 'CLIENT')")
    fun getProductById(@PathVariable id: String): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.getProductById(id))
    }
    
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE', 'CLIENT')")
    fun getProductsByCategory(@PathVariable categoryId: String): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getProductsByCategory(categoryId))
    }
    
    @GetMapping("/supplier/{supplierId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getProductsBySupplier(@PathVariable supplierId: String): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getProductsBySupplier(supplierId))
    }
    
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getLowStockProducts(@RequestParam(defaultValue = "10") threshold: Int): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold))
    }
    
    @GetMapping("/expired")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getExpiredProducts(): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getExpiredProducts())
    }
    
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun getExpiringProducts(@RequestParam(defaultValue = "7") days: Long): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.getExpiringProducts(days))
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun createProduct(@Valid @RequestBody productDto: ProductDto): ResponseEntity<Product> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(productService.createProduct(productDto))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody productDto: ProductDto
    ): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.updateProduct(id, productDto))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun deleteProduct(@PathVariable id: String): ResponseEntity<Void> {
        productService.deleteProduct(id)
        return ResponseEntity.noContent().build()
    }
    
    @PutMapping("/{id}/stock")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE')")
    fun updateStock(
        @PathVariable id: String,
        @RequestParam quantity: Int,
        @RequestParam(defaultValue = "Manual adjustment") reason: String
    ): ResponseEntity<Product> {
        return ResponseEntity.ok(productService.updateStock(id, quantity, reason))
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('OWNER', 'EMPLOYEE', 'CLIENT')")
    fun searchProducts(@RequestParam query: String): ResponseEntity<List<Product>> {
        return ResponseEntity.ok(productService.searchProducts(query))
    }
}
