package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.snippets.repositories.TagRepository;
import com.example.api.domains.snippets.responses.TagResponse;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Transactional(readOnly = true)
public class Tag {

    private final TagRepository tags;

    public Tag(TagRepository tags) {
        this.tags = tags;
    }

    public List<TagResponse> summarize() {
        List<Object[]> results = tags.findTagsWithUsageCount();
        Set<String> known = new HashSet<>();
        List<TagResponse> responses = new ArrayList<>();

        for (Object[] result : results) {
            String name = (String) result[0];
            Long count = (Long) result[1];
            responses.add(TagResponse.from(name, count));
            known.add(name.toLowerCase());
        }

        List<com.example.api.domains.snippets.domain.Tag> catalog = tags.findAll();
        for (com.example.api.domains.snippets.domain.Tag tag : catalog) {
            if (!known.contains(tag.name().toLowerCase())) {
                responses.add(TagResponse.from(tag.name(), 0L));
            }
        }

        return responses;
    }
}
