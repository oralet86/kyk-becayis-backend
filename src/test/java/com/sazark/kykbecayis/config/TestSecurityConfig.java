package com.sazark.kykbecayis.config;

import com.sazark.kykbecayis.core.filters.JwtAuthFilter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@TestConfiguration
@Profile("test")
@EnableMethodSecurity
public class TestSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                /* no server-side session */
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                /* disable CSRF for tests */
                .csrf(AbstractHttpConfigurer::disable)

                /* disable Spring’s default anonymous auth -> principal == null when JWT missing */
                .anonymous(AbstractHttpConfigurer::disable)

                /* every endpoint is allowed; we only test authentication behaviour */
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())

                /* run our JWT filter before the Username/Password filter */
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        // no-op: simply echoes the incoming Authentication
        return authentication -> authentication;
    }
}
