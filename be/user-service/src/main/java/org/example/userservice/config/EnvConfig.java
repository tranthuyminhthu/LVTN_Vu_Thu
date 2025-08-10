package org.example.userservice.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Configuration
public class EnvConfig {

    @PostConstruct
    public void loadEnvFile() {
        try {
            // Tìm file .env trong thư mục hiện tại
            Path envPath = Paths.get(".env");
            if (Files.exists(envPath)) {
                Properties props = new Properties();
                props.load(Files.newBufferedReader(envPath));
                
                // Set các biến môi trường
                props.forEach((key, value) -> {
                    if (System.getProperty(key.toString()) == null) {
                        System.setProperty(key.toString(), value.toString());
                    }
                });
                
                System.out.println("✅ Loaded .env file successfully");
            } else {
                System.out.println("⚠️  .env file not found, using default values");
            }
        } catch (IOException e) {
            System.err.println("❌ Error loading .env file: " + e.getMessage());
        }
    }
}
