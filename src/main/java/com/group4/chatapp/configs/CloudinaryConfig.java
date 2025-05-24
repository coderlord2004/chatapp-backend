package com.group4.chatapp.configs;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Bean
    public Cloudinary cloudinaryClient() {

        var config = Map.of(
            "api_key", apiKey,
            "api_secret", apiSecret,
            "cloud_name", cloudName
        );

        return new Cloudinary(config);
    }
}
