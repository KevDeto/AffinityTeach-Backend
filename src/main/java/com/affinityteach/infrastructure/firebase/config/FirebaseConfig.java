package com.affinityteach.infrastructure.firebase.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

@Configuration
public class FirebaseConfig {
    @Bean
    public Firestore firestore() throws IOException {

        InputStream serviceAccount = getServiceAccount();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        serviceAccount.close();

        return FirestoreClient.getFirestore();
    }
    
    private InputStream getServiceAccount() throws IOException {

        String filePath = "/etc/secrets/private-key-firestore.json";
        File file = new File(filePath);

        if (file.exists()) {
            return new FileInputStream(file);
        }

        InputStream resourceStream = getClass()
                .getClassLoader()
                .getResourceAsStream("private-key-firestore.json");

        if (resourceStream == null) {
            throw new FileNotFoundException("No se encontró private-key-firestore.json");
        }

        return resourceStream;
    }
}
