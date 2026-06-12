package com.example.api.domains.snippets.responses;

import com.example.api.domains.auth.User;
import com.example.api.domains.snippets.domain.Solution;

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

    public static SolutionResponse build(Solution solution, String viewer) {
        User author = solution.author();
        SolutionAuthorResponse authorDto = new SolutionAuthorResponse(
            author.name(),
            author.avatar(),
            String.valueOf(author.reputation()),
            viewer != null && viewer.equals(author.id())
        );

        return new SolutionResponse(
            solution.id(),
            authorDto,
            solution.votes(),
            solution.content(),
            solution.accepted(),
            solution.code(),
            new Age(solution.created()).toString()
        );
    }
}
