package com.sazark.kykbecayis.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {
     //* Verify the Firebase ID token and return the UID.
     //* Throws FirebaseAuthException if token is invalid.
    public String verifyIdTokenAndGetUID(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        return decodedToken.getUid();
    }

    public void deleteUser(String idToken) throws FirebaseAuthException {
        FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
