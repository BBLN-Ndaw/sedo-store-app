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
        const val CHECK_LOGIN = "/auth/check_login"
        const val REFRESH_TOKEN = "/auth/refresh_token"
        const val SET_PASSWORD = "/auth/set-password"
        const val VALIDATE_TOKEN = "/auth/validate-token"
        const val LOYALTY = "/loyalty"
        const val ADMIN = "/admin"
        const val EMPLOYEE = "/employee"
        const val USER = "$API/users"
        const val IMAGE = "$API/products/images"
        const val CATEGORIES = "$API/products/categories"
        const val SUPPLIERS = "$API/suppliers"
        const val PRODUCTS = "$API/products"
        const val ORDERS = "$API/orders"
        const val SALES = "$API/sales"
        const val AUDIT = "$API/audit"
        const val DASHBOARD = "$API/dashboard"
    }
    
    object Roles {
        const val ADMIN_ROLE = "ADMIN"
        const val EMPLOYEE_ROLE = "EMPLOYEE"
        const val CUSTOMER = "CUSTOMER"
    }
    object Cookie {
        const val JWT_REFRESH_TOKEN_NAME = "refresh_token"
        const val JWT_REFRESH_TOKEN_MAX_AGE = 24 * 60 * 60 // 24 hour in second
    }
    object Product {
        const val MARGE= 0.3 // 30% default margin on purchase price
    }

    object Order {
        val FREE_SHIPPING_AMOUNT = BigDecimal(50) // Free shipping for orders over 50
        val TAX = BigDecimal(0.20) // 20% VAT
    }
}