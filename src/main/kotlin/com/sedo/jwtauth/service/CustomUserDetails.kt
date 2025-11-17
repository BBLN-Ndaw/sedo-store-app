package com.sedo.jwtauth.service

import com.sedo.jwtauth.model.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * 
 * This class serves as a bridge between the application's User entity and
 * Spring Security's UserDetails interface, providing authentication and authorization
 * information required by the security framework.
 * 
 * Security Integration:
 * - Converts application User entity to Spring Security UserDetails
 * - Maps user roles to Spring Security authorities
 * - Provides account status information for security decisions
 * - Used throughout the authentication and authorization process
 * 
 * Business Logic:
 * - All accounts are considered non-expired, non-locked, and enabled
 * - Credentials are considered non-expired
 * - User roles are mapped to SimpleGrantedAuthority objects
 * - Encapsulates user security information
 * 
 * @param user The User entity containing user information
 *
 */
class CustomUserDetails(private val user: User) : UserDetails {

    /**
     * Returns the authorities granted to the user.
     * Converts user roles to Spring Security authorities.
     * 
     * @return Collection of GrantedAuthority representing user roles
     */
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return user.roles.map { SimpleGrantedAuthority(it) }
    }

    /**
     * Returns the password used to authenticate the user.
     * 
     * @return The user's password
     */
    override fun getPassword(): String = user.password

    /**
     * Returns the username used to authenticate the user.
     * 
     * @return The user's username
     */
    override fun getUsername(): String = user.userName

    /**
     * Indicates whether the user's account has expired.
     * Currently, all accounts are considered non-expired.
     * 
     * @return true if the user's account is valid (non-expired)
     */
    override fun isAccountNonExpired(): Boolean = true

    /**
     * Indicates whether the user is locked or unlocked.
     * Currently, all accounts are considered non-locked.
     * 
     * @return true if the user is not locked
     */
    override fun isAccountNonLocked(): Boolean = true

    /**
     * Indicates whether the user's credentials (password) has expired.
     * Currently, all credentials are considered non-expired.
     * 
     * @return true if the user's credentials are valid (non-expired)
     */
    override fun isCredentialsNonExpired(): Boolean = true

    /**
     * Indicates whether the user is enabled or disabled.
     * Currently, all users are considered enabled.
     * 
     * @return true if the user is enabled
     */
    override fun isEnabled(): Boolean = true
}
