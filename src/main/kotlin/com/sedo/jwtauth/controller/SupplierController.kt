package com.sedo.jwtauth.controller

import com.sedo.jwtauth.model.dto.SupplierDto
import com.sedo.jwtauth.model.entity.Supplier
import com.sedo.jwtauth.service.SupplierService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/suppliers")
class SupplierController(
    private val supplierService: SupplierService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getAllSuppliers(): ResponseEntity<List<Supplier>> {
        return ResponseEntity.ok(supplierService.getAllSuppliers())
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getSupplierById(@PathVariable id: String): ResponseEntity<Supplier> {
        return ResponseEntity.ok(supplierService.getSupplierById(id))
    }
    
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun getSuppliersByCategory(@PathVariable category: String): ResponseEntity<List<Supplier>> {
        return ResponseEntity.ok(supplierService.getSuppliersByCategory(category))
    }
    
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    fun createSupplier(@Valid @RequestBody supplierDto: SupplierDto): ResponseEntity<Supplier> {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(supplierService.createSupplier(supplierDto))
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun updateSupplier(
        @PathVariable id: String,
        @Valid @RequestBody supplierDto: SupplierDto
    ): ResponseEntity<Supplier> {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<Void> {
        supplierService.deleteSupplier(id)
        return ResponseEntity.noContent().build()
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'EMPLOYEE')")
    fun searchSuppliers(@RequestParam query: String): ResponseEntity<List<Supplier>> {
        return ResponseEntity.ok(supplierService.searchSuppliers(query))
    }
}
