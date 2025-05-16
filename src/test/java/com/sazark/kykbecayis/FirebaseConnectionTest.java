package com.sazark.kykbecayis;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",  // use in-memory DB just for this test
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none"
})
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
