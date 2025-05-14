package com.group4.chatapp.repositories;

import com.group4.chatapp.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Stream<User> findByUsernameContaining(String keyword, PageRequest pageable);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
