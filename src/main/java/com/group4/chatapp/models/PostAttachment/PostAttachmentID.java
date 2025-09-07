package com.group4.chatapp.models.PostAttachment;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class PostAttachmentID implements Serializable {
    private Long postId;
    private Long attachmentId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PostAttachmentID that)) return false;
        return Objects.equals(postId, that.postId) &&
                Objects.equals(attachmentId, that.attachmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, attachmentId);
    }
}
