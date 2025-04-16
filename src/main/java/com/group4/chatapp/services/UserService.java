package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserDto dto) {

        if (repository.existsByUsername(dto.username())) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Username already exists. Please try a different one."
            );
        }

        var hashedPassword = passwordEncoder.encode(dto.password());

        var user = User.builder()
            .username(dto.username())
            .password(hashedPassword)
            .build();

        repository.save(user);
    }

    public Optional<User> getUserByAuthentication(@Nullable Authentication authentication) {

        if (authentication == null) {
            return Optional.empty();
        }

        var principal = authentication.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String username = jwt.getSubject();
            return repository.findByUsername(username);
        } else if (principal instanceof User user) {
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> getUserByContext() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        return getUserByAuthentication(authentication);
    }

    public User getUserOrThrows() {
        return getUserByContext()
            .orElseThrow(() -> new ErrorResponseException(HttpStatus.UNAUTHORIZED));
    }
}
