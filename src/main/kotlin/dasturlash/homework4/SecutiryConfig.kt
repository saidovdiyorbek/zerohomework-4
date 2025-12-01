package dasturlash.homework4

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain



@Configuration
@EnableWebSecurity
class SecurityConfig(
    //private val customUserDetailsService: CustomUserDetailsService
) {

    companion object{
        @JvmStatic
        val swaggerPaths = arrayOf( "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-resources",
            "/swagger-resources/",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/",
            "/webjars/",
            "/swagger-ui.html")
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity,
                            customUserDetailsService: CustomUserDetailsService): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers("/api/auth/**").permitAll()
                    .requestMatchers(*SecurityConstants.AUTH_WHITELIST).permitAll()

                    .requestMatchers("/api/orders/**").authenticated()


                    .requestMatchers("/api/orders/update-status-admin/").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST,"/api/category/", "/api/products").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT,"/api/category/", "/api/products").hasRole("ADMIN")
                    .requestMatchers("/api/users/*").hasRole("ADMIN")

                    .anyRequest().authenticated()
            }
            .httpBasic {  }
            .userDetailsService(customUserDetailsService)
        return http.build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }
}

// SecurityConstants.kt fayli
class SecurityConstants {
    companion object {
        // JvmStatic massivga Java usulida kirishni ta'minlaydi
        @JvmStatic
        val AUTH_WHITELIST = arrayOf(
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/",
            "/swagger-resources",
            "/swagger-resources/",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/",
            "/webjars/",
            "/swagger-ui.html"
        )
    }
}