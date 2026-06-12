package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.TagRepository;
import com.example.api.domains.snippets.requests.CreateSnippetRequest;
import com.example.api.domains.snippets.domain.Tag;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Transactional
public class Snippet {

    private final SnippetRepository snippets;
    private final TagRepository tags;
    private final UserRepository users;

    public Snippet(SnippetRepository snippets, TagRepository tags, UserRepository users) {
        this.snippets = snippets;
        this.tags = tags;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<com.example.api.domains.snippets.domain.Snippet> search(String query, String tag, String kind) {
        query = normalize(query);
        tag = normalize(tag);
        kind = normalize(kind);

        return snippets.findAllFiltered(query, tag, kind);
    }

    @Transactional(readOnly = true)
    public com.example.api.domains.snippets.domain.Snippet find(String identifier) {
        return snippets.findById(identifier)
            .orElseThrow(() -> new IllegalArgumentException("Snippet not found with id: " + identifier));
    }

    public com.example.api.domains.snippets.domain.Snippet publish(CreateSnippetRequest request) {
        User author = users.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + request.authorId()));

        com.example.api.domains.snippets.domain.Snippet draft = new com.example.api.domains.snippets.domain.Snippet(
            null,
            request.title(),
            request.description(),
            request.code(),
            request.language(),
            request.type(),
            author
        );

        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> bound = request.tags().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(name -> !name.isEmpty())
                .map(name -> tags.findByName(name)
                    .orElseGet(() -> tags.save(new Tag(name))))
                .collect(Collectors.toSet());
            draft.tag(bound);
        }

        return snippets.save(draft);
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }

        value = value.trim();
        return value.isEmpty() ? null : value;
    }
}
