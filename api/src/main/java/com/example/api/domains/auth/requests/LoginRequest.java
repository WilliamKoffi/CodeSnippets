package com.example.api.domains.auth.requests;

public record LoginRequest(
    String email,
    String password
) {}
