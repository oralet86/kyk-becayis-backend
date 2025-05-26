package com.sazark.kykbecayis.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.exception.InvalidEmailException;
import com.sazark.kykbecayis.misc.dto.FirebaseIdTokenDto;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.misc.dto.user.UserRegisterDto;
import com.sazark.kykbecayis.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

import java.net.URI;
import java.util.HashSet;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final FirebaseService firebaseService;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(FirebaseService firebaseService, UserService userService, JwtService jwtService) {
        this.firebaseService = firebaseService;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody FirebaseIdTokenDto request, HttpServletResponse response) {
        String firebaseIdToken = request.getFirebaseIdToken();
        if (firebaseIdToken == null || firebaseIdToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Firebase token is missing");
        }

        try {
            // 1. Verify Firebase token and get UID
            String uid = firebaseService.verifyIdTokenAndGetUID(firebaseIdToken);

            // 2. Look up the user in the DB
            UserBaseDto user = userService.getByFirebaseUID(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 3. Generate JWT
            String jwt = jwtService.generateToken(uid);

            // 4. Set JWT as HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(JwtService.JWT_LIFESPAN_SECOND)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 5. Respond with 200 OK
            return ResponseEntity.ok("Login successful");

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<UserBaseDto> register(@Valid @RequestBody UserRegisterDto request, HttpServletResponse response) {
        if (!request.getEmail().toLowerCase().trim().endsWith(".edu.tr")) {
            throw new InvalidEmailException("Email must end with '.edu.tr' to be eligible.");
        }
        try {
            // 1. Verify Firebase ID token and get UID
            String uid = firebaseService.verifyIdTokenAndGetUID(request.getFirebaseIdToken());

            // 2. Create a UserBaseDto to use in UserService
            UserBaseDto user = new UserBaseDto();
            user.setFirstname(request.getFirstname());
            user.setSurname(request.getSurname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setCity(request.getCity());
            user.setGender(request.getGender());
            user.setCurrentDormId(request.getCurrentDormId());
            user.setFirebaseUID(uid);
            user.setRoles(new HashSet<>());

            // 3. Create user in your system
            UserBaseDto savedUser = userService.create(user);

            // 4. Generate JWT
            String jwt = jwtService.generateToken(uid);

            // 5. Set JWT as HttpOnly cookie
            ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(JwtService.JWT_LIFESPAN_SECOND)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // 6. Return 201 Created with location header
            URI location = URI.create("/api/users/" + savedUser.getId());
            return ResponseEntity.created(location).body(savedUser);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@CookieValue(name = "jwt", required = false) String jwt) {
        if (jwt == null || jwt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        if (!jwtService.isTokenValid(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String firebaseUID = jwtService.extractUID(jwt);
        if (firebaseUID == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unable to extract UID");
        }

        UserBaseDto user = userService.getByFirebaseUID(firebaseUID);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the cookie by setting Max-Age=0
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok("Logged out");
    }
}
