package com.sedo.jwtauth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.config.EnableMongoAuditing

@SpringBootApplication
@EnableMongoAuditing
class JwtAuthApplication

fun main(args: Array<String>) {
    runApplication<JwtAuthApplication>(*args)
}
