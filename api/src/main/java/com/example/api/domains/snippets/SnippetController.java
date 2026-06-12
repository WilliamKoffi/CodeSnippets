package com.example.api.domains.snippets;

import com.example.api.domains.snippets.dto.CreateSnippetRequest;
import com.example.api.domains.snippets.dto.SnippetResponse;
import com.example.api.domains.snippets.dto.TagResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to access the API during development
public class SnippetController {

    private final SnippetService snippetService;

    public SnippetController(SnippetService snippetService) {
        this.snippetService = snippetService;
    }

    @GetMapping("/snippets")
    public ResponseEntity<List<SnippetResponse>> getSnippets(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String type) {
        
        List<SnippetResponse> responses = snippetService.getSnippets(search, tag, type).stream()
                .map(snippet -> SnippetResponse.fromSnippet(snippet, null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/snippets/{id}")
    public ResponseEntity<?> getSnippet(@PathVariable String id) {
        try {
            Snippet snippet = snippetService.getSnippet(id);
            return ResponseEntity.ok(SnippetResponse.fromSnippet(snippet, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/snippets")
    public ResponseEntity<?> createSnippet(@RequestBody CreateSnippetRequest request) {
        try {
            Snippet snippet = snippetService.createSnippet(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SnippetResponse.fromSnippet(snippet, null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getTags() {
        return ResponseEntity.ok(snippetService.getTags());
    }

    // Helper inner class for simple error responses
    public record ErrorResponse(String error) {}
}
