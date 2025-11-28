package com.sedo.jwtauth.event

data class CreateUserRequestEvent (val userName: String,val firstName: String, val lastName: String,
                                   val email: String, val token: String)