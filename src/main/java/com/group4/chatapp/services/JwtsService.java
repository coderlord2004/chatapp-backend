package com.group4.chatapp.services;

import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtsService {

    private final JwtEncoder jwtEncoder;
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
            .issuedAt(issued)
            .expiresAt(expiration)
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

        return new TokenObtainPairDto(
            generateToken(authentication, accessTokenLifetime).getTokenValue(),
            generateToken(authentication, refreshTokenLifetime).getTokenValue()
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
