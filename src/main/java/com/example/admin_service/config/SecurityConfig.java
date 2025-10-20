// config/SecurityConfig.java
package com.example.admin_service.config;

import com.example.admin_service.services.ServiceProviderUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtAuthFilter;
    private final ServiceProviderUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS configuration
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/service/auth/signup", "/service/auth/login", "/service/auth/test").permitAll()
                        .requestMatchers("/admin/auth/login", "/admin/auth/test", "/admin/auth/setup").permitAll()

                        // TripFluencer Points - Public endpoints (accessible by all users)
                        .requestMatchers("/admin/auth/points/settings", "/admin/auth/points/settings/**").permitAll()
                        .requestMatchers("/admin/auth/tripfluencer-points/user/**").permitAll()
                        .requestMatchers("/admin/auth/redemptions/user/**").permitAll()
                        .requestMatchers("/admin/auth/redemptions").permitAll()

                        .requestMatchers("/service/subscription-plans/all").permitAll()

                        // Service Provider endpoints
                        .requestMatchers("/service/auth/profile").hasAnyRole("HOTEL", "TOUR_GUIDE", "TRAVEL_AGENT")

                        // Admin endpoints
                        .requestMatchers("/admin/profile", "/admin/**").hasAnyRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow specific origins (replace with your frontend URLs)
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:3000",      // Local React development
                "http://localhost:5173",      // Vite development server
                "https://*.vercel.app",       // Vercel deployments
                "https://*.netlify.app",      // Netlify deployments
                "https://*.github.io",        // GitHub Pages
                "*"                           // Allow all origins (use cautiously in production)
        ));

        // Allow specific HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // Expose headers that the client can access
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Disposition"
        ));

        // How long the browser can cache preflight requests
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
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
}