package com.sedo.jwtauth.config

import com.sedo.jwtauth.constants.Constants.Endpoints.API
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGIN
import com.sedo.jwtauth.constants.Constants.Endpoints.LOGOUT
import com.sedo.jwtauth.constants.Constants.Endpoints.REFRESH_TOKEN
import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.filter.JwtAuthFilter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter
) {
    
    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600)
            }
        }
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .cors {}  // Active la configuration CORS
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints - only authentication related
                    .requestMatchers("$API$LOGIN").permitAll()
                    .requestMatchers("$API$LOGOUT").permitAll()
                    .requestMatchers("$API$REFRESH_TOKEN").permitAll()

                    // Admin only endpoints (contrôle total)
                    .requestMatchers("/api/admin").hasAuthority(ADMIN_ROLE)
//
//                    // Suppliers - Admin et Employee
//                    .requestMatchers("/api/suppliers/**").hasAnyAuthority("ADMIN", "EMPLOYEE")
//
//                    // Products - lecture pour tous, modification pour Admin/Employee
//                    .requestMatchers(GET, "/api/products/**").hasAnyAuthority("ADMIN", "EMPLOYEE", "CLIENT")
//                    .requestMatchers(POST, "/api/products").hasAnyAuthority("ADMIN", "EMPLOYEE")
//                    .requestMatchers(PUT, "/api/products/**").hasAnyAuthority("ADMIN", "EMPLOYEE")
//                    .requestMatchers(DELETE, "/api/products/**").hasAuthority("ADMIN")
//
//                    // Orders - Clients peuvent créer et voir les leurs, Admin/Employee gèrent tout
//                    .requestMatchers(POST, "/api/orders").hasAnyAuthority("ADMIN", "EMPLOYEE", "CLIENT")
//                    .requestMatchers(GET, "/api/orders/{id}").hasAnyAuthority("ADMIN", "EMPLOYEE", "CLIENT")
//                    .requestMatchers("/api/orders/**").hasAnyAuthority("ADMIN", "EMPLOYEE")
//
//                    // Sales - Point de vente (Admin/Employee uniquement)
//                    .requestMatchers("/api/sales/**").hasAnyAuthority("ADMIN", "EMPLOYEE")
//
//                    // Dashboard et rapports - Admin et Employee
//                    .requestMatchers("/api/dashboard/**").hasAnyAuthority("ADMIN", "EMPLOYEE")
//
                    .anyRequest().authenticated()
            }
            .exceptionHandling { exceptions ->
                exceptions
                    .authenticationEntryPoint { _, response, authException ->
                        response.status = HttpServletResponse.SC_UNAUTHORIZED
                        response.contentType = "application/json"
                        response.characterEncoding = "UTF-8"
                        response.writer.write("""
                            {
                                "error": "Authentication Required",
                                "message": "Full authentication is required to access this resource",
                                "status": 401,
                                "timestamp": "${java.time.LocalDateTime.now()}"
                            }
                        """.trimIndent())
                    }
                    .accessDeniedHandler { _, response, accessDeniedException ->
                        response.status = HttpServletResponse.SC_FORBIDDEN
                        response.contentType = "application/json"
                        response.characterEncoding = "UTF-8"
                        response.writer.write("""
                            {
                                "error": "Access Denied",
                                "message": "You don't have permission to access this resource",
                                "status": 403,
                                "timestamp": "${java.time.LocalDateTime.now()}"
                            }
                        """.trimIndent())
                    }
            }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()


}
