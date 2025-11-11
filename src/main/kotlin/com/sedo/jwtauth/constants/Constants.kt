package com.sedo.jwtauth.constants

import java.math.BigDecimal

/**
 * Application constants
 */
object Constants {
    
    object Endpoints {
        const val API = "/api"
        const val LOGIN = "/auth/login"
        const val LOGOUT = "/auth/logout"
        const val REFRESH_TOKEN = "/auth/refresh_token"
        const val SET_PASSWORD = "/auth/set-password"
        const val VALIDATE_TOKEN = "/auth/validate-token"
        const val LOYALTY = "/loyalty"
        const val USER = "$API/users"
        const val IMAGE = "$API/products/images"
        const val CATEGORIES = "$API/products/categories"
        const val PRODUCTS = "$API/products"
        const val PRODUCT_WITH_CATEGORY = "/product-with-category"
    }
    
    object Roles {
        const val ADMIN_ROLE = "ADMIN"
        const val EMPLOYEE_ROLE = "EMPLOYEE"
    }
    object Cookie {
        const val JWT_REFRESH_TOKEN_NAME = "refresh_token"
        const val JWT_REFRESH_TOKEN_MAX_AGE = 24 * 60 * 60 // 24 hour in second
    }

    object Order {
        val FREE_SHIPPING_AMOUNT = BigDecimal(50) // Free shipping for orders over 50
        val TAX = BigDecimal("0.20") // 20% VAT
    }
}