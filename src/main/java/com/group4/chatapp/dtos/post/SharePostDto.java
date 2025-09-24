package com.group4.chatapp.dtos.post;

import com.group4.chatapp.models.Enum.PostVisibilityType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SharePostDto {
        @Nullable
        private String caption;
        @NotNull
        private PostVisibilityType visibility;
        @NotNull
        private Long sharedPostId;
}
