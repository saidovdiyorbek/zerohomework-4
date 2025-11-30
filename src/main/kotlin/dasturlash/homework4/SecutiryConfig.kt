package dasturlash.homework4

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
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
    private val customUserDetailsService: CustomUserDetailsService
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/swagger-ui/index.html",
                        "/v3/api-docs/**",
                        "/v3/api-docs.yaml",
                        "/webjars/**"
                    ).permitAll()




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
}