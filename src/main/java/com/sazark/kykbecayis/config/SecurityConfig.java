package com.sazark.kykbecayis.config;

import com.sazark.kykbecayis.filters.AllowAllFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@Profile("!test")
public class SecurityConfig {

    private final AllowAllFilter allowAllFilter;

    public SecurityConfig(AllowAllFilter allowAllFilter) {
        this.allowAllFilter = allowAllFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of(
                                    "https://becayisbul.com",
                                    "http://localhost:8080"
                    ));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    return corsConfig;
                }))
                // Disable csrf
                .csrf(AbstractHttpConfigurer::disable)

                // Permit all requests for now
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Add filter that allows all requests
                .addFilterBefore(allowAllFilter, BasicAuthenticationFilter.class)

                // Stateless session, needed for JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
