package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        select c
        from ChatMessage c
        where c.room.id = :roomId
        order by c.sentOn desc
    """)
    Stream<ChatMessage> findByRoomId(@Param("roomId") long roomId, Pageable pageable);
}
