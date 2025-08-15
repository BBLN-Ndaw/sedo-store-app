package com.sedo.jwtauth.controller

import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.CategoryDto
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
    fun getAllCategories(): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getAllCategories().map { it.toDto() })
    }
    
    @GetMapping("/{id}")
    fun getCategoryById(@PathVariable id: String): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.getCategoryById(id).toDto())
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE', 'CUSTOMER')")
    fun searchCategories(@RequestParam query: String): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getCategoriesByName(query).map { it.toDto() })
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun createCategory(@Valid @RequestBody categoryDto: CategoryDto): ResponseEntity<CategoryDto> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(categoryService.createCategory(categoryDto.toEntity()).run { categoryDto })
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateCategory(
        @PathVariable id: String,
        @Valid @RequestBody categoryDto: CategoryDto
    ): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.updateCategory(id, categoryDto.toEntity()).toDto())
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<CategoryDto> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getDeletedCategories(): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getAllDeletedCategories().map { it.toDto() })
    }
    

}
