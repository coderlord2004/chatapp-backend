package com.group4.chatapp.dtos.callInvitation;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CallInvitationResponseDto {
    private Long channelId;
    private UserWithAvatarDto caller;
    private List<UserWithAvatarDto> members;
    private Boolean isUseVideo;
}
