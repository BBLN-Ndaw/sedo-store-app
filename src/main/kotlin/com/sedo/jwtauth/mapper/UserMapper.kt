package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.model.entity.User

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
