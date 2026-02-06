package com.affinityteach.firebase;

import java.io.File;
import java.io.FileInputStream;
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
public class FirebaseInitializer {
	
	@Bean
	public Firestore initFirestore() throws IOException {
		String filePath = "/etc/secrets/private-key-firestore.json";
		File file = new File(filePath);
		
		InputStream serviceAccount;
		if (file.exists()) {
			// archivo desde /etc/secrets/
			serviceAccount = new FileInputStream(file);
		} else {
			//resources (desarrollo local que por el momento no esta habilitado)
			serviceAccount = getClass().getClassLoader()
					.getResourceAsStream("private-key-firestore.json");
		}

		FirebaseOptions options = new FirebaseOptions.Builder()
				.setCredentials(GoogleCredentials.fromStream(serviceAccount))
				.setDatabaseUrl("https://affinityteach.firebaseio.com")
				.build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
        
        serviceAccount.close();
        
        return FirestoreClient.getFirestore();
	}
}
