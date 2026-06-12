package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Bookmark;
import com.example.api.domains.snippets.repositories.BookmarkRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class BookmarkController {

    private final BookmarkRepository bookmarks;
    private final SnippetRepository snippets;
    private final UserRepository users;

    public BookmarkController(BookmarkRepository bookmarks, SnippetRepository snippets, UserRepository users) {
        this.bookmarks = bookmarks;
        this.snippets = snippets;
        this.users = users;
    }

    @PostMapping("/snippets/{id}/bookmarks")
    @Transactional
    public ResponseEntity<?> store(@PathVariable String id, @RequestParam String userId) {
        Snippet snippet = snippets.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Snippet not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Bookmark> existing = bookmarks.findBySnippetIdAndUserId(id, userId);
        if (existing.isEmpty()) {
            Bookmark bookmark = new Bookmark(snippet, user);
            bookmarks.save(bookmark);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/snippets/{id}/bookmarks")
    @Transactional
    public ResponseEntity<?> destroy(@PathVariable String id, @RequestParam String userId) {
        Optional<Bookmark> existing = bookmarks.findBySnippetIdAndUserId(id, userId);
        existing.ifPresent(bookmarks::delete);
        return ResponseEntity.ok().build();
    }
}
