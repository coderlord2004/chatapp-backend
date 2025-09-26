package com.group4.chatapp.dtos.comment;

import com.group4.chatapp.models.Enum.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @NotNull
    private Long targetId;
    @NotNull
    private TargetType targetType;
    @NotBlank
    private String content;
}
