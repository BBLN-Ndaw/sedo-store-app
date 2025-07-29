package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.UserDto
import com.sedo.jwtauth.model.entity.User

fun User.toDto(): UserDto = UserDto(
    id = this.id,
    username = this.username,
    password = "", // Ne pas exposer le mot de passe dans la réponse
    roles = this.roles
)

fun UserDto.toEntity(): User = User(
    id = this.id,
    username = this.username,
    password = this.password,
    roles = this.roles
)


