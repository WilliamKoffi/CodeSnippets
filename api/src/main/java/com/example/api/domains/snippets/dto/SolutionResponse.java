package com.example.api.domains.snippets.dto;

import com.example.api.domains.snippets.Solution;
import com.example.api.domains.auth.User;
import java.time.LocalDateTime;

public record SolutionResponse(
    String id,
    SolutionAuthorResponse author,
    int votes,
    String content,
    boolean accepted,
    String code,
    String createdAt
) {
    public record SolutionAuthorResponse(
        String name,
        String avatar,
        String reputation,
        Boolean isAuthor
    ) {}

    public static SolutionResponse fromSolution(Solution solution, String currentUserId) {
        User author = solution.author();
        SolutionAuthorResponse authorDto = new SolutionAuthorResponse(
            author.name(),
            author.avatar(),
            String.valueOf(author.reputation()),
            currentUserId != null && currentUserId.equals(author.id())
        );

        return new SolutionResponse(
            solution.id(),
            authorDto,
            solution.votes(),
            solution.content(),
            solution.accepted(),
            solution.code(),
            formatRelativeTime(solution.created())
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
