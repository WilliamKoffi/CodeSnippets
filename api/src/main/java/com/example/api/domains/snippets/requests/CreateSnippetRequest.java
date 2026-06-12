package com.example.api.domains.snippets.requests;

import java.util.List;

public record CreateSnippetRequest(
    String title,
    String description,
    String code,
    String language,
    String type,
    List<String> tags,
    String authorId
) {}
