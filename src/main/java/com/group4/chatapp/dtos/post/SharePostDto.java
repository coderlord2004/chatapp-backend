package com.group4.chatapp.dtos.post;

import com.group4.chatapp.models.Enum.PostVisibilityType;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

public record SharePostDto (
        @Nullable
        String caption,
        @NotNull
        PostVisibilityType visibility,
        @NotNull
        Long sharedPostId
) {}
