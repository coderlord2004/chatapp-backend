package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Comment;
import com.group4.chatapp.models.Enum.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("""
            SELECT COUNT(c.id)
            FROM Comment c
            WHERE c.targetId = ?1 AND c.targetType = ?2
            """)
    Long getTotalComments(Long targetId, TargetType targetType);

    @Query("""
            SELECT c
            FROM Comment c
            WHERE c.targetId = ?1 AND c.targetType = ?2
            """)
    List<Comment> getComments(Long targetId, TargetType targetType);

    @Query("""
            SELECT COUNT(c.user)
            FROM Comment c
            WHERE c.user.id = ?1
            """)
    Long countByUserId(Long userId);
}
