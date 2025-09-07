package com.group4.chatapp.repositories;

import com.group4.chatapp.models.PostAttachment.PostAttachment;
import com.group4.chatapp.models.PostAttachment.PostAttachmentID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostAttachmentRepository extends JpaRepository<PostAttachment, PostAttachmentID> {
}
