package com.sedo.jwtauth.controller

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.model.dto.LoginResponseDto
import com.sedo.jwtauth.model.dto.LoginUserDto
import com.sedo.jwtauth.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(API)
class AuthController @Autowired constructor(
    private val authService: AuthService
) {

    @PostMapping(LOGIN)
    fun login(@Valid @RequestBody userDto: LoginUserDto, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.authenticate(userDto, response)
            .let {ResponseEntity.ok(it) }
    }

    @PostMapping(REFRESH_TOKEN)
    fun refreshToken(@CookieValue(value = "refresh_token", required = false) refreshToken: String?, response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
        return authService.refreshToken(refreshToken, response).let {
            if( it.success) {
                ResponseEntity.ok(it)
            } else {
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(it)
            }
        }

    }


//    @PostMapping(LOGOUT)
//    fun logout(response: HttpServletResponse): ResponseEntity<LoginResponseDto> {
//        // Clear access
//        val accessCookie = Cookie(JWT_ACCESS_TOKEN_NAME, null).apply {
//            value = ""  // Explicitement vider la valeur
//            maxAge = 0  // Expire immédiatement
//            path = "/"  // Même path que lors de la création
//            isHttpOnly = true
//            secure = false  // Désactiver secure pour le développement local
//            setAttribute("SameSite", "Lax")
//        }
//
//        // Clear refresh token
//        val refreshCookie = Cookie(JWT_REFRESH_TOKEN_NAME, null).apply {
//            value = ""  // Explicitement vider la valeur
//            maxAge = 0  // Expire immédiatement
//            path = "/"  // Même path que lors de la création
//            isHttpOnly = true
//            secure = false  // Désactiver secure pour le développement local
//            setAttribute("SameSite", "Lax")
//        }
//
//        // Ajouter les cookies expirés à la réponse
//        response.addCookie(accessCookie)
//        response.addCookie(refreshCookie)
//
//        // S'assurer que la réponse a les bons headers CORS
//        response.setHeader("Access-Control-Allow-Credentials", "true")
//
//        return ResponseEntity.ok(LoginResponseDto(success = true, message = "LOGGED_OUT"))
//    }
}
