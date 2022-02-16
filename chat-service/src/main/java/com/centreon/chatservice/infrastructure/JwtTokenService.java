package com.centreon.chatservice.infrastructure;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.centreon.chatservice.application.adapters.TokenAdapter;
import org.springframework.stereotype.Service;

/**
 * Jwt implementation of TokenAdapter
 **/
@Service
public class JwtTokenService implements TokenAdapter
{
    private static final String USER_ID_KEY = "user-id";

    private final Algorithm algorithm;
    private final Duration validity;

    /**
     * jwt token service constructor.
     *
     * @param properties jwt configuration properties
     */
    public JwtTokenService(JwtTokenConfigurationProperties properties)
    {
        this.algorithm = Algorithm.ECDSA512(properties.getPublicKey(), properties.getPrivateKey());
        this.validity = properties.getValidity();
    }

    @Override
    public String generate(String userId)
    {
        Objects.requireNonNull(userId, "userId must not be null");
        LocalDateTime issuedAtDate = LocalDateTime.now();
        LocalDateTime expirationDate = issuedAtDate
            .plus(validity);
        return JWT.create()
            .withIssuedAt(
                Date.from(issuedAtDate.atZone(
                    ZoneId.systemDefault()).toInstant()))
            .withExpiresAt(
                Date.from(expirationDate.atZone(
                    ZoneId.systemDefault()).toInstant()))
            .withClaim(USER_ID_KEY, userId)
            .sign(this.algorithm);
    }

    @Override
    public String validate(String token)
    {
        Objects.requireNonNull(token, "token must not be null");

        try {
            DecodedJWT decodedJWT =
                JWT.require(this.algorithm).build().verify(token);
            return decodedJWT.getClaim(USER_ID_KEY).asString();
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
