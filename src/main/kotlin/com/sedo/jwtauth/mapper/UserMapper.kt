package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.constants.Constants.Roles.CUSTOMER
import com.sedo.jwtauth.model.dto.RegisterUserDto
import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.model.entity.User

/**
 * Extension function to convert User entity to UserDto.
 *
 * Maps database entity to data transfer object for API responses,
 * excluding sensitive information like passwords for security.
 *
 * @return UserDto containing safe user information for API responses
 */
fun User.toDto(): UserDto = UserDto(
    id = this.id,
    userName = this.userName,
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    numTel = this.numTel,
    isActive = this.isActive,
    roles = this.roles,
    address = this.address,
    createdAt = this.createdAt,
)

fun RegisterUserDto.toUser(): User = User(
    userName = this.userName,
    password = "",// Password should be set separately after email verification
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    numTel = this.numTel,
    isActive = false,
    roles = listOf(CUSTOMER),
    address = this.address,
)
