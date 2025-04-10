package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        select c
        from ChatMessage c
        where c.room.id = ?1
        order by c.sentOn
    """)
    Collection<ChatMessage> findByRoomId(long id);
}
