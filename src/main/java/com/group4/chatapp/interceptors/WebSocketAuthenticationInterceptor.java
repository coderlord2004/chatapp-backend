package com.group4.chatapp.interceptors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthenticationInterceptor implements ChannelInterceptor {

    private final AuthenticationManager authenticationManager;

    private Optional<Authentication> getAuthenticationByHeader(String header) {

        if (!header.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String token = header.substring(7);
        log.debug("Access token: {}", token);

        var authentication = authenticationManager.authenticate(
            new BearerTokenAuthenticationToken(token)
        );

        return Optional.of(authentication);
    }

    private void addUserToAccessor(Message<?> message) {

        var accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return;
        }

        var authenticationHeader = accessor.getFirstNativeHeader("Authorization");
        if (authenticationHeader == null) {
            return;
        }

        var authentication = getAuthenticationByHeader(authenticationHeader);
        if (authentication.isEmpty()) {
            return;
        }
        accessor.setUser(authentication.get());
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        try {
            addUserToAccessor(message);
        } catch (Exception e) {
            log.error("Failed to detect accessor!", e);
            throw e;
        }

        return message;
    }
}
