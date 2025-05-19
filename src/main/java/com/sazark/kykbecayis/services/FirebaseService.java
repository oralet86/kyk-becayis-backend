package com.sazark.kykbecayis.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {
    public void validateUID(String uid) throws FirebaseAuthException {
        FirebaseAuth.getInstance().getUser(uid);
    }
}
