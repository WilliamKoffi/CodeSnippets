package com.example.api.domains.snippets.responses;

import com.example.api.domains.auth.User;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Tag;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public record SnippetResponse(
    String id,
    String title,
    String description,
    String code,
    String language,
    List<String> tags,
    AuthorResponse author,
    int likes,
    int tally,
    String age,
    String type,
    List<SolutionResponse> solutions,
    boolean liked,
    boolean saved
) {
    public record AuthorResponse(
        String name,
        String handle,
        String avatar,
        String reputation,
        boolean owner
    ) {}

    public static SnippetResponse build(Snippet snippet, String viewer) {
        User creator = snippet.author();
        AuthorResponse author = new AuthorResponse(
            creator.name(),
            creator.handle(),
            creator.avatar(),
            String.valueOf(creator.reputation()),
            viewer != null && viewer.equals(creator.id())
        );

        List<String> labels = snippet.tags().stream()
            .map(Tag::name)
            .toList();

        List<SolutionResponse> answers = snippet.solutions() == null ? List.of() :
            snippet.solutions().stream()
                .map(solution -> SolutionResponse.build(solution, viewer))
                .toList();

        return new SnippetResponse(
            snippet.id(),
            snippet.title(),
            snippet.description(),
            snippet.code(),
            snippet.language(),
            labels,
            author,
            snippet.likes(),
            snippet.answers(),
            new Age(snippet.created()).toString(),
            snippet.type(),
            answers,
            false,
            false
        );
    }
}

record Age(LocalDateTime time) {
    @Override
    public String toString() {
        if (time == null) return "À l'instant";

        long seconds = Math.abs(Duration.between(time, LocalDateTime.now()).getSeconds());

        if (seconds < 60) return "À l'instant";

        long minutes = seconds / 60;
        if (minutes < 60) return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");

        long hours = minutes / 60;
        if (hours < 24) return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");

        long days = hours / 24;
        return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
    }
}
