package com.sazark.kykbecayis;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
@ActiveProfiles("test")
public class FirebaseConnectionTest {
    @Test
    public void testFirebaseIsInitialized() {
        // FirebaseApp should be initialized
        assertFalse(FirebaseApp.getApps().isEmpty(), "FirebaseApp is not initialized");

        // FirebaseAuth should be accessible
        FirebaseAuth auth = FirebaseAuth.getInstance();
        assertNotNull(auth, "FirebaseAuth instance is null");
    }
}
