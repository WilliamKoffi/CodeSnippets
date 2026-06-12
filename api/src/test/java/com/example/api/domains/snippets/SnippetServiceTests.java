package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.UserRepository;
import com.example.api.domains.snippets.dto.CreateSnippetRequest;
import com.example.api.domains.snippets.dto.TagResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SnippetServiceTests {

    private SnippetRepository snippetRepository;
    private TagRepository tagRepository;
    private UserRepository userRepository;
    private SnippetService snippetService;

    @BeforeEach
    void setUp() {
        snippetRepository = mock(SnippetRepository.class);
        tagRepository = mock(TagRepository.class);
        userRepository = mock(UserRepository.class);
        snippetService = new SnippetService(snippetRepository, tagRepository, userRepository);
    }

    @Test
    void testGetSnippets() {
        Snippet snippet = new Snippet("id1", "Title", "Desc", "code", "java", "snippet", null);
        when(snippetRepository.findAllFiltered(any(), any(), any())).thenReturn(List.of(snippet));

        List<Snippet> results = snippetService.getSnippets("search", "tag", "type");
        assertEquals(1, results.size());
        assertEquals("id1", results.get(0).id());
        verify(snippetRepository).findAllFiltered("search", "tag", "type");
    }

    @Test
    void testGetSnippetById() {
        Snippet snippet = new Snippet("id1", "Title", "Desc", "code", "java", "snippet", null);
        when(snippetRepository.findById("id1")).thenReturn(Optional.of(snippet));

        Snippet result = snippetService.getSnippet("id1");
        assertNotNull(result);
        assertEquals("Title", result.title());
    }

    @Test
    void testGetSnippetByIdNotFound() {
        when(snippetRepository.findById("invalid")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> snippetService.getSnippet("invalid"));
    }

    @Test
    void testCreateSnippet() {
        User user = new User("user1", "Name", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        when(userRepository.findById("user1")).thenReturn(Optional.of(user));

        Tag tag = new Tag("react");
        when(tagRepository.findByName("react")).thenReturn(Optional.of(tag));

        CreateSnippetRequest request = new CreateSnippetRequest(
            "My Snippet",
            "Description",
            "const x = 1;",
            "javascript",
            "snippet",
            List.of("react"),
            "user1"
        );

        when(snippetRepository.save(any(Snippet.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Snippet created = snippetService.createSnippet(request);

        assertNotNull(created);
        assertEquals("My Snippet", created.title());
        assertEquals("javascript", created.language());
        assertEquals(user, created.author());
        assertEquals(1, created.tags().size());
        assertTrue(created.tags().contains(tag));
    }
}
