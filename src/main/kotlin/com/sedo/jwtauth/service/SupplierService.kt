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

@Service
class SupplierService(
    private val supplierRepository: SupplierRepository,
    private val auditService: AuditService,
    private val mongoTemplate: MongoTemplate
) {
    
    private val logger = LoggerFactory.getLogger(SupplierService::class.java)
    
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
    
    fun getSupplierById(id: String): Supplier {
        logger.debug("Retrieving supplier with ID: {}", id)
        return supplierRepository.findById(id).orElse(null)
            ?: throw ResourceNotFoundException("Supplier not found with ID: $id")
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
                "email" to (savedSupplier.email),
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
