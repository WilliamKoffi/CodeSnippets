package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Like;
import com.example.api.domains.snippets.repositories.LikeRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LikeController {

    private final LikeRepository likes;
    private final SnippetRepository snippets;
    private final UserRepository users;

    public LikeController(LikeRepository likes, SnippetRepository snippets, UserRepository users) {
        this.likes = likes;
        this.snippets = snippets;
        this.users = users;
    }

    @PostMapping("/snippets/{id}/likes")
    @Transactional
    public ResponseEntity<?> store(@PathVariable String id, @RequestParam String userId) {
        Snippet snippet = snippets.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Like> existing = likes.findBySnippetIdAndUserId(id, userId);
        if (existing.isEmpty()) {
            Like like = new Like(snippet, user);
            likes.save(like);
            snippet.like();
            snippets.save(snippet);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/snippets/{id}/likes")
    @Transactional
    public ResponseEntity<?> destroy(@PathVariable String id, @RequestParam String userId) {
        Optional<Like> existing = likes.findBySnippetIdAndUserId(id, userId);
        if (existing.isPresent()) {
            likes.delete(existing.get());
            Snippet snippet = snippets.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Snippet not found"));
            snippet.unlike();
            snippets.save(snippet);
        }

        return ResponseEntity.ok().build();
    }
}
