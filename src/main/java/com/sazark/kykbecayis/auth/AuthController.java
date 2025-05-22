package com.sazark.kykbecayis.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.misc.dto.UserDto;
import com.sazark.kykbecayis.misc.dto.UserCreationRequest;
import com.sazark.kykbecayis.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shaded_package.javax.validation.Valid;

import java.net.URI;

@RestController
@RequestMapping("/auth")
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
    public ResponseEntity<?> login(@RequestBody FirebaseLoginRequest request) {
        String firebaseIdToken = request.firebaseToken();
        if (firebaseIdToken == null || firebaseIdToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Firebase token is missing");
        }

        try {
            // 1. Verify Firebase token and get UID
            String uid = firebaseService.verifyIdTokenAndGetUID(firebaseIdToken);

            // 2. Look up the user in the DB
            UserDto user = userService.getByFirebaseUID(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 3. Generate JWT
            String jwt = jwtService.generateToken(uid);

            // 4. Return JWT
            return ResponseEntity.ok(new JwtResponse(jwt));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody UserCreationRequest request) {
        try {
            // Verify Firebase ID token and get UID
            String uid = firebaseService.verifyIdTokenAndGetUID(request.getFirebaseIdToken());

            // Optional: check UID matches userDto's FirebaseUID if provided
            if (request.getUserDto().getFirebaseUID() != null
                    && !uid.equals(request.getUserDto().getFirebaseUID())) {
                return ResponseEntity.badRequest().build();
            }

            // Set verified UID to userDto to avoid mismatches
            request.getUserDto().setFirebaseUID(uid);

            // Create user in your system
            UserDto savedUser = userService.create(request.getUserDto());

            // Return 201 Created with location header
            URI location = URI.create("/api/users/" + savedUser.getId());
            return ResponseEntity.created(location).body(savedUser);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record FirebaseLoginRequest(String firebaseToken) {}
    public record JwtResponse(String token) {}
}
