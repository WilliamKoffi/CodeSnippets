package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Solution;
import com.example.api.domains.snippets.responses.SolutionResponse;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AcceptedSolutionController {

    private final Solution service;
    private final VoteRepository votes;

    public AcceptedSolutionController(Solution service, VoteRepository votes) {
        this.service = service;
        this.votes = votes;
    }

    @PutMapping("/solutions/{id}/accept")
    public ResponseEntity<?> store(@PathVariable String id, @RequestParam String viewer) {
        try {
            com.example.api.domains.snippets.domain.Solution solution = service.accept(id, viewer);
            return ResponseEntity.ok(SolutionResponse.build(solution, viewer, votes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(e.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
