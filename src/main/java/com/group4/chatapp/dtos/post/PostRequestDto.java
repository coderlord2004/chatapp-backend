package com.group4.chatapp.dtos.post;

import com.group4.chatapp.dtos.UploadFileDto;
import com.group4.chatapp.models.Enum.PostVisibilityType;
import lombok.*;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostRequestDto {
    private String caption;
    private int captionBackground;
    private PostVisibilityType visibility;
    @Builder.Default
    private boolean isScheduled = false;
    private LocalDateTime scheduledAt;
    @Nullable
    private List<UploadFileDto> attachments;
}
