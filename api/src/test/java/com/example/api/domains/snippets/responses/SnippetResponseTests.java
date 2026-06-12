package com.example.api.domains.snippets.responses;

import com.example.api.domains.auth.User;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Solution;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnippetResponseTests {

    @Test
    void buildsWithOneWordAffordances() {
        User creator = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Snippet snippet = new Snippet("snippet_1", "Title", "Description", "code", "java", "snippet", creator);
        Solution solution = new Solution(null, creator, "Answer", "code");
        solution.upvote();
        solution.upvote();
        solution.upvote();
        snippet.solve(solution);

        SnippetResponse response = SnippetResponse.build(snippet, "user_1");

        assertEquals("snippet_1", response.id());
        assertEquals(1, response.tally());
        assertEquals("À l'instant", response.age());
        assertTrue(response.author().owner());
        assertFalse(response.liked());
        assertFalse(response.saved());
    }

    @Test
    void formatsAgeFromTime() {
        assertEquals("À l'instant", new Age(null).toString());
        assertEquals("Il y a 2 minutes", new Age(LocalDateTime.now().minusMinutes(2)).toString());
        assertEquals("Il y a 3 heures", new Age(LocalDateTime.now().minusHours(3)).toString());
        assertEquals("Il y a 4 jours", new Age(LocalDateTime.now().minusDays(4)).toString());
    }
}
