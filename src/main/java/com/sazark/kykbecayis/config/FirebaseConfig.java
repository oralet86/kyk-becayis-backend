package com.sazark.kykbecayis.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase_key.json");

            if (serviceAccount != null) {
                try (InputStream stream = serviceAccount) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(stream))
                            .build();

                    FirebaseApp.initializeApp(options);
                }
            } else {
                Path path = Paths.get("firebase_key.json");
                if (!Files.exists(path)) {
                    throw new IOException("firebase_key.json not found as resource or in working directory");
                }

                try (InputStream stream = Files.newInputStream(path)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(stream))
                            .build();

                    FirebaseApp.initializeApp(options);
                }
            }
        }
    }
}