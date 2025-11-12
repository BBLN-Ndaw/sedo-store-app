package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.SUPPLIERS
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.SupplierDto
import com.sedo.jwtauth.model.entity.Supplier
import com.sedo.jwtauth.service.SupplierService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(SUPPLIERS)
class SupplierController(
    private val supplierService: SupplierService
) {
    
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllSuppliers(@RequestParam(required = false) search: String?,
                        @RequestParam(required = false) isActive: String?,
                        @RequestParam(required = false) category: String?,
                        @RequestParam(defaultValue = "0") page: Int,
                        @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<SupplierDto>> {
        return ResponseEntity.ok(supplierService.getAllSuppliers(search, isActive, category, page, size).map { it.toDto() })
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getSupplierById(@PathVariable id: String): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.getSupplierById(id).toDto())
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createSupplier(@Valid @RequestBody supplierDto: SupplierDto): ResponseEntity<SupplierDto> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(supplierDto).toDto())
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateSupplier(
        @PathVariable id: String,
        @Valid @RequestBody supplierDto: SupplierDto
    ): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto).toDto())
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateSupplierStatus(
            @PathVariable id: String,
            @Valid @RequestBody action: ActionDto
    ): ResponseEntity<Supplier> {
        return ResponseEntity.ok(supplierService.updateSupplierStatus(id, action))
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.deleteSupplier(id).toDto())
    }
}