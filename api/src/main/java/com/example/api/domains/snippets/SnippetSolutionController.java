package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Solution;
import com.example.api.domains.snippets.requests.CreateSolutionRequest;
import com.example.api.domains.snippets.responses.SolutionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SnippetSolutionController {

    private final Solution service;

    public SnippetSolutionController(Solution service) {
        this.service = service;
    }

    @PostMapping("/snippets/{id}/solutions")
    public ResponseEntity<?> store(@PathVariable String id, @RequestBody CreateSolutionRequest request) {
        try {
            com.example.api.domains.snippets.domain.Solution solution = service.submit(id, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SolutionResponse.build(solution, request.author()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    public record ErrorResponse(String error) {}
}
