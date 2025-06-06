package com.group4.chatapp.repositories;

import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {

    @Query("""
        select i
        from Invitation i
        where i.receiver.id = ?1
    """)
    Stream<Invitation> findByReceiverId(long id);

    @Query("""
        select (count(i) > 0) from Invitation i
        where i.sender.id = ?1
          and i.receiver.id = ?2
          and i.chatRoom is null
          and i.status = ?3
    """)
    boolean existsFriendRequestWith(
        long senderId,
        long receiverId,
        Invitation.Status status
    );

    @Query("""
        select (count(i) > 0)
        from Invitation i
        where i.sender.id = ?1
          and i.receiver.id = ?2
          and i.chatRoom.id = ?3
          and i.status = ?4
    """)
    boolean existGroupInvitationWith(
        long senderId,
        long receiverId,
        long chatRoomId,
        Invitation.Status status
    );
}
