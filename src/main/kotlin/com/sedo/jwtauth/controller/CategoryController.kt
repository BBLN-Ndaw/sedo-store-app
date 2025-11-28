package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.CATEGORIES
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.CategoryDto
import com.sedo.jwtauth.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for managing product categories in the store management system.
 *
 * This controller provides comprehensive CRUD operations for product categories,
 * including category retrieval, creation, modification, and status management.
 * Categories are fundamental organizational units for products in the store.
 *
 * All write operations (POST, PUT, PATCH) require ADMIN or EMPLOYEE role authorization.
 * Read operations are publicly accessible for customer browsing.
 *
 * @property categoryService Service layer for category business logic
 *
 */
@RestController
@RequestMapping(CATEGORIES)
class CategoryController(
    private val categoryService: CategoryService
) {
    
    /**
     * Retrieves all product categories available in the system.
     *
     * This endpoint returns a comprehensive list of all categories,
     * both active and inactive, for administrative purposes and
     * customer browsing functionality.
     *
     * @return ResponseEntity containing list of CategoryDto objects
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllCategories(): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getAllCategories().map { it.toDto() })
    }
    
    /**
     * Retrieves a specific category by its unique identifier.
     *
     * @param id Unique identifier of the category to retrieve
     * @return ResponseEntity containing the CategoryDto if found
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.getCategoryById(id).toDto())
    }
    
    /**
     * Creates a new product category in the system.
     *
     * This endpoint allows authorized users (ADMIN/EMPLOYEE) to create new
     * product categories. The category name must be unique and follow
     * business validation rules.
     *
     * @param categoryDto Valid category data transfer object containing category details
     * @return ResponseEntity with HTTP 201 status and created CategoryDto
     *
     * Security: Requires ADMIN or EMPLOYEE role
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createCategory(@Valid @RequestBody categoryDto: CategoryDto): ResponseEntity<CategoryDto> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.createCategory(categoryDto.toEntity()).run { categoryDto })
    }
    
    /**
     * Updates an existing category with new information.
     *
     * This endpoint performs a complete update of category information,
     * replacing all modifiable fields with the provided data.
     *
     * @param id Unique identifier of the category to update
     * @param categoryDto Valid category data with updated information
     * @return ResponseEntity containing the updated CategoryDto
     *
     * Security: Requires ADMIN or EMPLOYEE role
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateCategory(
        @PathVariable id: String,
        @Valid @RequestBody categoryDto: CategoryDto
    ): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto.toEntity()).toDto())
    }
    
    /**
     * Updates the status of an existing category (activate/deactivate).
     *
     * This endpoint allows partial updates to category status, enabling
     * administrators to activate or deactivate categories without affecting
     * other category properties.
     *
     * @param id Unique identifier of the category to update
     * @param action ActionDto containing the status change operation
     * @return ResponseEntity containing the updated CategoryDto
     *
     * Security: Requires ADMIN or EMPLOYEE role
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateCategoryStatus(@PathVariable id: String, action: ActionDto): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.updateCategoryStatus(id, action).toDto())
    }
}
