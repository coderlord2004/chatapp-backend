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
