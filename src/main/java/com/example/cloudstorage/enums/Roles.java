package com.example.cloudstorage.enums;

import lombok.Getter;

@Getter
public enum Roles {
    DEFAULT_ROLE("ROLE_USER"),
    MODERATOR("ROLE_MODERATOR"),
    ADMIN("ROLE_ADMIN");

    public final String value;

    Roles(String role) {
        this.value = role;
    }
}