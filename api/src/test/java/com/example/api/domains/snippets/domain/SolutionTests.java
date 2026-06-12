package com.example.api.domains.snippets.domain;

import com.example.api.domains.auth.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolutionTests {

    @Test
    void startsWithLifecycleDefaults() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Solution solution = new Solution(null, author, "Answer", "code");

        assertNull(solution.id());
        assertNull(solution.snippet());
        assertEquals(0, solution.votes());
        assertFalse(solution.accepted());
    }

    @Test
    void affordancesControlLifecycleState() {
        User author = new User("user_1", "Ada Lovelace", "ada", "ada@example.com", "hash", "avatar.png", 42, "backend", "Expert");
        Solution solution = new Solution(null, author, "Answer", "code");

        solution.upvote();
        solution.upvote();
        solution.downvote();
        solution.accept();

        assertEquals(1, solution.votes());
        assertTrue(solution.accepted());
    }
}
