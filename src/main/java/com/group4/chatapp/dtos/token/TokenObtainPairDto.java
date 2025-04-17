package com.group4.chatapp.dtos.token;

import org.springframework.security.oauth2.jwt.Jwt;

public record TokenObtainPairDto(

    String access,
    String refresh

) {

    public TokenObtainPairDto(Jwt access, Jwt refresh) {
        this(
            access.getTokenValue(),
            refresh.getTokenValue()
        );
    }
}
