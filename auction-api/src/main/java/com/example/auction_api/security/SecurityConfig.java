package com.example.auction_api.security;

import com.example.auction_api.dto.enums.UserRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(request ->
                request
                        .requestMatchers(HttpMethod.GET, "/api/auctions/search", "/api/auctions/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/search/my").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auctions").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/auctions/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auctions/search/admin").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/auctions/{id}").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/cancel").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/approval").hasAnyRole(UserRole.MODERATOR.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/rejection").hasAnyRole(UserRole.MODERATOR.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/cancellation-approval").hasAnyRole(UserRole.MODERATOR.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.POST, "/api/auctions/{id}/cancellation-rejection").hasAnyRole(UserRole.MODERATOR.name(), UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET,"/api/categories").permitAll()
                        .requestMatchers("/api/categories/**").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/bids/my").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/bids/{id}").hasRole(UserRole.ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/bids/**").permitAll()
                        .requestMatchers("/api/bids/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "api/users").hasRole(UserRole.ADMIN.name())
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                );
        http.httpBasic(withDefaults());


        return http.build();
    }
}
