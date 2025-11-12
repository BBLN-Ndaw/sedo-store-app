package com.sedo.jwtauth.model.entity

import com.sedo.jwtauth.model.dto.Address
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "suppliers")
data class Supplier(
    @Id
    val id: String? = null,

    @field:Indexed(unique = true)
    val name: String,

    val contactPersonName: String? = null,

    val category: String? = null,

    val email: String,

    val phone: String,

    val address: Address,

    val isActive: Boolean = true,

    @field:CreatedDate
    var createdAt: Instant? = null,

    @field:LastModifiedDate
    var updatedAt: Instant? = null,
    @field:Version
    var version: Long? = null,

    val createdBy: String? = null,
    val updatedBy: String? = null
)
