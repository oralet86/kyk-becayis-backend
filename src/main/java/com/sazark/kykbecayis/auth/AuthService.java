package com.sazark.kykbecayis.auth;

import com.google.firebase.auth.FirebaseAuthException;
import com.sazark.kykbecayis.exception.InvalidEmailException;
import com.sazark.kykbecayis.misc.dto.user.UserBaseDto;
import com.sazark.kykbecayis.misc.request.UserCreateRequest;
import com.sazark.kykbecayis.user.UserService;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class AuthService {
    private final FirebaseService firebaseService;
    private final UserService userService;

    public AuthService(FirebaseService firebaseService, UserService userService) {
        this.firebaseService = firebaseService;
        this.userService = userService;
    }

    public UserBaseDto registerUser(UserCreateRequest request) throws FirebaseAuthException {
        String email = request.getEmail();
        if (email == null || !email.toLowerCase().trim().endsWith(".edu.tr")) {
            throw new InvalidEmailException("Email must end with '.edu.tr'");
        }

        String firebaseUID = firebaseService.verifyIdTokenAndGetUID(request.getFirebaseIdToken());

        UserBaseDto user = new UserBaseDto();
        user.setFirstname(request.getFirstname());
        user.setSurname(request.getSurname());
        user.setEmail(email);
        user.setPhone(request.getPhone());
        user.setCity(request.getCity());
        user.setGender(request.getGender());
        user.setCurrentDormId(request.getCurrentDormId());
        user.setFirebaseUID(firebaseUID);
        user.setRoles(new HashSet<>());

        return userService.create(user);
    }
}
