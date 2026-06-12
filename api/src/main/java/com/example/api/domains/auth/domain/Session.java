package com.example.api.domains.auth.domain;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.LoginRequest;

public final class Session {

    private Session() {}

    public static User establish(LoginRequest request, UserRepository directory) {
        User user = directory.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!Password.matches(request.password(), user.password())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }
}
