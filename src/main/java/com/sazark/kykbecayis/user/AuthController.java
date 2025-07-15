package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.core.exceptions.InvalidEmailException;
import com.sazark.kykbecayis.core.filters.JwtAuthFilter;
import com.sazark.kykbecayis.user.dto.UserCreateRequest;
import com.sazark.kykbecayis.user.dto.UserDto;
import com.sazark.kykbecayis.user.dto.UserLoginRequest;
import com.sazark.kykbecayis.user.dto.UserPatchRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Username-/password-based authentication & self-service endpoints.
 * All routes except /login, /register and /logout assume a populated SecurityContext
 * (set by JwtAuthFilter) and therefore perform no manual JWT checks.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /* Login */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid UserLoginRequest userLoginRequest,
                                   HttpServletResponse resp) {
        if (userLoginRequest == null || userLoginRequest.getEmail() == null || userLoginRequest.getPassword() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(userLoginRequest.getEmail(), userLoginRequest.getPassword());
        authenticationManager.authenticate(token); // throws if bad credentials

        String jwt = jwtService.generateToken(userLoginRequest.getEmail()); // token stores the email

        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.TOKEN_NAME, jwt)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(JwtService.JWT_LIFESPAN_SECOND)
                .sameSite("Strict")
                .build();

        resp.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().body("Login successful");
    }

    /* Register */
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreateRequest userCreateRequest) {
        String email = userCreateRequest.getEmail();
        if (email == null || !email.toLowerCase().trim().endsWith(".edu.tr")) {
            throw new InvalidEmailException("Email must end with '.edu.tr'");
        }
        UserDto saved = userService.create(userCreateRequest);
        URI location = URI.create("/api/users/" + saved.getId());
        return ResponseEntity.created(location).body(saved);
    }

    /* Logout */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse resp) {
        ResponseCookie cookie = ResponseCookie.from(JwtAuthFilter.TOKEN_NAME, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        resp.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    /* Self info get/update/delete */
    @GetMapping("/me")
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(userService.getByUserEmail(email));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto> update(@AuthenticationPrincipal String email,
                                          @RequestBody @Valid UserPatchRequest req) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        UserDto updated = userService.updateByEmail(email, req);
        return updated != null
                ? ResponseEntity.ok(updated)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return userService.deleteByEmail(email)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
