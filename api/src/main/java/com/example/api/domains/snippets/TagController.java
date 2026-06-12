package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Tag;
import com.example.api.domains.snippets.responses.TagResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow frontend to access the API during development
public class TagController {

    private final Tag tags;

    public TagController(Tag tags) {
        this.tags = tags;
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> index() {
        return ResponseEntity.ok(tags.summarize());
    }
}
