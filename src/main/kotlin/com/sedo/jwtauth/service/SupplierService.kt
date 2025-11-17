package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.dto.ActionDto
import com.sedo.jwtauth.model.dto.SupplierDto
import com.sedo.jwtauth.model.entity.Supplier
import com.sedo.jwtauth.repository.SupplierRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant

/**
 * Service class responsible for managing suppliers in the Store Management System.
 * 
 * This service provides comprehensive supplier management functionality including:
 * - Supplier CRUD operations (Create, Read, Update, Delete)
 * - Advanced search and filtering capabilities
 * - Supplier status management (active/inactive)
 * - Category-based supplier organization
 * - Pagination support for large supplier lists
 * - Integration with audit logging system
 * 
 * Business Logic:
 * - Suppliers provide products for the store inventory
 * - Each supplier has contact information and category classification
 * - Soft delete implementation preserves supplier history
 * - All supplier operations are audited for compliance
 * - Search functionality across multiple supplier attributes
 * 
 * Search and Filtering:
 * - Text search across name, contact person, email, and phone
 * - Filter by active/inactive status
 * - Filter by supplier category
 * - Paginated results for performance
 * - MongoDB-based flexible querying
 * 
 * Integration Points:
 * - Product management for supplier associations
 * - Audit system for tracking supplier changes
 * - User management for operation authorization
 * - Inventory management for supplier relationships
 * 
 * Dependencies:
 * - SupplierRepository for data access operations
 * - AuditService for comprehensive audit logging
 * - MongoTemplate for advanced querying capabilities
 * - Spring Security for user context management
 *
 */
