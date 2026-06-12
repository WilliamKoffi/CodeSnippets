package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.TagRepository;
import com.example.api.domains.snippets.requests.CreateSnippetRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SnippetCatalogTests {

    private SnippetRepository snippets;
    private TagRepository tags;
    private UserRepository users;
    private Snippet catalog;

    @BeforeEach
    void setUp() {
        snippets = mock(SnippetRepository.class);
        tags = mock(TagRepository.class);
        users = mock(UserRepository.class);
        catalog = new Snippet(snippets, tags, users);
    }

    @Test
    void searchNormalizesInputs() {
        com.example.api.domains.snippets.domain.Snippet snippet =
            new com.example.api.domains.snippets.domain.Snippet("id1", "Title", "Desc", "code", "java", "snippet", null);
        when(snippets.findAllFiltered(any(), any(), any())).thenReturn(List.of(snippet));

        List<com.example.api.domains.snippets.domain.Snippet> results = catalog.search(" search ", " tag ", " type ");

        assertEquals(1, results.size());
        assertEquals("id1", results.getFirst().id());
        verify(snippets).findAllFiltered("search", "tag", "type");
    }

    @Test
    void findReturnsSnippet() {
        com.example.api.domains.snippets.domain.Snippet snippet =
            new com.example.api.domains.snippets.domain.Snippet("id1", "Title", "Desc", "code", "java", "snippet", null);
        when(snippets.findById("id1")).thenReturn(Optional.of(snippet));

        com.example.api.domains.snippets.domain.Snippet result = catalog.find("id1");

        assertNotNull(result);
        assertEquals("Title", result.title());
    }

    @Test
    void findRejectsUnknownIdentifier() {
        when(snippets.findById("invalid")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> catalog.find("invalid"));
    }

    @Test
    void publishBuildsSnippetFromRequest() {
        User user = new User("user1", "Name", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        when(users.findById("user1")).thenReturn(Optional.of(user));

        com.example.api.domains.snippets.domain.Tag tag = new com.example.api.domains.snippets.domain.Tag("react");
        when(tags.findByName("react")).thenReturn(Optional.of(tag));

        CreateSnippetRequest request = new CreateSnippetRequest(
            "My Snippet",
            "Description",
            "const x = 1;",
            "javascript",
            "snippet",
            List.of("react"),
            "user1"
        );

        when(snippets.save(any(com.example.api.domains.snippets.domain.Snippet.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        com.example.api.domains.snippets.domain.Snippet created = catalog.publish(request);

        assertNotNull(created);
        assertEquals("My Snippet", created.title());
        assertEquals("javascript", created.language());
        assertEquals(user, created.author());
        assertEquals(1, created.tags().size());
        assertTrue(created.tags().contains(tag));
    }
}
