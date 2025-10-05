package com.example.cloudstorage.services;

import com.example.cloudstorage.exceptions.handler.ExceptionMessages;
import com.example.cloudstorage.repos.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public CustomUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepo.findByEmailWithAuthorities(email)
                .orElseThrow(() -> new UsernameNotFoundException(ExceptionMessages.USER_WITH_CURRENT_EMAIL_NOT_FOUND.getMessage()));
    }
}