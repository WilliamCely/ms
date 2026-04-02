package com.cely.auth_server.helpers;

import com.cely.auth_server.entities.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Component
@Slf4j
public class JwtHelper {

    private static final long TOKEN_EXPIRATION_MS = 60 * 60 * 1000;
    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${application.jwt.secret}")
    private String jwtSecret;


    public String createToken(String username){
        final var now = new Date();
        final var expirationDate = new Date(now.getTime() + TOKEN_EXPIRATION_MS);
        return Jwts
                .builder()
                    .setSubject(username)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(expirationDate)
                    .signWith(this.getSecretKey())
                .compact();

    }

    public boolean validateToken(String token){
        if(token == null || token.isBlank()){
            return false;
        }
        try {
            final var expirationDate = this.getExpirationDate(this.normalizeToken(token));
            return expirationDate.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid or expired JWT token");
            return false;
        }
    }

    private Date getExpirationDate(String token){
        return this.getClaimsFromToken(token,Claims::getExpiration);
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T>resolver){
        return resolver.apply(this.singToken(token));
    }

    private Claims singToken(String token){
        return Jwts
                .parserBuilder()
                    .setSigningKey(this.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(this.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    private String normalizeToken(String token){
        if(token.startsWith(BEARER_PREFIX)){
            return token.substring(BEARER_PREFIX.length());
        }
        return token;
    }
}
