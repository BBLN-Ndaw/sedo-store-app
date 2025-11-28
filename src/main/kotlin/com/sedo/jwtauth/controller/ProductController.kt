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

/**
 * REST Controller for product management operations.
 *
 * This controller handles all product-related operations including CRUD operations,
 * stock management, product search with filtering, and product image management.
 * It provides endpoints for both public product browsing and administrative product management.
 *
 * @property productService Service for handling product business logic
 * @property imageService Service for handling product image operations
 *
 */
@RestController
@RequestMapping(PRODUCTS)
class ProductController(
    private val productService: ProductService,
    private val imageService: ImageService
) {

    /**
     * Retrieves all products with pre-signed URLs for images.
     * This endpoint is publicly accessible for browsing products.
     *
     * @return ResponseEntity containing list of all products with accessible image URLs
     */
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllProductsWithPresignedUrls(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getAllProducts().map { it.toDtoWithPresignedUrls(imageService) })
    }

    /**
     * Retrieves all products with their category information.
     *
     * @return ResponseEntity containing list of products with category details
     */
    @GetMapping("$PRODUCT_WITH_CATEGORY/all")
    fun getProductWithCategories(): ResponseEntity<List<ProductWithCategoryDto>> {
        return ResponseEntity.ok(productService.getAllProductsWithCategories())
    }

    /**
     * Retrieves a specific product with its category information by product ID.
     *
     * @param id Product ID to retrieve
     * @return ResponseEntity containing product with category details
     */
    @GetMapping("$PRODUCT_WITH_CATEGORY/{id}")
    fun getProductWithCategoryByProductId(@PathVariable id: String): ResponseEntity<ProductWithCategoryDto> {
        return ResponseEntity.ok(productService.getProductWithCategoryByProductId(id))
    }

    /**
     * Creates a new product in the inventory.
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @param createProductDto Product data for creation
     * @return ResponseEntity containing the created product data
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createProduct(@Valid @RequestBody createProductDto: CreateProductDto): ResponseEntity<CreateProductDto> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(productService.createProduct(createProductDto.toEntity()).run { createProductDto })
    }

    /**
     * Searches products with various filtering options.
     * Supports filtering by search term, status, category, supplier, promotion, price range, and stock levels.
     *
     * @param search Optional search term for product fields
     * @param isActive Optional filter by active status
     * @param categoryId Optional filter by category ID
     * @param supplierId Optional filter by supplier ID
     * @param isOnPromotion Optional filter by promotion status
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param isLowStock Optional filter for low stock products
     * @param isInStock Optional filter for in-stock products
     * @param isOutOfStock Optional filter for out-of-stock products
     * @param page Page number for pagination (default: 0)
     * @param size Number of items per page (default: 50)
     * @return ResponseEntity containing paginated list of products with categories
     */
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

    /**
     * Updates an existing product's information.
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @param id Product ID to update
     * @param updateProductDto Updated product data
     * @return ResponseEntity containing the updated product
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProduct(
        @PathVariable id: String,
        @Valid @RequestBody updateProductDto: UpdateProductDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProduct(updateProductDto).toDto())
    }

    /**
     * Updates the stock quantity of a specific product.
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @param productId Product ID whose stock to update
     * @param stockQuantityDto New stock quantity data
     * @return ResponseEntity containing the updated product
     */
    @PatchMapping("/stock/{productId}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProductStock(
            @PathVariable productId: String,
            @Valid @RequestBody stockQuantityDto: StockQuantityDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProductStockQuantity(productId, stockQuantityDto.quantity).toDto())
    }

    /**
     * Updates a product's active status (activate/deactivate).
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @param id Product ID to update
     * @param action Action DTO containing the new status
     * @return ResponseEntity containing the updated product
     */
    @PatchMapping("/status/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateProductStatus(
            @PathVariable id: String,
            @RequestBody action: ActionDto
    ): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.updateProductStatus(id, action).toDto())
    }

    /**
     * Retrieves products with low stock levels.
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @return ResponseEntity containing list of low stock products
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getLowStockProducts(): ResponseEntity<List<ProductDto>> {
        return ResponseEntity.ok(productService.getLowStockProducts().map { it.toDto() })
    }

    /**
     * Deletes a product from the inventory.
     * Requires ADMIN or EMPLOYEE role for access.
     *
     * @param id Product ID to delete
     * @return ResponseEntity containing the deleted product data
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDeletedProducts( @PathVariable id: String): ResponseEntity<ProductDto> {
        return ResponseEntity.ok(productService.deleteProduct(id).toDto() )
    }
}