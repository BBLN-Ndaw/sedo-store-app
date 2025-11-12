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

@RestController
@RequestMapping(CATEGORIES)
class CategoryController(
    private val categoryService: CategoryService
) {
    
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getAllCategories().map { it.toDto() })
    }
    
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.getCategoryById(id).toDto())
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createCategory(@Valid @RequestBody categoryDto: CategoryDto): ResponseEntity<CategoryDto> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.createCategory(categoryDto.toEntity()).run { categoryDto })
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateCategory(
        @PathVariable id: String,
        @Valid @RequestBody categoryDto: CategoryDto
    ): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto.toEntity()).toDto())
    }
    
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateCategoryStatus(@PathVariable id: String, action: ActionDto): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.updateCategoryStatus(id, action).toDto())
    }
}
