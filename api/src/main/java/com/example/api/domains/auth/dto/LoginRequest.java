package com.example.api.domains.auth.dto;

public record LoginRequest(
    String email,
    String password
) {}
