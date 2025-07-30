package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.USER
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.model.dto.CreateUserDto
import com.sedo.jwtauth.model.dto.UpdatePasswordDto
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
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
    @PreAuthorize("hasAuthority('OWNER')")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return userService.getAllUsers()
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.getUserById(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/me")
    fun getMyProfile(authentication: Authentication): ResponseEntity<UserDto> {
        return userService.getUserByUsername(authentication.name)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    fun createUser(@Valid @RequestBody createUserDto: CreateUserDto): ResponseEntity<UserDto> {
        return userService.createUser(createUserDto)
            .toDto()
            .let { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun updateUser(
        @PathVariable id: String, 
        @Valid @RequestBody updateRequest: UserDto
    ): ResponseEntity<UserDto> {
        return userService.updateUser(
            id,
           updateRequest.userName,
            updateRequest.firstName,
            updateRequest.lastName,
            updateRequest.email,
            updateRequest.isActive,
            updateRequest.roles
        ).toDto().let { ResponseEntity.ok(it) }
    }

    @PutMapping("/{id}/password")
    fun updateUserPassword(
        @PathVariable id: String,
        @Valid @RequestBody passwordUpdate: UpdatePasswordDto
    ): ResponseEntity<Map<String, String>> {
        userService.updatePassword(id, passwordUpdate.currentPassword, passwordUpdate.newPassword)
        return ResponseEntity.ok(mapOf("message" to "Password updated successfully"))
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('OWNER')")
    fun deleteUser(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.deleteUser(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }
}
