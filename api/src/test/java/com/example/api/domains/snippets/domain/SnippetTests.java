package com.example.api.domains.snippets.domain;

import com.example.api.domains.auth.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnippetTests {

    @Test
    void solveAttachesSolutionAndIncrementsAnswersOnce() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Solution solution = new Solution(null, author, "Answer", "code");

        snippet.solve(solution);
        snippet.solve(solution);

        assertEquals(1, snippet.answers());
        assertEquals(1, snippet.solutions().size());
        assertSame(snippet, solution.snippet());
    }

    @Test
    void solveRejectsSolutionFromAnotherSnippet() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Snippet first = new Snippet("snippet_1", "First", "Description", "code", "java", "snippet", author);
        Snippet second = new Snippet("snippet_2", "Second", "Description", "code", "java", "snippet", author);
        Solution solution = new Solution(null, author, "Answer", "code");

        first.solve(solution);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> second.solve(solution));
        assertTrue(error.getMessage().contains("another snippet"));
    }

    @Test
    void unlikeStopsAtZeroAndTagReplacesLabels() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Tag first = new Tag("react");
        Tag second = new Tag("java");

        snippet.like();
        snippet.unlike();
        snippet.unlike();
        snippet.tag(Set.of(first));
        snippet.tag(Set.of(second));

        assertEquals(0, snippet.likes());
        assertEquals(Set.of(second), snippet.tags());
    }

    @Test
    void acceptMarksSolutionWhenViewerOwnsSnippet() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        User helper = new User("user_2", "Grace Hopper", "grace", "grace@example.com", "hash", "avatar.png", 21, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Solution solution = new Solution(null, helper, "Answer", "code");

        snippet.solve(solution);

        assertTrue(snippet.accept(solution, "user_1"));
        assertTrue(solution.accepted());
    }

    @Test
    void acceptRejectsViewerOutsideSnippetOwnership() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        User helper = new User("user_2", "Grace Hopper", "grace", "grace@example.com", "hash", "avatar.png", 21, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Solution solution = new Solution(null, helper, "Answer", "code");

        snippet.solve(solution);

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> snippet.accept(solution, "user_3"));
        assertTrue(error.getMessage().contains("Only the author"));
        assertFalse(solution.accepted());
    }

    @Test
    void acceptRejectsSecondAcceptedSolution() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        User first = new User("user_2", "Grace Hopper", "grace", "grace@example.com", "hash", "avatar.png", 21, "backend", "Expert");
        User second = new User("user_3", "Margaret Hamilton", "margaret", "margaret@example.com", "hash", "avatar.png", 21, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Solution accepted = new Solution(null, first, "Answer", "code");
        Solution pending = new Solution(null, second, "Another", "code");

        snippet.solve(accepted);
        snippet.solve(pending);
        snippet.accept(accepted, "user_1");

        IllegalStateException error = assertThrows(IllegalStateException.class, () -> snippet.accept(pending, "user_1"));
        assertTrue(error.getMessage().contains("already been accepted"));
        assertFalse(pending.accepted());
    }

    @Test
    void acceptReturnsFalseWhenSolutionIsAlreadyAccepted() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        User helper = new User("user_2", "Grace Hopper", "grace", "grace@example.com", "hash", "avatar.png", 21, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", author);
        Solution solution = new Solution(null, helper, "Answer", "code");

        snippet.solve(solution);
        snippet.accept(solution, "user_1");

        assertFalse(snippet.accept(solution, "user_1"));
    }
}
