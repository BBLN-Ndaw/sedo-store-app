package com.sedo.jwtauth.constants

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
        const val ADMIN = "/admin"
        const val EMPLOYEE = "/employee"
        const val USER = "$API/users"
        const val CATEGORIES = "$API/categories"
        const val SUPPLIERS = "$API/suppliers"
        const val PRODUCTS = "$API/products"
        const val ORDERS = "$API/orders"
        const val SALES = "$API/sales"
        const val AUDIT = "$API/audit"
        const val DASHBOARD = "$API/dashboard"
    }
    
    object Roles {
        const val ADMIN_ROLE = "ADMIN"        // Propriétaire - accès total
        const val EMPLOYEE_ROLE = "EMPLOYEE"  // Employé/Gestionnaire - gestion quotidienne
        const val CLIENT_ROLE = "CLIENT"      // Client - commandes uniquement
    }
    object Cookie {
        const val JWT_REFRESH_TOKEN_NAME = "refresh_token"
        const val JWT_REFRESH_TOKEN_MAX_AGE = 24 * 60 * 60 // 24 hour in second
    }
}