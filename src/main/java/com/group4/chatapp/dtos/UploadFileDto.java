package com.group4.chatapp.dtos;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record UploadFileDto(
        @Nullable
        String description,
        MultipartFile file
) {}
