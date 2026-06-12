package com.example.api.domains.snippets.dto;

import com.example.api.domains.snippets.Snippet;
import com.example.api.domains.snippets.Tag;
import com.example.api.domains.auth.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record SnippetResponse(
    String id,
    String title,
    String description,
    String code,
    String language,
    List<String> tags,
    SnippetAuthorResponse author,
    int likes,
    int solutionsCount,
    String createdAt,
    String type,
    List<SolutionResponse> solutions,
    boolean isLikedByMe,
    boolean isSavedByMe
) {
    public record SnippetAuthorResponse(
        String name,
        String handle,
        String avatar,
        String reputation,
        Boolean isAuthor
    ) {}

    public static SnippetResponse fromSnippet(Snippet snippet, String currentUserId) {
        User author = snippet.author();
        SnippetAuthorResponse authorDto = new SnippetAuthorResponse(
            author.name(),
            author.handle(),
            author.avatar(),
            String.valueOf(author.reputation()),
            currentUserId != null && currentUserId.equals(author.id())
        );

        List<String> tagNames = snippet.tags().stream()
            .map(Tag::name)
            .collect(Collectors.toList());

        List<SolutionResponse> solutionsList = snippet.solutions() == null ? List.of() :
            snippet.solutions().stream()
                .map(s -> SolutionResponse.fromSolution(s, currentUserId))
                .collect(Collectors.toList());

        return new SnippetResponse(
            snippet.id(),
            snippet.title(),
            snippet.description(),
            snippet.code(),
            snippet.language(),
            tagNames,
            authorDto,
            snippet.likesCount(),
            snippet.solutionsCount(),
            formatRelativeTime(snippet.created()),
            snippet.type(),
            solutionsList,
            false,
            false
        );
    }

    private static String formatRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) return "À l'instant";
        java.time.Duration duration = java.time.Duration.between(dateTime, LocalDateTime.now());
        long seconds = Math.abs(duration.getSeconds());
        if (seconds < 60) {
            return "À l'instant";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        }
        long days = hours / 24;
        return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
    }
}
