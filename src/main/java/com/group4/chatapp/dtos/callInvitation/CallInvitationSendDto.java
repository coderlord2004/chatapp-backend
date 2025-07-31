package com.group4.chatapp.dtos.callInvitation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CallInvitationSendDto {
    private Long channelId;
    private List<String> membersUsername;
    private Boolean isUseVideo;
    private String agoraToken;
}
