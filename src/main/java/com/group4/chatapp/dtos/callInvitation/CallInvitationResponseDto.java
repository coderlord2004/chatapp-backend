package com.group4.chatapp.dtos.callInvitation;

import com.group4.chatapp.dtos.ChatRoomDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallInvitationResponseDto {
    private Boolean isUseVideo;
    private String agoraToken;
    private Map<String, Object> chatRoomDto;
}
