package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.services.ChatRoomService;
import com.group4.chatapp.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/api/v1/chatrooms/")
    public List<ChatRoomDto> listChatRooms() {
        return chatRoomService.listRoomsWithLatestMessage();
    }
}
