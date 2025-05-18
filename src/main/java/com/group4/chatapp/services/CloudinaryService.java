package com.group4.chatapp.services;

import com.cloudinary.Cloudinary;
import com.group4.chatapp.exceptions.ApiException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public String uploadFile(MultipartFile file, String type) {
        try {
            Map<String, Object> options = Map.of(
                    "folder", "Image/",
                    "resource_type", type
            );
            return (String) cloudinary.uploader().upload(file.getBytes(), options).get("secure_url");
        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
    }
}
