package com.taraethreads.tarae.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun userDetailsService(
        @Value("\${admin.username}") username: String,
        @Value("\${admin.password}") password: String,
        passwordEncoder: PasswordEncoder,
    ): UserDetailsService {
        val admin = User.withUsername(username)
            .password(passwordEncoder.encode(password))
            .roles("ADMIN")
            .build()
        return InMemoryUserDetailsManager(admin)
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf ->
                csrf.ignoringRequestMatchers("/api/**")
            }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/admin/login").permitAll()
                auth.requestMatchers("/admin/admin.css", "/admin/admin.js", "/admin/css/**").permitAll()
                auth.requestMatchers("/admin/**").authenticated()
                auth.anyRequest().permitAll()
            }
            .formLogin { form ->
                form.loginPage("/admin/login")
                form.loginProcessingUrl("/admin/login")
                form.defaultSuccessUrl("/admin", true)
                form.failureUrl("/admin/login?error=true")
            }
            .logout { logout ->
                logout.logoutUrl("/admin/logout")
                logout.logoutSuccessUrl("/admin/login")
                logout.invalidateHttpSession(true)
                logout.deleteCookies("JSESSIONID")
            }

        return http.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf(
            "https://taraethreads.com",
            "https://www.taraethreads.com",
            "http://localhost:3000",
            "http://localhost:3001",
        )
        config.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }
}
