package com.sazark.kykbecayis.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.domain.requests.UserCreationRequest;
import com.sazark.kykbecayis.domain.responses.JwtResponse;
import com.sazark.kykbecayis.domain.dto.UserDto;
import com.sazark.kykbecayis.services.FirebaseService;
import com.sazark.kykbecayis.services.JwtService;
import com.sazark.kykbecayis.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shaded_package.javax.validation.Valid;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final FirebaseService firebaseService;
    private final JwtService jwtService;

    public UserController(UserService userService, FirebaseService firebaseService, JwtService jwtService) {
        this.userService = userService;
        this.firebaseService = firebaseService;
        this.jwtService = jwtService;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody String firebaseIdToken) {
        if (firebaseIdToken == null || firebaseIdToken.isEmpty()) {
            return ResponseEntity.badRequest().body("FirebaseIdToken cannot be empty");
        }
        try {
            String uid = firebaseService.verifyIdTokenAndGetUID(firebaseIdToken);

            // 2. Get user from DB
            UserDto user = userService.getByFirebaseUID(uid);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
            }

            // 3. Generate JWT for your app
            String jwt = jwtService.generateToken(user.getFirebaseUID());

            // 4. Return JWT token
            return ResponseEntity.ok(new JwtResponse(jwt));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token");
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<List<UserDto>> filterUsers(
            @RequestParam(required = false) String postingId
    ) {
        return ResponseEntity.ok(userService.filterUsers(postingId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.findById(id);
        return (user != null) ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getUsers() {
        List<UserDto> users = userService.findAll();
        return (users != null) ? ResponseEntity.ok(users) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        UserDto updated = userService.update(id, userDto);
        return (updated != null) ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}