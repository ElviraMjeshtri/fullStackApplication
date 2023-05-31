package com.elacode.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JWTUtil {

    private static final String SECRET_KEY = "elacode_123456789_elacode_123456789_elacode_123456789_elacode_123456789";

    public String issueToken(
            String subject){
        return issueToken(subject, Map.of());
    }

    public String issueToken(
            String subject,
            String ...scopes){
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(
            String subject,
            Map<String, Object> claims) {
            // return the created token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer("https://elacode.com")
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(
                        Date.from(
                                Instant.now().plus(15, DAYS)
                        )
                )
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
}
