package com.group4.chatapp.repositories;

import com.group4.chatapp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Stream<User> findByUsernameContaining(String keyword);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
