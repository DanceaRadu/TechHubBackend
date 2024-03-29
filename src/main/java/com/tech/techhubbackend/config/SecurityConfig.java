package com.tech.techhubbackend.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((auth) -> auth

                .requestMatchers(HttpMethod.OPTIONS).permitAll()

                .requestMatchers(HttpMethod.POST, "/api/v1/product").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/v1/product/paginate/filter/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/product").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/v1/product").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/product/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/v1/product/image").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/v1/product/image").hasAuthority("ADMIN")

                .requestMatchers("/api/v1/image/**").permitAll()

                .requestMatchers(HttpMethod.POST,"/api/v1/category").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/category").hasAuthority("ADMIN")
                .requestMatchers("/api/v1/category/**").permitAll()

                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/auth/validate").authenticated()

                .requestMatchers(HttpMethod.GET, "/api/v1/review/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/review/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/review/**").authenticated()

                .requestMatchers("/api/v1/favorite/**").authenticated()

                .anyRequest()
                .authenticated()
                );
        http
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}