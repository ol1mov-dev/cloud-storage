package com.example.cloudstorage.configs.security.filters;

import com.example.cloudstorage.exceptions.RefreshTokenDoesNotExist;
import com.example.cloudstorage.exceptions.RefreshTokenExpiredException;
import com.example.cloudstorage.exceptions.handler.ExceptionMessages;
import com.example.cloudstorage.models.User;
import com.example.cloudstorage.repos.RefreshTokenRepo;
import com.example.cloudstorage.repos.UserRepo;
import com.example.cloudstorage.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter{

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserRepo userRepo;

    @Value("${jwt.access-token.secret-key}")
    private String ACCESS_TOKEN_SECRET_KEY;

    @Value("${jwt.refresh-token.secret-key}")
    private String REFRESH_TOKEN_SECRET_KEY;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService, RefreshTokenRepo refreshTokenRepo, UserRepo userRepo){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("Getting Authorization Header");
        String authorizationHeader = request.getHeader("Authorization");

        log.info("Checking is Authorization Header and Bearer Token exists");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.info("Authorization Header or Bearer is not present");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("Extract jwt token");
        String accessToken = authorizationHeader.substring(7);
        String userEmail = jwtService
                .getClaimsFromToken(accessToken, ACCESS_TOKEN_SECRET_KEY)
                .getSubject();

        log.info("Checking is user not authenticated");
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Trying to get UserDetails with email {}",  userEmail);
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            log.info("Getting user from DB.");
            User user = userRepo.findByEmail(userEmail).orElseThrow(
                    () -> {
                        log.error("User with email {} not found in database", userEmail);
                        return new UsernameNotFoundException(ExceptionMessages.USER_WITH_CURRENT_EMAIL_NOT_FOUND.getMessage());
                    }
            );

            log.info("Getting refresh token");
            String refreshToken = refreshTokenRepo.findByUser(user)
                    .orElseThrow(() -> new RefreshTokenDoesNotExist(ExceptionMessages.REFRESH_TOKEN_DOES_NOT_EXIST.getMessage()))
                    .getToken();

            if (jwtService.getClaimsFromToken(refreshToken, REFRESH_TOKEN_SECRET_KEY).getExpiration().before(new Date())){
                throw new RefreshTokenExpiredException(ExceptionMessages.REFRESH_TOKEN_EXPIRED.getMessage());
            }

            log.info("Checking is access and refresh token valid.");
            if (jwtService.isTokenValid(accessToken, ACCESS_TOKEN_SECRET_KEY, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                log.info("Set user details");
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                log.info("Set authentication token");
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        log.info("Do filter next");
        filterChain.doFilter(request, response);
    }
}