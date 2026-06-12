package com.example.api.domains.auth.domain;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.auth.requests.RegisterRequest;

import java.util.UUID;

public final class Registration {

    private Registration() {
    }

    public static User process(RegisterRequest request, UserRepository directory) {
        String handle = request.handle().replace("@", "");

        if (directory.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already taken");
        }
        if (directory.existsByHandle(handle)) {
            throw new IllegalArgumentException("Handle already taken");
        }

        User user = new User(
                UUID.randomUUID().toString(),
                request.name(),
                handle,
                request.email(),
                Password.secure(request.password()),
                "https://api.dicebear.com/7.x/bottts/svg?seed=" + handle,
                120,
                request.role() != null ? request.role() : "fullstack",
                "Aspirant"
        );

        return directory.save(user);
    }
}
