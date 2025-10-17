package com.group4.chatapp.repositories;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
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
        left join ChatMessage msg on msg.room.id = r.id
        where mem.id = ?1 and (
            msg.id is null or msg.id = (
                select msg2.id
                from ChatMessage msg2
                where msg2.room.id = r.id
                order by msg2.sentOn desc, msg2.id asc
                limit 1
            )
        )
    """)
    List<ChatRoomDto> findWithLatestMessage(long userId);

    @Query("""
        SELECT members.username
        FROM ChatRoom c
        INNER JOIN c.members members
        WHERE c.id = ?1
    """)
    List<String> findChatRoomWithUsername (long roomId);

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

    @Query("""
            SELECT DISTINCT cr
            FROM ChatRoom cr
            JOIN cr.members m1
            JOIN cr.members m2
            WHERE cr.type = com.group4.chatapp.models.ChatRoom.Type.DUO
                  AND m1.id = :authUserId
                  AND m2.id = :otherUserId
            """)
    ChatRoom findDuoChatRoom(Long authUserId, Long otherUserId);

    @Query("""
            select new com.group4.chatapp.dtos.ChatRoomDto(r, msg)
            from ChatRoom r
            inner join r.members mem
            left join ChatMessage msg on msg.room.id = r.id
            where r.isWaitingRoom = true
                    and mem.id = ?1
                    and ( msg.id is null or msg.id = (
                        select msg2.id
                        from ChatMessage msg2
                        where msg2.room.id = r.id
                        order by msg2.sentOn desc, msg2.id asc
                        limit 1
                    )
            )
            """)
    List<ChatRoomDto> findWaitingRoom(Long userId);
}
