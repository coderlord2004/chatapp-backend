package com.group4.chatapp.services;

import com.group4.chatapp.exceptions.ApiException;
import io.agora.media.RtcTokenBuilder2;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AgoraTokenService {
    @Value("${AGORA_APP_ID}")
    private String AGORA_APP_ID;

    @Value("${AGORA_APP_CERTIFICATE}")
    private String AGORA_APP_CERTIFICATE;

    private static final int AGORA_TOKEN_EXPIRATION_IN_SECONDS = 24 * 60 * 60;

    public String generateAgoraToken (String channelName, String uid) {
        try {
            RtcTokenBuilder2 tokenBuilder = new RtcTokenBuilder2();

            int currentTimestamp = (int) (System.currentTimeMillis() / 1000);
            int privilegeExpiredTs = currentTimestamp + AGORA_TOKEN_EXPIRATION_IN_SECONDS;

            return tokenBuilder.buildTokenWithUid(
                    AGORA_APP_ID,
                    AGORA_APP_CERTIFICATE,
                    channelName,
                    Integer.parseInt(uid),
                    RtcTokenBuilder2.Role.ROLE_PUBLISHER,
                    privilegeExpiredTs,
                    privilegeExpiredTs
            );
        } catch (Exception e) {
            throw new ApiException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage()
            );
        }
    }
}
