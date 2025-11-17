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

/**
 * REST Controller for comprehensive supplier relationship management.
 *
 * This controller manages all aspects of supplier operations in the store
 * management system, including supplier registration, contact management,
 * performance tracking, and supply chain coordination. Suppliers are critical
 * business partners providing products and services to the store.
 *
 * The controller provides full CRUD operations with advanced search and
 * filtering capabilities to support procurement and vendor management workflows.
 *
 * @property supplierService Service layer for supplier business logic and operations
 *
 */
@RestController
@RequestMapping(SUPPLIERS)
class SupplierController(
    private val supplierService: SupplierService
) {
    
    /**
     * Retrieves all suppliers with advanced filtering and pagination.
     *
     * This endpoint provides comprehensive supplier listing with support for
     * text search, status filtering, and category-based filtering to help
     * with supplier discovery and management.
     *
     * @param search Optional text search query (supplier name, contact info)
     * @param isActive Optional status filter ("true"/"false" for active/inactive suppliers)
     * @param category Optional category filter to find suppliers by product category
     * @param page Page number for pagination (zero-based, default: 0)
     * @param size Number of suppliers per page (default: 50)
     * @return ResponseEntity containing paginated SupplierDto objects
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier data access
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getAllSuppliers(@RequestParam(required = false) search: String?,
                        @RequestParam(required = false) isActive: String?,
                        @RequestParam(required = false) category: String?,
                        @RequestParam(defaultValue = "0") page: Int,
                        @RequestParam(defaultValue = "50") size: Int): ResponseEntity<Page<SupplierDto>> {
        return ResponseEntity.ok(supplierService.getAllSuppliers(search, isActive, category, page, size).map { it.toDto() })
    }
    
    /**
     * Retrieves detailed information for a specific supplier.
     *
     * This endpoint provides comprehensive supplier details including
     * contact information, performance metrics, product categories,
     * and relationship history.
     *
     * @param id Unique identifier of the supplier to retrieve
     * @return ResponseEntity containing detailed SupplierDto
     * @throws SupplierNotFoundException if supplier with given ID doesn't exist
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier details access
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun getSupplierById(@PathVariable id: String): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.getSupplierById(id).toDto())
    }

    /**
     * Creates a new supplier relationship in the system.
     *
     * This endpoint establishes new supplier partnerships by registering
     * supplier information, contact details, and business terms. Essential
     * for expanding the supply chain and vendor network.
     *
     * @param supplierDto Valid supplier data containing business information
     * @return ResponseEntity with HTTP 201 status containing created SupplierDto
     * @throws ValidationException if supplier data is invalid
     * @throws DuplicateSupplierException if supplier already exists
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier creation
     */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createSupplier(@Valid @RequestBody supplierDto: SupplierDto): ResponseEntity<SupplierDto> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supplierService.createSupplier(supplierDto).toDto())
    }
    
    /**
     * Updates complete supplier information.
     *
     * This endpoint performs comprehensive updates to supplier data,
     * including contact information, business terms, and performance
     * criteria. Used for maintaining current supplier relationships.
     *
     * @param id Unique identifier of the supplier to update
     * @param supplierDto Valid supplier data with updated information
     * @return ResponseEntity containing updated SupplierDto
     * @throws SupplierNotFoundException if supplier doesn't exist
     * @throws ValidationException if updated data is invalid
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier updates
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateSupplier(
        @PathVariable id: String,
        @Valid @RequestBody supplierDto: SupplierDto
    ): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierDto).toDto())
    }

    /**
     * Updates supplier status (activate/deactivate/suspend).
     *
     * This endpoint manages supplier relationship status, allowing
     * administrators to activate, deactivate, or suspend supplier
     * partnerships based on performance or business requirements.
     *
     * @param id Unique identifier of the supplier to update
     * @param action ActionDto containing the status change operation
     * @return ResponseEntity containing updated Supplier entity
     * @throws SupplierNotFoundException if supplier doesn't exist
     * @throws InvalidStatusTransitionException if status change is not allowed
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier status management
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun updateSupplierStatus(
            @PathVariable id: String,
            @Valid @RequestBody action: ActionDto
    ): ResponseEntity<Supplier> {
        return ResponseEntity.ok(supplierService.updateSupplierStatus(id, action))
    }
    
    /**
     * Permanently removes a supplier from the system.
     *
     * This endpoint handles supplier relationship termination, including
     * proper cleanup of related data and business processes. Use with
     * caution as this operation may affect historical data integrity.
     *
     * @param id Unique identifier of the supplier to delete
     * @return ResponseEntity containing deleted SupplierDto for confirmation
     * @throws SupplierNotFoundException if supplier doesn't exist
     * @throws SupplierDeletionException if supplier has active relationships
     *
     * Security: Requires ADMIN or EMPLOYEE role for supplier deletion
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun deleteSupplier(@PathVariable id: String): ResponseEntity<SupplierDto> {
        return ResponseEntity.ok(supplierService.deleteSupplier(id).toDto())
    }
}