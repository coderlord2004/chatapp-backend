package com.group4.chatapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomDto {
    private String chatRoomName;
    private List<String> members;
}
