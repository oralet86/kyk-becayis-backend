package com.sazark.kykbecayis.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.exception.InvalidEmailException;
import com.sazark.kykbecayis.misc.dto.FirebaseIdTokenDto;
import com.sazark.kykbecayis.misc.dto.JwtDto;
import com.sazark.kykbecayis.misc.dto.impl.UserBaseDto;
import com.sazark.kykbecayis.misc.dto.impl.UserRegisterDto;
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
    public ResponseEntity<?> login(@RequestBody FirebaseIdTokenDto request) {
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

            // 4. Return JWT
            return ResponseEntity.ok(new JwtDto(jwt));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserBaseDto> register(@Valid @RequestBody UserRegisterDto request) {
        if (!request.getEmail().toLowerCase().trim().endsWith(".edu.tr")) {
            throw new InvalidEmailException("Email must end with '.edu.tr' to be eligible.");
        }
        try {
            // Verify Firebase ID token and get UID
            String uid = firebaseService.verifyIdTokenAndGetUID(request.getFirebaseIdToken());

            // Create a UserBaseDto to use in UserService
            UserBaseDto user = new UserBaseDto();
            user.setFirstname(request.getFirstname());
            user.setSurname(request.getSurname());
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());
            user.setCity(request.getCity());
            user.setGender(request.getGender());
            user.setCurrentDormId(request.getCurrentDormId());
            user.setFirebaseUID(uid);

            // Create user in your system
            UserBaseDto savedUser = userService.create(user);

            // Return 201 Created with location header
            URI location = URI.create("/api/users/" + savedUser.getId());
            return ResponseEntity.created(location).body(savedUser);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
