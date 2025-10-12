package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.CATEGORIES
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
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

    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun searchCategories(@RequestParam query: String): ResponseEntity<CategoryDto> {
        return ResponseEntity.ok(categoryService.getCategoryByName(query)?.toDto() )
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
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteCategory(@PathVariable id: String): ResponseEntity<CategoryDto> {
        categoryService.deleteCategory(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/deleted")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getDeletedCategories(): ResponseEntity<List<CategoryDto>> {
        return ResponseEntity.ok(categoryService.getAllDeletedCategories().map { it.toDto() })
    }
    

}