@Service
class SupplierService(
    private val supplierRepository: SupplierRepository,
    private val auditService: AuditService,
    private val mongoTemplate: MongoTemplate
) {
    
    private val logger = LoggerFactory.getLogger(SupplierService::class.java)
    
    /**
     * Retrieves suppliers with advanced search and filtering capabilities.
     * 
     * This method provides comprehensive supplier search functionality with:
     * - Text search across multiple fields (name, contact person, email, phone)
     * - Status filtering (active/inactive)
     * - Category-based filtering
     * - Paginated results for performance optimization
     * 
     * Search Capabilities:
     * - Case-insensitive text search using regex patterns
     * - Boolean status filtering
     * - Exact category matching
     * - MongoDB aggregation for efficient querying
     * 
     * @param search Optional text search term for name, contact person, email, or phone
     * @param isActive Optional status filter ("true"/"false" as string)
     * @param category Optional category filter for supplier classification
     * @param page Page number for pagination (0-based)
     * @param size Number of results per page
     * @return Paginated list of suppliers matching the criteria
     */
    fun getAllSuppliers(search: String?, isActive: String?, category: String?, page: Int, size: Int): Page<Supplier> {
        logger.debug("Retrieving suppliers with search: {}, isActive: {}, category: {}", search, isActive, category)
        val query  = createSearchQuery(search, isActive, category)
        val pageable: Pageable = PageRequest.of(page, size)
        query.with(pageable)

        val suppliers = mongoTemplate.find(query, Supplier::class.java)
        val total = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Supplier::class.java)

        logger.info("Number of Suppliers found: {}", total)
        return PageImpl(suppliers, pageable, total)
    }

    /**
     * Creates MongoDB query for supplier search with multiple criteria.
     * 
     * @param search Text search term
     * @param isActive Status filter
     * @param category Category filter
     * @return Constructed MongoDB Query object
     */
    private fun createSearchQuery(search: String?, isActive: String?, category: String?):Query {
        val query = Query()
        val criteriaList = mutableListOf<Criteria>()
        if (!search.isNullOrBlank()) {
            criteriaList.add(
                    Criteria().orOperator(
                            Criteria.where("name").regex(search, "i"),
                            Criteria.where("contactPersonName").regex(search, "i"),
                            Criteria.where("email").regex(search, "i"),
                            Criteria.where("phone").regex(search, "i")
                    )
            )
        }
        if (!isActive.isNullOrBlank()) {
            criteriaList.add(Criteria.where("isActive").`is`(isActive.toBoolean()))
        }
        if (!category.isNullOrBlank()) {
            criteriaList.add(Criteria.where("category").`is`(category))
        }
        if (criteriaList.isNotEmpty()) {
            query.addCriteria(Criteria().andOperator(*criteriaList.toTypedArray()))
        }
        return query
    }
    
    /**
     * Retrieves a single supplier by its unique identifier.
     * 
     * @param id The unique identifier of the supplier
     * @return Supplier entity
     * @throws ResourceNotFoundException if supplier with given ID is not found
     */
    fun getSupplierById(id: String): Supplier {
        logger.debug("Retrieving supplier with ID: {}", id)
        return supplierRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Supplier not found with ID: $id")
    }
    
    /**
     * Creates a new supplier in the system.
     * 
     * Business Rules:
     * - New suppliers are active by default
     * - All operations are audited with user information
     * - Contact information is validated before creation
     * - Creation timestamp is automatically set
     * 
     * @param supplierDto The supplier data to create
     * @return The created Supplier entity with generated ID
     */
    fun createSupplier(supplierDto: SupplierDto): Supplier {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Creating supplier: {} by user: {}", supplierDto.name, currentUser)
        
        val supplier = Supplier(
            name = supplierDto.name,
            contactPersonName = supplierDto.contactPersonName,
            email = supplierDto.email,
            phone = supplierDto.phone,
            address = supplierDto.address,
            isActive = true,
            createdAt = Instant.now(),
            createdBy = currentUser,
            category = supplierDto.category
        )
        
        val savedSupplier = supplierRepository.save(supplier)
        
        auditService.logAction(
            userName = currentUser,
            action = "CREATE",
            entityType = "Supplier",
            entityId = savedSupplier.id,
            description = "Created supplier: ${savedSupplier.name}",
            newData = mapOf(
                "name" to savedSupplier.name,
                "contactName" to (savedSupplier.contactPersonName ?: ""),
                "email" to (savedSupplier.email),
                "supplierCategory" to (savedSupplier.category ?: "")
            )
        )
        
        logger.info("Supplier created successfully: {} (ID: {})", savedSupplier.name, savedSupplier.id)
        return savedSupplier
    }
    
    /**
     * Updates an existing supplier with new information.
     * 
     * Business Rules:
     * - Supplier must exist before update
     * - All changes are tracked with old and new values
     * - Update timestamp is automatically set
     * - Audit trail is maintained for compliance
     * 
     * @param id The unique identifier of the supplier to update
     * @param supplierDto The updated supplier data
     * @return The updated Supplier entity
     * @throws ResourceNotFoundException if supplier with given ID is not found
     */
    fun updateSupplier(id: String, supplierDto: SupplierDto): Supplier {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating supplier ID: {} by user: {}", id, currentUser)
        
        val existingSupplier = getSupplierById(id)
        
        val oldData = mapOf(
            "name" to existingSupplier.name,
            "contactName" to (existingSupplier.contactPersonName ?: ""),
            "email" to (existingSupplier.email),
            "supplierCategory" to (existingSupplier.category ?: "")
        )
        
        val updatedSupplier = existingSupplier.copy(
            name = supplierDto.name,
            contactPersonName = supplierDto.contactPersonName,
            email = supplierDto.email,
            phone = supplierDto.phone,
            address = supplierDto.address,
            updatedAt = Instant.now(),
            updatedBy = currentUser,
            category = supplierDto.category
        )
        
        val savedSupplier = supplierRepository.save(updatedSupplier)
        
        auditService.logAction(
            userName = currentUser,
            action = "UPDATE",
            entityType = "Supplier",
            entityId = savedSupplier.id,
            description = "Updated supplier: ${savedSupplier.name}",
            oldData = oldData,
            newData = mapOf(
                "name" to savedSupplier.name,
                "contactName" to (savedSupplier.contactPersonName ?: ""),
                "email" to (savedSupplier.email),
                "supplierCategory" to (savedSupplier.category ?: "")
            )
        )
        
        logger.info("Supplier updated successfully: {} (ID: {})", savedSupplier.name, savedSupplier.id)
        return savedSupplier
    }

    /**
     * Updates the active status of a supplier.
     * 
     * Business Logic:
     * - Inactive suppliers cannot be assigned to new products
     * - Existing product associations remain unchanged
     * - Status changes are audited for tracking
     * 
     * @param id The unique identifier of the supplier
     * @param action ActionDto containing the status action ("activate" or "deactivate")
     * @return The updated Supplier entity with new status
     * @throws ResourceNotFoundException if supplier with given ID is not found
     */
    fun updateSupplierStatus(id: String, action: ActionDto): Supplier {
        val status = action.value == "activate"
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating status of supplier ID: {} to {} by user: {}", id, status, currentUser)

        val existingSupplier = getSupplierById(id)

        val updatedSupplier = existingSupplier.copy(isActive = status)

        val savedSupplier = supplierRepository.save(updatedSupplier)

        auditService.logAction(
            userName = currentUser,
            action = "STATUS_UPDATE",
            entityType = "Supplier",
            entityId = savedSupplier.id,
            description = "Updated status of supplier: ${savedSupplier.name} to isActive=${status}"
        )

        logger.info("Supplier status updated successfully: {} (ID: {})", savedSupplier.name, savedSupplier.id)
        return savedSupplier
    }
    
    /**
     * Performs soft delete of a supplier by deactivating it.
     * 
     * Business Logic:
     * - Implements soft delete to preserve supplier history
     * - Deactivated suppliers are hidden from active operations
     * - Existing product relationships are maintained for historical data
     * - All delete operations are audited for compliance
     * 
     * @param id The unique identifier of the supplier to delete
     * @return The deactivated Supplier entity
     * @throws ResourceNotFoundException if supplier with given ID is not found
     */
    fun deleteSupplier(id: String): Supplier {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Deactivating supplier ID: {} by user: {}", id, currentUser)
        
        val existingSupplier = getSupplierById(id)
        
        // Soft delete - on désactive au lieu de supprimer
        val deletedSupplier = existingSupplier.copy(
            isActive = false,
            updatedAt = Instant.now(),
            updatedBy = currentUser
        )
        
        supplierRepository.save(deletedSupplier)
        
        auditService.logAction(
            userName = currentUser,
            action = "DELETE",
            entityType = "Supplier",
            entityId = deletedSupplier.id,
            description = "Deactivated supplier: ${deletedSupplier.name}"
        )
        
        logger.info("Supplier deactivated successfully: {} (ID: {})", deletedSupplier.name, deletedSupplier.id)
        return deletedSupplier
    }
}
