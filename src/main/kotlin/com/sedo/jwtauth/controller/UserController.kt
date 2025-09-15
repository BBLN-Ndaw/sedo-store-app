package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.USER
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.constants.Constants.Roles.EMPLOYEE_ROLE
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.CreateUserDto
import com.sedo.jwtauth.model.dto.UpdatePasswordDto
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(USER)
class UserController @Autowired constructor(
    private val userService: UserService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('$ADMIN_ROLE')")
    fun searchUsers(@RequestParam(required = false) search: String?,
                    @RequestParam(required = false) isActive: String?,
                    @RequestParam(required = false) hasOrders: String?,
                    @RequestParam(defaultValue = "0") page: Int,
                    @RequestParam(defaultValue = "20") size: Int): ResponseEntity<Page<UserDto>> {
        return userService.searchUsers(search, isActive, hasOrders, page, size)
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('$ADMIN_ROLE', '$EMPLOYEE_ROLE')")
    fun createUser(@Valid @RequestBody createUserDto: CreateUserDto): ResponseEntity<UserDto> {
        return userService.createUser(createUserDto)
            .toDto()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @GetMapping("/profile")
    fun getUserProfile(authentication: Authentication): ResponseEntity<UserDto> {
        return userService.getUserByUsername(authentication.name)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.getUserById(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }



    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: String, 
        @Valid @RequestBody updateRequest: UserDto
    ): ResponseEntity<UserDto> {
        return userService.updateUser(
            id,
           updateRequest.userName,
            updateRequest.firstName,
            updateRequest.lastName,
            updateRequest.address,
            updateRequest.email,
            updateRequest.isActive,
            updateRequest.roles
        ).toDto().let { ResponseEntity.ok(it) }
    }

    @PutMapping("/{id}/password")
    fun updateUserPassword(
        @PathVariable id: String,
        @Valid @RequestBody passwordUpdate: UpdatePasswordDto
    ): ResponseEntity<UpdatePasswordDto> {
        return userService.updatePassword(id, passwordUpdate.currentPassword, passwordUpdate.newPassword)
            .let { ResponseEntity.ok(it) }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun deleteUser(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.deleteUser(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }
}
