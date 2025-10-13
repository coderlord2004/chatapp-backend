package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Enum.ReactionType;
import com.group4.chatapp.models.Enum.TargetType;
import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.id = ?1
            """)
    Post findPostAndAttachment(Long postId);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user = ?1
            ORDER BY p.publishedAt DESC
            """)
    List<Post> getPostsByAuthUser(User authUser, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.username = ?1 AND p.visibility = 'PUBLIC' AND p.visibility = 'FRIEND' AND p.status = 'PUBLISHED'
            ORDER BY p.publishedAt DESC
            """)
    List<Post> getPostsIfIsFriend(String username, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.username = ?1 AND p.visibility = 'PUBLIC' AND p.status = 'PUBLISHED'
            ORDER BY p.publishedAt DESC
            """)
    List<Post> getPostsIfIsNotFriend(String username, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.id = ?1 AND p.visibility <> 'PRIVATE' AND p.status = 'PUBLISHED'
            ORDER BY p.publishedAt DESC
            """)
    List<Post> getNewPostByUserId(Long userId, Pageable pageable);

    @Query("""
           SELECT p
           FROM Post p
           LEFT JOIN FETCH p.attachments
           WHERE p.totalViews >= 0 AND p.user.id <> ?1
           ORDER BY p.totalViews DESC
           """)
    List<Post> getPostsByTopViews(Long authUserId, Pageable pageable);

    @Query("""
            SELECT COUNT(p)
            FROM Post p
            WHERE p.user.id = ?1 AND (p.visibility = 'PUBLIC' OR p.visibility = 'FRIEND')
            """)
    Long countPostByUserId(Long userId);

    @Query("""
            SELECT r.reactionType
            FROM Reaction r
            WHERE r.targetId = ?1 AND r.targetType = ?2
            GROUP BY r.reactionType
            ORDER BY COUNT(r.reactionType) DESC
            """)
    List<ReactionType> getTopReactionType(Long targetId, TargetType targetType, Pageable pageable);

    @Query("""
            SELECT r.reactionType
            FROM Reaction r
            WHERE r.targetId = ?1 AND r.targetType = ?2 AND r.user.id = ?3
            """)
    ReactionType getUserReaction(Long postId, TargetType targetType, Long userId);
}
