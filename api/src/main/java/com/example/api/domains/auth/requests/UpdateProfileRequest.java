package com.example.api.domains.auth.requests;

public record UpdateProfileRequest(
    String name,
    String handle,
    String avatar,
    String role,
    String level
) {}
