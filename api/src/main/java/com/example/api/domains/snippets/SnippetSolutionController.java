package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Solution;
import com.example.api.domains.snippets.requests.CreateSolutionRequest;
import com.example.api.domains.snippets.responses.SolutionResponse;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SnippetSolutionController {

    private final Solution service;
    private final VoteRepository votes;

    public SnippetSolutionController(Solution service, VoteRepository votes) {
        this.service = service;
        this.votes = votes;
    }

    @PostMapping("/snippets/{id}/solutions")
    public ResponseEntity<?> store(@PathVariable String id, @RequestBody CreateSolutionRequest request) {
        try {
            com.example.api.domains.snippets.domain.Solution solution = service.submit(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SolutionResponse.build(solution, request.author(), votes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
