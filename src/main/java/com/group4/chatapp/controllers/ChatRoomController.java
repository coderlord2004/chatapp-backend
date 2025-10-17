package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.CreateChatRoomDto;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.services.ChatRoomService;
import com.group4.chatapp.services.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chatrooms/get/")
    public List<ChatRoomDto> listChatRooms() {
        return chatRoomService.listRoomsWithLatestMessage();
    }

    @GetMapping("/chatroom/get/")
    public ChatRoomDto getChatRoomByUsername(@RequestParam("username") String username) {
        return chatRoomService.getChatRoomByUsername(username);
    }

    @GetMapping("/chatrooms/waiting/")
    public List<ChatRoomDto> getWaitingRooms() {
        return chatRoomService.getWaitingRooms();
    }

    @PostMapping("/chatroom/create/")
    public ChatRoomDto createChatRoom (@RequestBody CreateChatRoomDto dto) {
        return chatRoomService.createChatRoom(dto);
    }

    @PostMapping("/chatroom/update-permission/")
    public void updateSendPermission(@RequestParam("roomId") Long roomId) {
        chatRoomService.updatePermission(roomId);
    }
}
