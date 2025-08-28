package com.group4.chatapp.repositories;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Stream<User> findByUsernameContaining(String keyword, PageRequest pageable);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    @Query("""
        SELECT sender, receiver
        FROM Invitation i
        JOIN i.sender sender
        JOIN i.receiver receiver
        WHERE i.status = 1 AND (sender.id = ?1 OR receiver.id = ?1)
    """)
    List<Object[]> getListFriend (Long userId);
}
