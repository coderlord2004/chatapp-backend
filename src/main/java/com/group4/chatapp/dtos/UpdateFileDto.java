package com.group4.chatapp.dtos;

import org.springframework.web.multipart.MultipartFile;

public record UpdateFileDto (
        String publicId,
        MultipartFile file
) {}
