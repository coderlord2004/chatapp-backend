package com.group4.chatapp.repositories;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserWithRelationDto;
import com.group4.chatapp.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("""
      SELECT u, i
      FROM User u
      LEFT JOIN Invitation i
         ON ((i.sender = :authUser AND i.receiver = u)
          OR (i.receiver = :authUser AND i.sender = u))
      WHERE u.id <> :#{#authUser.id}
        AND LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Object[]> searchUsersWithInvitation(
            @Param("authUser") User authUser,
            @Param("keyword") String keyword,
            Pageable pageable);

    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("""
        SELECT sender, receiver
        FROM Invitation i
        JOIN i.sender sender
        JOIN i.receiver receiver
        WHERE i.status = 'ACCEPTED' AND (sender.id = ?1 OR receiver.id = ?1)
    """)
    List<Object[]> getListFriend (Long userId);

    @Query("""
    SELECT u
    FROM User u
    WHERE u.id <> :userId
      AND NOT EXISTS (
          SELECT 1
          FROM Invitation i
          WHERE (i.sender = u AND i.receiver.id = :userId)
              OR (i.receiver = u AND i.sender.id = :userId)
      )
    """)
    List<User> getUserIsNotFriend(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT sender, receiver
            FROM Invitation i
            JOIN i.sender sender
            JOIN i.receiver receiver
            WHERE i.status = 'ACCEPTED' AND (sender.id = ?1 OR receiver.id = ?1) AND sender.isOnline = true AND receiver.isOnline = true
            """)
    List<Object[]> getOnlineFriends(Long userId);
}
