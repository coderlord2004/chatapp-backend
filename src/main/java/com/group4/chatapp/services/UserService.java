package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
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

    public List<UserWithAvatarDto> getListFriend () {
        User authUser = getUserOrThrows();
        List<Object[]> friends = repository.getListFriend(authUser.getId());

        return friends.stream().map(pair -> {
            User sender = (User) pair[0];
            User receiver = (User) pair[1];
            User friend = Objects.equals(authUser.getId(), sender.getId()) ? receiver : sender;

            return new UserWithAvatarDto(friend);
        }).toList();
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

    @Transactional(readOnly = true)
    public List<UserWithAvatarDto> searchUser(String keyword, int limit) {

        var MAX_LIMIT = 20;

        if (limit <= 0 || limit > MAX_LIMIT) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                String.format("Limit must be between 1 and %d", MAX_LIMIT)
            );
        }

        var pageable = PageRequest.ofSize(limit);

        return repository.findByUsernameContaining(keyword, pageable)
            .map(UserWithAvatarDto::new)
            .toList();
    }
}
