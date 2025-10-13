package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.receiver.id = ?1
            ORDER BY n.sentOn DESC
            """)
    List<Notification> findByReceiverId(Long userId);

    @Query("""
            SELECT n
            FROM Notification n
            WHERE n.sender.id = ?1
            ORDER BY n.sentOn ASC
            """)
    List<Notification> findOldestByReceiverId(Long userId, Pageable pageable);

    @Query("""
            SELECT COUNT(n)
            FROM Notification n
            WHERE n.receiver.id = ?1
            """)
    int countByUserId(Long userId);
}
