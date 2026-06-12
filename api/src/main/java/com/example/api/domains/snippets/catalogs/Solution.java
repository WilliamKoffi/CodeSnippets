package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.SolutionRepository;
import com.example.api.domains.snippets.requests.CreateSolutionRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class Solution {

    private final SolutionRepository solutions;
    private final SnippetRepository snippets;
    private final UserRepository users;

    public Solution(
            SolutionRepository solutions,
            SnippetRepository snippets,
            UserRepository users) {
        this.solutions = solutions;
        this.snippets = snippets;
        this.users = users;
    }

    public com.example.api.domains.snippets.domain.Solution submit(String identity, CreateSolutionRequest payload) {
        Snippet snippet = snippet(identity);

        User creator = creator(payload.author());

        com.example.api.domains.snippets.domain.Solution draft = new com.example.api.domains.snippets.domain.Solution(
            snippet,
            creator,
            payload.content(),
            payload.code()
        );

        snippet.solve(draft);
        snippets.save(snippet);
        return solutions.save(draft);
    }

    public com.example.api.domains.snippets.domain.Solution accept(String identity, String viewer) {
        com.example.api.domains.snippets.domain.Solution solution = solution(identity);

        Snippet snippet = solution.snippet();
        if (snippet == null) {
            throw new IllegalStateException("Solution is not associated with any snippet");
        }

        if (!snippet.accept(solution, viewer)) {
            return solution;
        }

        return solutions.save(solution);
    }

    private Snippet snippet(String identity) {
        return snippets.findById(identity)
            .orElseThrow(() -> new IllegalArgumentException("Snippet not found with id: " + identity));
    }

    private User creator(String identity) {
        return users.findById(identity)
            .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + identity));
    }

    private com.example.api.domains.snippets.domain.Solution solution(String identity) {
        return solutions.findById(identity)
            .orElseThrow(() -> new IllegalArgumentException("Solution not found with id: " + identity));
    }
}
