package com.sedo.jwtauth.config

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
                    .requestMatchers("/actuator/**").permitAll()
                    
                    // Owner only endpoints (contrôle total)
                    .requestMatchers("/api/admin").hasAuthority("OWNER")
//
//                    // Suppliers - Owner et Employee
//                    .requestMatchers("/api/suppliers/**").hasAnyAuthority("OWNER", "EMPLOYEE")
//
//                    // Products - lecture pour tous, modification pour Owner/Employee
//                    .requestMatchers(GET, "/api/products/**").hasAnyAuthority("OWNER", "EMPLOYEE", "CLIENT")
//                    .requestMatchers(POST, "/api/products").hasAnyAuthority("OWNER", "EMPLOYEE")
//                    .requestMatchers(PUT, "/api/products/**").hasAnyAuthority("OWNER", "EMPLOYEE")
//                    .requestMatchers(DELETE, "/api/products/**").hasAuthority("OWNER")
//
//                    // Orders - Clients peuvent créer et voir les leurs, Owner/Employee gèrent tout
//                    .requestMatchers(POST, "/api/orders").hasAnyAuthority("OWNER", "EMPLOYEE", "CLIENT")
//                    .requestMatchers(GET, "/api/orders/{id}").hasAnyAuthority("OWNER", "EMPLOYEE", "CLIENT")
//                    .requestMatchers("/api/orders/**").hasAnyAuthority("OWNER", "EMPLOYEE")
//
//                    // Sales - Point de vente (Owner/Employee uniquement)
//                    .requestMatchers("/api/sales/**").hasAnyAuthority("OWNER", "EMPLOYEE")
//
//                    // Dashboard et rapports - Owner et Employee
//                    .requestMatchers("/api/dashboard/**").hasAnyAuthority("OWNER", "EMPLOYEE")
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
