package com.sedo.jwtauth.mapper

import com.sedo.jwtauth.model.dto.CreateUserDto
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
    address = this.address
)
fun UserDto.toCreateUserDto(passWord: String? = null): CreateUserDto = CreateUserDto(
    username = this.userName,
    password =  passWord ?: "",
    firstName = this.firstName,
    lastName = this.lastName,
    address = this.address,
    numTel = this.numTel,
    email = this.email,
    isActive = this.isActive,
    roles = this.roles,
)

fun CreateUserDto.toEntity(): User = User(
    userName = this.username,
    password = this.password,
    email = this.email,
    firstName = this.firstName,
    lastName = this.lastName,
    address = this.address,
    numTel = this.numTel,
    isActive = this.isActive,
    roles = this.roles,
)


