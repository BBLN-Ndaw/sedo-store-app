package com.sedo.jwtauth.repository

import com.sedo.jwtauth.model.entity.UserLoyalty
import org.springframework.data.mongodb.repository.MongoRepository

interface UserLoyaltyRepository : MongoRepository<UserLoyalty, String> {
    fun findByCustomerUserName(customerUserName: String): UserLoyalty?
}