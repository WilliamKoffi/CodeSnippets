package com.example.api.domains.auth.dto;

public record UpdateProfileRequest(
    String name,
    String handle,
    String avatar,
    String role,
    String level
) {}
