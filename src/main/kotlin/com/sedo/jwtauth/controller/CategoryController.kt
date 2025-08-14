package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.dto.CategoryDto
import com.sedo.jwtauth.model.entity.Category
import com.sedo.jwtauth.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/categories")
class CategoryController(
    private val categoryService: CategoryService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun getAllCategories(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryService.getAllCategories())
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<Category> {
        return ResponseEntity.ok(categoryService.getCategoryById(id))
    }
    
    @GetMapping("/main")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun getMainCategories(): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryService.getMainCategories())
    }
    
    @GetMapping("/{parentId}/subcategories")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun getSubCategories(@PathVariable parentId: String): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryService.getSubCategories(parentId))
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun createCategory(@Valid @RequestBody categoryDto: CategoryDto): ResponseEntity<Category> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.createCategory(categoryDto))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateCategory(
        @PathVariable id: String,
        @Valid @RequestBody categoryDto: CategoryDto
    ): ResponseEntity<Category> {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<Void> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun searchCategories(@RequestParam query: String): ResponseEntity<List<Category>> {
        return ResponseEntity.ok(categoryService.searchCategories(query))
    }
}
