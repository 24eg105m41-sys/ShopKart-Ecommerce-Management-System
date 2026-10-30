package com.ecommerce.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ecommerce.security.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String SHOPKART_VERCEL_PREVIEW_ORIGIN =
        "https://shopkart-frontend-qlr8n5of6-ecommerce-f8fb.vercel.app";
    private static final String SHOPKART_VERCEL_LEGACY_ORIGIN =
        "https://shopkart-frontend-6lpfdehl7-ecommerce-f8fb.vercel.app";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Value("${app.cors.allowed-origins:}")
    private String allowedOrigins;

    @Value("${app.cors.allow-localhost:true}")
    private boolean allowLocalhost;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/hr/**").hasAnyAuthority("ADMIN", "HR")
                .requestMatchers("/api/customers/**").hasAnyAuthority("ADMIN", "HR", "CUSTOMER")
                .requestMatchers("/api/products/**").hasAnyAuthority("ADMIN", "HR", "MERCHANT")
                .requestMatchers("/api/categories/**").hasAnyAuthority("ADMIN", "HR", "MERCHANT")
                .requestMatchers("/api/orders/**").hasAnyAuthority("ADMIN", "HR", "MERCHANT")
                .requestMatchers("/api/inventory/**").hasAnyAuthority("ADMIN", "HR", "MERCHANT")
                .requestMatchers("/api/users/**").hasAnyAuthority("ADMIN", "HR")
                .anyRequest().authenticated())
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Unauthorized: " +
                        authException.getMessage() + "\",\"errorCode\":\"UNAUTHORIZED\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Access Denied: " +
                        accessDeniedException.getMessage() + "\",\"errorCode\":\"ACCESS_DENIED\"}");
                })
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        Set<String> patterns = new LinkedHashSet<>();

        // Add confirmed production & preview domains
        patterns.add(SHOPKART_VERCEL_PREVIEW_ORIGIN);
        patterns.add(SHOPKART_VERCEL_LEGACY_ORIGIN);

        // Add pattern rules for Vercel preview and production environments
        patterns.add("https://*.vercel.app");
        patterns.add("https://*-ecommerce-f8fb.vercel.app");
        patterns.add("https://shopkart-*.vercel.app");

        // Add user-configured origins from application.properties or CORS_ALLOWED_ORIGINS environment variable
        if (allowedOrigins != null && !allowedOrigins.isBlank()) {
            Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .forEach(patterns::add);
        }

        // Add localhost origins for development
        if (allowLocalhost) {
            patterns.add("http://localhost:[*]");
            patterns.add("http://127.0.0.1:[*]");
            patterns.add("http://localhost:5173");
            patterns.add("http://localhost:5174");
            patterns.add("http://localhost:3000");
            patterns.add("http://localhost:8080");
        }

        configuration.setAllowedOriginPatterns(new ArrayList<>(patterns));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(List.of(
            "*",
            "Authorization",
            "Content-Type",
            "Accept",
            "Origin",
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
