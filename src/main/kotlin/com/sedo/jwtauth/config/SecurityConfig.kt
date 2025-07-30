package com.sedo.jwtauth.config

import com.sedo.jwtauth.constants.Constants.Roles.ADMIN_ROLE
import com.sedo.jwtauth.filter.JwtAuthFilter
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
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Public endpoints
                    .requestMatchers("/api/login").permitAll()

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
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun corsConfigurer(): WebMvcConfigurer {
        return object : WebMvcConfigurer {
            override fun addCorsMappings(registry: CorsRegistry) {
                registry.addMapping("/**")
                    .allowedOrigins("http://localhost:4200")
            }
        }
    }


}
