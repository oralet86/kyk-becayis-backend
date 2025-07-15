package com.sazark.kykbecayis.core.filters;

import com.sazark.kykbecayis.core.exceptions.JwtAuthException;
import com.sazark.kykbecayis.user.JwtService;
import com.sazark.kykbecayis.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String TOKEN_NAME = "jwt";
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    public static String extractToken(HttpServletRequest request) {
        // 1. Prefer the Authorization header (e.g. mobile apps, CLI clients)
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String bearer = auth.substring(7).trim();
            if (!bearer.isEmpty()) {
                return bearer;
            }
        }

        // 2. Fallback to HttpOnly cookie for browser clients
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (TOKEN_NAME.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                    return c.getValue().trim();
                }
            }
        }

        // 3. Nothing found
        return null;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {   // CORS pre-flight
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = extractToken(request);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                JwtService.JwtValidationResult result = jwtService.validateToken(token);

                if (result != JwtService.JwtValidationResult.VALID) {
                    throw new JwtAuthException("JWT " + result.name().toLowerCase());
                }

                String email = jwtService.extractEmail(token);
                List<SimpleGrantedAuthority> auth = userService.getRolesByEmail(email).stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .toList();

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(email, null, auth)
                );
            }

            filterChain.doFilter(request, response);

        } catch (JwtAuthException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            response.getWriter().flush();
        }
    }
}
