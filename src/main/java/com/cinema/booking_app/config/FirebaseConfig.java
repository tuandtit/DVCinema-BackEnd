package com.cinema.booking_app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;

@Configuration
public class FirebaseConfig {
    @SneakyThrows
    @PostConstruct
    public void init() {
        FileInputStream serviceAccount = new FileInputStream(new ClassPathResource("firebase/account-admin-sdk.json").getFile());
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
