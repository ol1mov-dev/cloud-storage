package com.example.cloudstorage.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.access-token.secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token.secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    @Value("${jwt.access-token.expiration-time-in-minutes}")
    private int ACCESS_TOKEN_EXPIRATION_TIME_IN_MINUTES;

    @Value("${jwt.refresh-token.expiration-time-in-days}")
    private int REFRESH_TOKEN_EXPIRATION_TIME_IN_DAYS;

    public String generateAccessToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(getAccessTokenExpirationDate(ACCESS_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .signWith(getSignKey(ACCESS_TOKEN_SECRET_KEY))
                .compact();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        log.info("Generating refresh token for user {}", userDetails.getUsername());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .expiration(getRefreshTokenExpirationDate(REFRESH_TOKEN_EXPIRATION_TIME_IN_DAYS))
                .signWith(getSignKey(REFRESH_TOKEN_SECRET_KEY))
                .compact();
    }

    /**
     * Создаем время жизни для access токена
     * @param expirationTimeInMinutes время жизни access токена в минутах
     * @return время жизни для access токена
     */
    public Date getAccessTokenExpirationDate(int expirationTimeInMinutes) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now
                .plusMinutes(expirationTimeInMinutes)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(accessExpirationInstant);
    }

    /**
     * Создаем время жизни для refresh токена
     * @param expirationTimeInDays время жизни refresh токена в днях
     * @return время жизни для refresh токена
     */
    public Date getRefreshTokenExpirationDate(int expirationTimeInDays) {
        LocalDateTime now = LocalDateTime.now();
        Instant accessExpirationInstant = now
                .plusDays(expirationTimeInDays)
                .atZone(ZoneId.systemDefault())
                .toInstant();

        return Date.from(accessExpirationInstant);
    }

    /**
     * Получаем ключ для токена
     * @param secretKey секретный ключ
     * @return возвращает секретный ключ
     */
    private SecretKey getSignKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Извлекаем данные токена.
     * @param token токен
     * @param tokenSecretKey ключ по которому можно получить доступ к данным токена.
     * @return возвращает Claims - данные которые получены с токена.
     */
    public Claims getClaimsFromToken(String token, String tokenSecretKey) {
        return Jwts
                .parser()
                .verifyWith(getSignKey(tokenSecretKey))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Проверяет валидность Access токена.
     * @param token токен
     * @param userDetails данные пользователя
     * @return Возвращает true, если токен валиден, в противном случае - false.
     */
    public boolean isTokenValid(String token, String secretKey , UserDetails userDetails) {
        log.info("Checking token for user {}", userDetails.getUsername());
        String username = getClaimsFromToken(token, secretKey).getSubject();

        log.info("Checking is token not expired for user {}", username);
        boolean isTokenNotExpired = getClaimsFromToken(token, secretKey)
                .getExpiration()
                .after(new Date());

        return isTokenNotExpired &&
               username.equals(userDetails.getUsername());
    }
}