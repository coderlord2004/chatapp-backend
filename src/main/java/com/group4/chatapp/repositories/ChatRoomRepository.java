package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        select c
        from ChatRoom c
        inner join c.members m where m.id = ?1
    """)
    List<ChatRoom> findByMembersId(long id);
}
