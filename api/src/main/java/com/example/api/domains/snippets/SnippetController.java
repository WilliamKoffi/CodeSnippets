package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Snippet;
import com.example.api.domains.snippets.requests.CreateSnippetRequest;
import com.example.api.domains.snippets.responses.SnippetResponse;
import com.example.api.domains.snippets.repositories.LikeRepository;
import com.example.api.domains.snippets.repositories.BookmarkRepository;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to access the API during development
public class SnippetController {

    private final Snippet snippets;
    private final LikeRepository likes;
    private final BookmarkRepository bookmarks;
    private final VoteRepository votes;

    public SnippetController(
            Snippet snippets,
            LikeRepository likes,
            BookmarkRepository bookmarks,
            VoteRepository votes) {
        this.snippets = snippets;
        this.likes = likes;
        this.bookmarks = bookmarks;
        this.votes = votes;
    }

    @GetMapping("/snippets")
    public ResponseEntity<List<SnippetResponse>> index(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String viewer) {

        List<SnippetResponse> responses = snippets.search(search, tag, type).stream()
                .map(snippet -> SnippetResponse.build(snippet, viewer, likes, bookmarks, votes))
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/snippets/{id}")
    public ResponseEntity<?> show(
            @PathVariable String id,
            @RequestParam(required = false) String viewer) {
        try {
            com.example.api.domains.snippets.domain.Snippet snippet = snippets.find(id);
            return ResponseEntity.ok(SnippetResponse.build(snippet, viewer, likes, bookmarks, votes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/snippets")
    public ResponseEntity<?> store(@RequestBody CreateSnippetRequest request) {
        try {
            com.example.api.domains.snippets.domain.Snippet snippet = snippets.publish(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(SnippetResponse.build(snippet, request.authorId(), likes, bookmarks, votes));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()));
        }
    }

    // Helper inner class for simple error responses
    public record ErrorResponse(String error) {}
}
