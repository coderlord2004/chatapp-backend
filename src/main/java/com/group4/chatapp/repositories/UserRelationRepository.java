package com.group4.chatapp.repositories;

import com.group4.chatapp.models.UserRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface UserRelationRepository extends JpaRepository<UserRelation, Long> {

    @Query("""
        select i
        from UserRelation i
        where i.receiver.id = ?1
    """)
    Stream<UserRelation> findByReceiverId(long id);

    @Query("""
        select (count(i) > 0) from UserRelation i
        where i.sender.id = ?1
          and i.receiver.id = ?2
          and i.chatRoom is null
          and i.status = ?3
    """)
    boolean existsFriendRequestWith(
        long senderId,
        long receiverId,
        UserRelation.Status status
    );

    @Query("""
        select (count(i) > 0)
        from UserRelation i
        where i.sender.id = ?1
          and i.receiver.id = ?2
          and i.chatRoom.id = ?3
          and i.status = ?4
    """)
    boolean existGroupUserRelationWith(
        long senderId,
        long receiverId,
        long chatRoomId,
        UserRelation.Status status
    );

    @Query("""
        select (count(i) > 0) from UserRelation i
        where ((i.sender.username = ?1 and i.receiver.username = ?2)
          or (i.sender.username = ?2 and i.receiver.username = ?1))
          and i.chatRoom is null
          and i.status = 'ACCEPTED'
    """)
    boolean isFriend(
        String sender,
        String receiver
    );

    @Query("""
            SELECT ur
            FROM UserRelation ur
            WHERE (ur.sender.id = ?1 AND ur.receiver.id = ?2
                OR ur.sender.id = ?2 AND ur.receiver.id = ?1)
                AND ur.chatRoom IS NULL
            """)
    UserRelation getUserRelation(Long authUserId, Long otherUserId);

    @Query("""
            SELECT COUNT(i)
            FROM UserRelation i
            WHERE i.receiver.id = ?1 AND (i.status = 'PENDING' OR i.status = 'ACCEPTED')
            """)
    Long countFollowersByUserId(Long userId);

    @Query("""
            SELECT COUNT(i)
            FROM UserRelation i
            WHERE i.sender.id = ?1 AND (i.status = 'PENDING' OR i.status = 'ACCEPTED')
            """)
    Long countFollowingByUserId(Long userId);
}
