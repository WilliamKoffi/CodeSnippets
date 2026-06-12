package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.UserRepository;
import com.example.api.domains.snippets.dto.CreateSnippetRequest;
import com.example.api.domains.snippets.dto.TagResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class SnippetService {

    private final SnippetRepository snippetRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;

    public SnippetService(SnippetRepository snippetRepository, TagRepository tagRepository, UserRepository userRepository) {
        this.snippetRepository = snippetRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Snippet> getSnippets(String search, String tag, String type) {
        String cleanSearch = (search == null || search.trim().isEmpty()) ? null : search.trim();
        String cleanTag = (tag == null || tag.trim().isEmpty()) ? null : tag.trim();
        String cleanType = (type == null || type.trim().isEmpty()) ? null : type.trim();

        return snippetRepository.findAllFiltered(cleanSearch, cleanTag, cleanType);
    }

    @Transactional(readOnly = true)
    public Snippet getSnippet(String id) {
        return snippetRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Snippet not found with id: " + id));
    }

    public Snippet createSnippet(CreateSnippetRequest request) {
        User author = userRepository.findById(request.authorId())
            .orElseThrow(() -> new IllegalArgumentException("Author not found with id: " + request.authorId()));

        Snippet snippet = new Snippet(
            null, // Auto-generated ID in PrePersist
            request.title(),
            request.description(),
            request.code(),
            request.language(),
            request.type(),
            author
        );

        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = request.tags().stream()
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(name -> !name.isEmpty())
                .map(name -> tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name))))
                .collect(Collectors.toSet());
            snippet.updateTags(tags);
        }

        return snippetRepository.save(snippet);
    }

    @Transactional(readOnly = true)
    public List<TagResponse> getTags() {
        List<Object[]> results = tagRepository.findTagsWithUsageCount();
        Set<String> processedTags = new HashSet<>();
        List<TagResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            String name = (String) result[0];
            Long count = (Long) result[1];
            responses.add(TagResponse.from(name, count));
            processedTags.add(name.toLowerCase());
        }

        // Include any tags in the DB that currently have 0 count
        List<Tag> allTags = tagRepository.findAll();
        for (Tag tag : allTags) {
            if (!processedTags.contains(tag.name().toLowerCase())) {
                responses.add(TagResponse.from(tag.name(), 0L));
            }
        }

        return responses;
    }
}
