package com.example.api.domains.auth.domain;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;

public final class Account {

    private Account() {
    }

    public static User locate(String id, UserRepository directory) {
        return directory.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
