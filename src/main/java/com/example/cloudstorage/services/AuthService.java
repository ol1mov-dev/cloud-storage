package com.example.cloudstorage.services;

import com.example.cloudstorage.controllers.requests.AuthenticationRequest;
import com.example.cloudstorage.controllers.requests.RegistrationRequest;
import com.example.cloudstorage.controllers.responses.JwtResponse;
import com.example.cloudstorage.enums.Roles;
import com.example.cloudstorage.exceptions.handler.ExceptionMessages;
import com.example.cloudstorage.models.RefreshToken;
import com.example.cloudstorage.models.Role;
import com.example.cloudstorage.models.User;
import com.example.cloudstorage.repos.RefreshTokenRepo;
import com.example.cloudstorage.repos.RoleRepo;
import com.example.cloudstorage.repos.UserRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final RefreshTokenRepo refreshTokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepo userRepo, RoleRepo roleRepo, RefreshTokenRepo refreshTokenRepo, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.refreshTokenRepo = refreshTokenRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public JwtResponse register(RegistrationRequest request) {
        if (userRepo.existsByEmail(request.email())) {
            throw new EntityExistsException("User with this email already exists");
        }

        Role defaultRole = roleRepo.getRoleByName(Roles.DEFAULT_ROLE.getValue())
                .orElseThrow(() ->
                        new EntityNotFoundException(ExceptionMessages.DEFAULT_USER_ROLE_NOT_FOUND.getMessage())
                );

        User user = User.builder()
                .name(request.name())
                .surname(request.surname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(defaultRole)
                .build();

        userRepo.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        Optional<RefreshToken> existingToken = refreshTokenRepo.findByUser(user);

        if (existingToken.isPresent()) {
            // обновляем токен
            RefreshToken tokenEntity = existingToken.get();
            tokenEntity.setToken(refreshToken);
            refreshTokenRepo.save(tokenEntity);
        } else {
            RefreshToken tokenEntity = RefreshToken.builder()
                    .user(user)
                    .token(refreshToken)
                    .build();
            refreshTokenRepo.save(tokenEntity);
        }

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public JwtResponse login(AuthenticationRequest request) {
        log.info("Searching User with email {}", request.email());
        var user = userRepo.findByEmail(request.email()).orElseThrow(
                () -> new UsernameNotFoundException(ExceptionMessages.USER_WITH_CURRENT_EMAIL_NOT_FOUND.getMessage())
        );

        log.info("Logging In User");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        log.info("Return tokens");
        return JwtResponse
                .builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build();
    }
}