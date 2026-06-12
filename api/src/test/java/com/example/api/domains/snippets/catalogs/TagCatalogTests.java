package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.snippets.repositories.TagRepository;
import com.example.api.domains.snippets.responses.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TagCatalogTests {

    private TagRepository tags;
    private Tag catalog;

    @BeforeEach
    void setUp() {
        tags = mock(TagRepository.class);
        catalog = new Tag(tags);
    }

    @Test
    void summarizeIncludesUnusedTags() {
        when(tags.findTagsWithUsageCount()).thenReturn(List.<Object[]>of(new Object[]{"java", 2L}));
        when(tags.findAll()).thenReturn(List.of(
            new com.example.api.domains.snippets.domain.Tag("java"),
            new com.example.api.domains.snippets.domain.Tag("spring")
        ));

        List<TagResponse> results = catalog.summarize();

        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(tag -> tag.name().equals("java") && tag.count().equals("2")));
        assertTrue(results.stream().anyMatch(tag -> tag.name().equals("spring") && tag.count().equals("0")));
    }
}
