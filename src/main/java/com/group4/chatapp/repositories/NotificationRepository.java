package com.group4.chatapp.repositories;

import com.group4.chatapp.models.Notification;
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
            """)
    List<Notification> findByUserId(Long userId);
}
