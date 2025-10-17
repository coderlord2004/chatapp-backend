package com.group4.chatapp.listeners;

import com.group4.chatapp.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnectEvent(SessionConnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String username = user.getName();
            System.out.println("User connected: " + username);
            userService.updateOnlineStatusToFriend(username, true);
        }
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            String username = user.getName();
            System.out.println("User disconnected: " + username);
            userService.updateOnlineStatusToFriend(username, false);
        }
    }
}
