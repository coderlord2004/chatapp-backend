package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.repositories.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserService userService;
    private final ChatRoomRepository repository;

    @Transactional(readOnly = true)
    public List<ChatRoomDto> listChatRooms() {

        var user = userService.getUserOrThrows();

        return repository.findWithLatestMessage(user.getId())
            .stream()
            .map(ChatRoomDto::new)
            .toList();
    }
}
