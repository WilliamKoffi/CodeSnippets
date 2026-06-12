package com.example.api.domains.auth.requests;

public record RegisterRequest(
    String name,
    String handle,
    String email,
    String password,
    String role
) {}
