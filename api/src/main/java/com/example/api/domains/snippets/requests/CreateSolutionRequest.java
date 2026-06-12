package com.example.api.domains.snippets.requests;

public record CreateSolutionRequest(
    String author,
    String content,
    String code
) {}
