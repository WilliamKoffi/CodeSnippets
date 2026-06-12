package com.example.api.domains.snippets.responses;

import com.example.api.domains.auth.User;
import com.example.api.domains.snippets.domain.Solution;
import com.example.api.domains.snippets.domain.Vote;
import com.example.api.domains.snippets.repositories.VoteRepository;

public record SolutionResponse(
    String id,
    SolutionAuthorResponse author,
    int votes,
    String content,
    boolean accepted,
    String code,
    String createdAt,
    String voted
) {
    public record SolutionAuthorResponse(
        String name,
        String avatar,
        String reputation,
        Boolean isAuthor
    ) {}

    public static SolutionResponse build(Solution solution, String viewer) {
        return build(solution, viewer, null);
    }

    public static SolutionResponse build(Solution solution, String viewer, VoteRepository voteRepo) {
        User author = solution.author();
        SolutionAuthorResponse authorDto = new SolutionAuthorResponse(
            author.name(),
            author.avatar(),
            String.valueOf(author.reputation()),
            viewer != null && viewer.equals(author.id())
        );

        String voted = null;
        if (viewer != null && voteRepo != null) {
            voted = voteRepo.findBySolutionIdAndUserId(solution.id(), viewer)
                .map(Vote::direction)
                .orElse(null);
        }

        return new SolutionResponse(
            solution.id(),
            authorDto,
            solution.votes(),
            solution.content(),
            solution.accepted(),
            solution.code(),
            new Age(solution.created()).toString(),
            voted
        );
    }
}
