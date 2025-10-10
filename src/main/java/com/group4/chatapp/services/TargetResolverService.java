package com.group4.chatapp.services;

import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TargetResolverService {
    private final PostService postService;
    private final AttachmentService attachmentService;

    public User getAuthor(Long targetId, TargetType targetType) {
        User targetAuthor = null;
        if (targetType.equals(TargetType.POST)) {
            Post post = postService.getPost(targetId);
            targetAuthor = post.getUser();
        } else if (targetType.equals(TargetType.ATTACHMENT)) {
            Attachment attachment = attachmentService.getAttachment(targetId);
            targetAuthor = attachment.getPost().getUser();
        }
        return targetAuthor;
    }
}
