package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Snippet;
import com.example.api.domains.snippets.requests.CreateSnippetRequest;
import com.example.api.domains.snippets.responses.SnippetResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to access the API during development
public class SnippetController {

    private final Snippet snippets;

    public SnippetController(Snippet snippets) {
        this.snippets = snippets;
    }

    @GetMapping("/snippets")
    public ResponseEntity<List<SnippetResponse>> index(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String type) {

        List<SnippetResponse> responses = snippets.search(search, tag, type).stream()
                .map(snippet -> SnippetResponse.build(snippet, null))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/snippets/{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        try {
            com.example.api.domains.snippets.domain.Snippet snippet = snippets.find(id);
            return ResponseEntity.ok(SnippetResponse.build(snippet, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/snippets")
    public ResponseEntity<?> store(@RequestBody CreateSnippetRequest request) {
        try {
            com.example.api.domains.snippets.domain.Snippet snippet = snippets.publish(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SnippetResponse.build(snippet, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper inner class for simple error responses
    public record ErrorResponse(String error) {}
}
