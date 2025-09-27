package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("""
        select msg2.id
        from ChatMessage msg2
        where msg2.room.id = ?1
        order by msg2.sentOn desc, msg2.id asc
    """)
    Optional<ChatMessage> findLatestMessage(long roomId);

    @Query("""
        select c
        from ChatMessage c
        left join fetch c.attachments
        where c.room.id = :roomId
    """)
    List<ChatMessage> findByRoomId(@Param("roomId") long roomId, Pageable pageable);
}
