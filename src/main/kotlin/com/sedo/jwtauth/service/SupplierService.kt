package com.sedo.jwtauth.service

import com.sedo.jwtauth.exception.ResourceNotFoundException
import com.sedo.jwtauth.model.dto.SupplierDto
import com.sedo.jwtauth.model.entity.Supplier
import com.sedo.jwtauth.repository.SupplierRepository
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class SupplierService(
    private val supplierRepository: SupplierRepository,
    private val auditService: AuditService
) {
    
    private val logger = LoggerFactory.getLogger(SupplierService::class.java)
    
    fun getAllSuppliers(): List<Supplier> {
        logger.debug("Retrieving all suppliers")
        return supplierRepository.findByIsActiveTrue()
    }
    
    fun getSupplierById(id: String): Supplier {
        logger.debug("Retrieving supplier with ID: {}", id)
        return supplierRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Supplier not found with ID: $id")
    }
    
    fun getSuppliersByCategory(category: String): List<Supplier> {
        logger.debug("Retrieving suppliers for category: {}", category)
        return supplierRepository.findByCategoryContainingIgnoreCase(category)
    }
    
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
                "email" to (savedSupplier.email ?: ""),
                "supplierCategory" to (savedSupplier.category ?: "")
            )
        )
        
        logger.info("Supplier created successfully: {} (ID: {})", savedSupplier.name, savedSupplier.id)
        return savedSupplier
    }
    
    fun updateSupplier(id: String, supplierDto: SupplierDto): Supplier {
        val currentUser = SecurityContextHolder.getContext().authentication.name
        logger.info("Updating supplier ID: {} by user: {}", id, currentUser)
        
        val existingSupplier = getSupplierById(id)
        
        val oldData = mapOf(
            "name" to existingSupplier.name,
            "contactName" to (existingSupplier.contactPersonName ?: ""),
            "email" to (existingSupplier.email ?: ""),
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
                "email" to (savedSupplier.email ?: ""),
                "supplierCategory" to (savedSupplier.category ?: "")
            )
        )
        
        logger.info("Supplier updated successfully: {} (ID: {})", savedSupplier.name, savedSupplier.id)
        return savedSupplier
    }
    
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
    
    fun searchSuppliers(query: String): List<Supplier> {
        logger.debug("Searching suppliers with query: {}", query)
        return supplierRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(query)
    }
}
