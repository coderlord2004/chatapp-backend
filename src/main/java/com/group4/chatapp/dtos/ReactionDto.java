package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Enum.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionDto {
    @NotNull
    private Long targetId;
    @NotBlank
    private TargetType targetType;
    @NotBlank
    private String reactionType;
}
