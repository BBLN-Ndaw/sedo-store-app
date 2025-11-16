package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.PRODUCTS
import com.sedo.jwtauth.constants.Constants.Endpoints.PRODUCT_WITH_CATEGORY
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toDtoWithPresignedUrls
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.CreateProductDto
import com.sedo.jwtauth.model.dto.ProductDto
import com.sedo.jwtauth.model.dto.ProductWithCategoryDto
import com.sedo.jwtauth.model.dto.UpdateProductDto
import com.sedo.jwtauth.model.dto.StockQuantityDto
import com.sedo.jwtauth.service.ImageService
import com.sedo.jwtauth.service.ProductService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(PRODUCTS)
class ProductController(
    private val productService: ProductService,
    private val imageService: ImageService
) {

    @GetMapping("/all")
    fun getAllProductsWithPresignedUrls(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllProducts().map { it.toDtoWithPresignedUrls(imageService) })
    }

    @GetMapping("$PRODUCT_WITH_CATEGORY/all")
    fun getProductWithCategories(): ResponseEntity<List<ProductWithCategoryDto>> {
        return ResponseEntity.ok(productService.getAllProductsWithCategories())
    }

    @GetMapping("$PRODUCT_WITH_CATEGORY/{id}")
    fun getProductWithCategoryByProductId(@PathVariable id: String): ResponseEntity<ProductWithCategoryDto> {
        return ResponseEntity.ok(productService.getProductWithCategoryByProductId(id))
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createProduct(@Valid @RequestBody createProductDto: CreateProductDto): ResponseEntity<CreateProductDto> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(productService.createProduct(createProductDto.toEntity()).run { createProductDto })
    }

    @GetMapping
    fun searchProducts(@RequestParam(required = false) search: String?,
                       @RequestParam(required = false) isActive: String?,
                       @RequestParam(required = false) categoryId: String?,
                       @RequestParam(required = false) supplierId: String?,
                       @RequestParam(required = false) isOnPromotion: String?,
                       @RequestParam(required = false) minPrice: String?,
                       @RequestParam(required = false) maxPrice: String?,
                       @RequestParam(required = false) isLowStock: String?,
                       @RequestParam(required = false) isInStock: String?,
                       @RequestParam(required = false) isOutOfStock: String?,
                       @RequestParam(defaultValue = "0") page: Int,
                       @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<ProductWithCategoryDto>> {
        return ResponseEntity.ok(productService.searchProductsWithCategories(search, isActive, categoryId,
            supplierId, isOnPromotion, minPrice, maxPrice,isLowStock, isInStock,isOutOfStock,page, size))
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody updateProductDto: UpdateProductDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProduct(updateProductDto).toDto())
    }

    @PatchMapping("/stock/{productId}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProductStock(
            @PathVariable productId: String,
            @Valid @RequestBody stockQuantityDto: StockQuantityDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProductStockQuantity(productId, stockQuantityDto.quantity).toDto())
    }

    @PatchMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProductStatus(
            @PathVariable id: String,
            @RequestBody action: ActionDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProductStatus(id, action).toDto())
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getLowStockProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getLowStockProducts().map { it.toDto() })
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDeletedProducts( @PathVariable id: String): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.deleteProduct(id).toDto() )
    }
}