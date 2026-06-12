package com.example.api.domains.snippets.catalogs;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.SolutionRepository;
import com.example.api.domains.snippets.requests.CreateSolutionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolutionCatalogTests {

    private SolutionRepository solutions;
    private SnippetRepository snippets;
    private UserRepository users;
    private Solution catalog;

    @BeforeEach
    void setUp() {
        solutions = mock(SolutionRepository.class);
        snippets = mock(SnippetRepository.class);
        users = mock(UserRepository.class);
        catalog = new Solution(solutions, snippets, users);
    }

    @Test
    void submitCreatesAndSavesSolution() {
        User author = new User("user_author", "Author Name", "author_handle", "author@email.com", "hash", "avatar", 100, "fullstack", "Elite");
        Snippet snippet = new Snippet("snippet_1", "Snippet Title", "Snippet Desc", "code", "java", "snippet", author);

        when(snippets.findById("snippet_1")).thenReturn(Optional.of(snippet));
        when(users.findById("user_author")).thenReturn(Optional.of(author));
        when(solutions.save(any(com.example.api.domains.snippets.domain.Solution.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        CreateSolutionRequest request = new CreateSolutionRequest("user_author", "This is a fix", "System.out.println(1);");

        com.example.api.domains.snippets.domain.Solution created = catalog.submit("snippet_1", request);

        assertNotNull(created);
        assertEquals("This is a fix", created.content());
        assertEquals("System.out.println(1);", created.code());
        assertEquals(author, created.author());
        assertEquals(snippet, created.snippet());
        assertEquals(1, snippet.answers());
        verify(snippets).save(snippet);
        verify(solutions).save(any(com.example.api.domains.snippets.domain.Solution.class));
    }

    @Test
    void acceptSucceedsWhenViewerIsSnippetAuthor() {
        User snippetAuthor = new User("author_id", "Snippet Author", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        User solutionAuthor = new User("solution_author_id", "Sol Author", "handle2", "email2@test.com", "hash", "avatar", 50, "frontend", "Pro");
        Snippet snippet = new Snippet("snippet_1", "Snippet Title", "Desc", "code", "java", "snippet", snippetAuthor);
        com.example.api.domains.snippets.domain.Solution solution = new com.example.api.domains.snippets.domain.Solution(snippet, solutionAuthor, "Fix description", "code");

        when(solutions.findById("solution_1")).thenReturn(Optional.of(solution));
        when(solutions.save(any(com.example.api.domains.snippets.domain.Solution.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        com.example.api.domains.snippets.domain.Solution accepted = catalog.accept("solution_1", "author_id");

        assertTrue(accepted.accepted());
        verify(solutions).save(solution);
    }

    @Test
    void acceptFailsWhenViewerIsNotSnippetAuthor() {
        User snippetAuthor = new User("author_id", "Snippet Author", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        User solutionAuthor = new User("solution_author_id", "Sol Author", "handle2", "email2@test.com", "hash", "avatar", 50, "frontend", "Pro");
        Snippet snippet = new Snippet("snippet_1", "Snippet Title", "Desc", "code", "java", "snippet", snippetAuthor);
        com.example.api.domains.snippets.domain.Solution solution = new com.example.api.domains.snippets.domain.Solution(snippet, solutionAuthor, "Fix description", "code");

        when(solutions.findById("solution_1")).thenReturn(Optional.of(solution));

        assertThrows(IllegalArgumentException.class, () -> catalog.accept("solution_1", "other_user_id"));
        assertFalse(solution.accepted());
    }

    @Test
    void acceptFailsWhenAnotherSolutionIsAlreadyAccepted() {
        User snippetAuthor = new User("author_id", "Snippet Author", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        User firstAuthor = new User("solution_author_id", "Sol Author", "handle2", "email2@test.com", "hash", "avatar", 50, "frontend", "Pro");
        User secondAuthor = new User("solution_author_id_2", "Sol Author 2", "handle3", "email3@test.com", "hash", "avatar", 50, "frontend", "Pro");
        Snippet snippet = new Snippet("snippet_1", "Snippet Title", "Desc", "code", "java", "snippet", snippetAuthor);
        com.example.api.domains.snippets.domain.Solution accepted = new com.example.api.domains.snippets.domain.Solution(null, firstAuthor, "Accepted fix", "code");
        com.example.api.domains.snippets.domain.Solution pending = new com.example.api.domains.snippets.domain.Solution(null, secondAuthor, "Pending fix", "code");
        snippet.solve(accepted);
        snippet.solve(pending);
        accepted.accept();

        when(solutions.findById("solution_2")).thenReturn(Optional.of(pending));

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> catalog.accept("solution_2", "author_id"));
        assertTrue(error.getMessage().contains("already been accepted"));
        assertFalse(pending.accepted());
        verify(solutions, never()).save(any(com.example.api.domains.snippets.domain.Solution.class));
    }

    @Test
    void acceptReturnsSolutionWithoutSavingWhenAlreadyAccepted() {
        User snippetAuthor = new User("author_id", "Snippet Author", "handle", "email@test.com", "hash", "avatar", 100, "fullstack", "Elite");
        User solutionAuthor = new User("solution_author_id", "Sol Author", "handle2", "email2@test.com", "hash", "avatar", 50, "frontend", "Pro");
        Snippet snippet = new Snippet("snippet_1", "Snippet Title", "Desc", "code", "java", "snippet", snippetAuthor);
        com.example.api.domains.snippets.domain.Solution solution = new com.example.api.domains.snippets.domain.Solution(snippet, solutionAuthor, "Fix description", "code");
        solution.accept();

        when(solutions.findById("solution_1")).thenReturn(Optional.of(solution));

        com.example.api.domains.snippets.domain.Solution accepted = catalog.accept("solution_1", "author_id");

        assertTrue(accepted.accepted());
        verify(solutions, never()).save(any(com.example.api.domains.snippets.domain.Solution.class));
    }
}
