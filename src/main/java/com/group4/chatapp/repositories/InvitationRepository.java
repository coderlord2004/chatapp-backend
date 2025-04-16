package com.group4.chatapp.repositories;

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
}
