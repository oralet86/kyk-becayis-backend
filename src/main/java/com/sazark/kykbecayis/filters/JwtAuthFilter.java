package com.sazark.kykbecayis.filters;

import com.sazark.kykbecayis.auth.JwtService;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.user.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String firebaseUid = null;

        // Check for JWT in cookies
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Validate token
        if (token != null) {
            if (!jwtService.isTokenValid(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return; // stop the filter chain
            }
            firebaseUid = jwtService.extractUID(token);
        }


        // Authenticate if not already done
        if (firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserBaseDto user = userService.getByFirebaseUID(firebaseUid);
            List<GrantedAuthority> authorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                    .collect(Collectors.toList());

            var authToken = new UsernamePasswordAuthenticationToken(firebaseUid, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }


        filterChain.doFilter(request, response);
    }
}
