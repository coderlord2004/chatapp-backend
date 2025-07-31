package com.group4.chatapp.services;

import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtsService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final AuthenticationManager authenticationManager;

    @Value("${jwts.access-token-lifetime}")
    private Duration accessTokenLifetime;

    @Value("${jwts.refresh-token-lifetime}")
    private Duration refreshTokenLifetime;

    private Jwt generateToken(Authentication authentication, Duration duration) {

        var issued = Instant.now();
        var expiration = issued.plus(duration);

        var claimsSet = JwtClaimsSet.builder()
            .subject(authentication.getName())
            .id(UUID.randomUUID().toString())
            .expiresAt(expiration)
            .issuedAt(issued)
            .build();

        var parameter = JwtEncoderParameters.from(claimsSet);

        return jwtEncoder.encode(parameter);
    }

    public TokenObtainPairDto tokenObtainPair(UserDto dto) {

        var authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                dto.username(), dto.password()
            )
        );

        if (!authentication.isAuthenticated()) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Username or password isn't correct!"
            );
        }

        return new TokenObtainPairDto(
            generateToken(authentication, accessTokenLifetime),
            generateToken(authentication, refreshTokenLifetime)
        );
    }

    public TokenRefreshDto refreshToken(String refreshToken) {

        var authentication = authenticationManager.authenticate(
            new BearerTokenAuthenticationToken(refreshToken)
        );

        var accessToken = generateToken(authentication, accessTokenLifetime);
        return new TokenRefreshDto(accessToken.getTokenValue());
    }
}
