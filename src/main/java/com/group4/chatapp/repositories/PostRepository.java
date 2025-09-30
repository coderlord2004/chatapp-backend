package com.group4.chatapp.repositories;

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
            WHERE p.user = ?1
            ORDER BY p.createdOn DESC
            """)
    List<Post> getPostsByAuthUser(User authUser, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.username = ?1 AND p.visibility = 'PUBLIC' AND p.visibility = 'FRIEND'
            ORDER BY p.createdOn DESC
            """)
    List<Post> getPostsIfIsFriend(String username, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.username = ?1 AND p.visibility = 'PUBLIC'
            ORDER BY p.createdOn DESC
            """)
    List<Post> getPostsIfIsNotFriend(String username, Pageable pageable);

    @Query("""
            SELECT p
            FROM Post p
            LEFT JOIN FETCH p.attachments
            WHERE p.user.id = ?1
            ORDER BY p.createdOn DESC
            """)
    List<Post> getNewPostByUserId(Long userId, Pageable pageable);

    @Query("""
           SELECT p
           FROM Post p
           LEFT JOIN FETCH p.attachments
           WHERE p.totalReactions >= 10
           ORDER BY p.totalReactions DESC
           """)
    List<Post> getPostsByTopReactions(Pageable pageable);

    @Query("""
            SELECT COUNT(p)
            FROM Post p
            WHERE p.user.id = ?1 AND (p.visibility = 'PUBLIC' OR p.visibility = 'FRIEND')
            """)
    Long countPostByUserId(Long userId);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.scheduledAt <= :now AND p.status = 'SCHEDULED'
            """)
    List<Post> findReadyToPublish(@Param("now") LocalDateTime now);
}
