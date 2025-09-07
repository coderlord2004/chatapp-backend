package com.group4.chatapp.dtos;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostAttachmentDto {
    private String description;
    private MultipartFile attachment;
}
