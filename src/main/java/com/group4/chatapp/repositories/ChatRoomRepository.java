package com.group4.chatapp.repositories;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.models.ChatRoom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("""
        select new com.group4.chatapp.dtos.ChatRoomDto(r, msg)
        from ChatRoom r
        inner join r.members mem
        inner join ChatMessage msg on msg.room = r
        where mem.id = ?1
          and msg.sentOn = (
              select max(msg2.sentOn)
              from ChatMessage msg2
              where msg2.room = r
          )
    """)
    @EntityGraph(attributePaths = {"messages", "members"})
    List<ChatRoomDto> findWithLatestMessage(long id);

    @Query("""
        select (count(c) > 0)
        from ChatRoom c
        inner join c.members members
        where members.id = ?1 and c.id = ?2
    """)
    boolean userIsMemberInChatRoom(long userId, long roomId);

    @Query("""
        select (count(c) > 0)
        from ChatRoom c
        inner join c.members a
        inner join c.members b
        where a.id = ?1 and b.id = ?2 and c.type = ?3
    """)
    boolean usersShareRoomOfType(long id1, long id2, ChatRoom.Type type);
}
