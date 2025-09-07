package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Post;
import com.group4.chatapp.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p
            FROM Post p
            JOIN p.postAttachments pa
            JOIN pa.attachment a
            WHERE p.user = ?1
            ORDER BY p.createdOn DESC
            """)
    List<Post> findPostsByUser(User authUser, Pageable pageable);
}
