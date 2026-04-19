package com.ordering.jan.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) 
            .authorizeHttpRequests(auth -> auth
                // 1. Public Assets
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()

                // 2. Identity Management (STRICTLY ADMIN ONLY)
                // Only the top-level Admin can manage user accounts/credentials
                .requestMatchers("/users/**").hasRole("ADMIN")
                
                // 3. Labor, Items, and Orders - Management Logic (POST/PUT/DELETE)
                // Only ADMIN and CO-ADMIN can perform actions (add, update, delete)
                .requestMatchers(HttpMethod.POST, "/labor/**", "/items/**", "/orders/**").hasAnyRole("ADMIN", "CO-ADMIN")
                .requestMatchers(HttpMethod.PUT, "/labor/**", "/items/**", "/orders/**").hasAnyRole("ADMIN", "CO-ADMIN")
                .requestMatchers("/labor/delete/**", "/items/delete/**", "/orders/delete/**").hasAnyRole("ADMIN", "CO-ADMIN")

                // 4. Labor, Items, and Orders - View Logic (GET)
                // STAFF can view these pages, but cannot trigger the "POST" actions above
                .requestMatchers(HttpMethod.GET, "/labor/**", "/items/**", "/orders/**").hasAnyRole("ADMIN", "CO-ADMIN", "STAFF")
                
                // 5. Dashboard & Menu
                // Admin and Co-Admin manage/view dashboard; Staff is restricted from high-level analytics if needed
                .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "CO-ADMIN", "STAFF")
                .requestMatchers("/menu").authenticated()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()
                
                // 6. Final safety catch
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/menu", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}