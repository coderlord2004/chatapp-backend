package com.group4.chatapp.dtos.post;

import com.group4.chatapp.dtos.UploadFileDto;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import lombok.*;
import org.springframework.lang.Nullable;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String caption;
    private int captionBackground;
    private PostVisibilityType visibility;
    @Nullable
    private List<UploadFileDto> attachments;
}
