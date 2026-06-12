package com.example.api.domains.auth.responses;

import com.example.api.domains.auth.User;

public record UserResponse(
    String id,
    String name,
    String handle,
    String avatar,
    int reputation,
    String role,
    String level
) {
    public static UserResponse build(User user) {
        return new UserResponse(
            user.id(),
            user.name(),
            user.handle(),
            user.avatar(),
            user.reputation(),
            user.role(),
            user.level()
        );
    }
}
