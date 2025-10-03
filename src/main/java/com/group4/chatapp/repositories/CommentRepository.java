package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Comment;
import com.group4.chatapp.models.Enum.TargetType;
import org.springframework.data.domain.Pageable;
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
            SELECT c, COUNT(child.id) AS totalChildComments
            FROM Comment c
            LEFT JOIN Comment child ON child.parentComment = c
            WHERE c.targetId = ?1 AND c.targetType = ?2 AND c.parentComment IS NULL
            GROUP BY c
            """)
    List<Object[]> findRootCommentsWithChildCount(Long targetId, TargetType targetType, Pageable pageable);

    @Query("""
            SELECT c, COUNT(child.id) AS totalChildComments
            FROM Comment c
            LEFT JOIN Comment child ON c = child.parentComment
            WHERE c.parentComment.id = ?1
            GROUP BY c
            """)
    List<Object[]> findChildCommentsById(Long commentId, Pageable pageable);

    @Query("""
            SELECT COUNT(c.id)
            FROM Comment c
            WHERE c.user.id = ?1 AND c.targetId = ?2 AND c.targetType = ?3 AND c.parentComment IS NULL
            """)
    Long countRootCommentByUserId(Long userId, Long postId, TargetType targetType);
}
