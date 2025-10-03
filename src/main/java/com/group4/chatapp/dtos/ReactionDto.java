package com.group4.chatapp.dtos;

import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReactionDto {
    @NotNull
    private Long targetId;
    @NotBlank
    private TargetType targetType;
    @NotBlank
    private ReactionType reactionType;
}
