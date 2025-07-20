package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.USER
import com.sedo.jwtauth.mapper.toDto
import com.sedo.jwtauth.mapper.toEntity
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.service.UserService
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(USER)
class UserController @Autowired constructor(
    private val userService: UserService
) {

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    fun getAllUsers(): ResponseEntity<List<UserDto>> {
        return userService.getAllUsers()
            .map { it.toDto() }
            .let { ResponseEntity.ok(it) }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    fun getUserById(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.getUserById(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER')")
    fun createUser(@Valid @RequestBody user: UserDto): ResponseEntity<UserDto> {
            return userService.createUser(user.toEntity())
                .let { ResponseEntity.ok(user) }
        }


    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun updateUser(@PathVariable id: String, @Valid @RequestBody updateRequest: UserDto
    ): ResponseEntity<UserDto> {
        return userService.updateUser(
            id = id,
            username = updateRequest.username,
            password = updateRequest.password,
            roles = updateRequest.roles
        ).let { ResponseEntity.ok(updateRequest) }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun deleteUser(@PathVariable id: String): ResponseEntity<UserDto> {
        return userService.deleteUser(id)
            .toDto()
            .let { ResponseEntity.ok(it) }
    }
}
