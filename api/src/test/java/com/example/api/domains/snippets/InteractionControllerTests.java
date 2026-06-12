package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Snippet;
import com.example.api.domains.snippets.domain.Bookmark;
import com.example.api.domains.snippets.domain.Like;
import com.example.api.domains.snippets.domain.Solution;
import com.example.api.domains.snippets.domain.Vote;
import com.example.api.domains.snippets.repositories.BookmarkRepository;
import com.example.api.domains.snippets.repositories.LikeRepository;
import com.example.api.domains.snippets.repositories.SnippetRepository;
import com.example.api.domains.snippets.repositories.SolutionRepository;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InteractionControllerTests {

    private LikeRepository likeRepository;
    private BookmarkRepository bookmarkRepository;
    private VoteRepository voteRepository;
    private SnippetRepository snippetRepository;
    private SolutionRepository solutionRepository;
    private UserRepository userRepository;

    private LikeController likeController;
    private BookmarkController bookmarkController;
    private VoteController voteController;

    private Snippet snippet;
    private Solution solution;
    private User user;

    @BeforeEach
    void setUp() {
        likeRepository = mock(LikeRepository.class);
        bookmarkRepository = mock(BookmarkRepository.class);
        voteRepository = mock(VoteRepository.class);
        snippetRepository = mock(SnippetRepository.class);
        solutionRepository = mock(SolutionRepository.class);
        userRepository = mock(UserRepository.class);

        likeController = new LikeController(likeRepository, snippetRepository, userRepository);
        bookmarkController = new BookmarkController(bookmarkRepository, snippetRepository, userRepository);
        voteController = new VoteController(voteRepository, solutionRepository, userRepository);

        snippet = mock(Snippet.class);
        solution = mock(Solution.class);
        user = mock(User.class);

        when(snippetRepository.findById("snippet_1")).thenReturn(Optional.of(snippet));
        when(solutionRepository.findById("solution_1")).thenReturn(Optional.of(solution));
        when(userRepository.findById("user_1")).thenReturn(Optional.of(user));
    }

    @Test
    void likeStoreCreatesLikeAndIncrementsCount() {
        when(likeRepository.findBySnippetIdAndUserId("snippet_1", "user_1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = likeController.store("snippet_1", "user_1");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(likeRepository).save(any(Like.class));
        verify(snippet).like();
        verify(snippetRepository).save(snippet);
    }

    @Test
    void likeDestroyDeletesLikeAndDecrementsCount() {
        Like like = mock(Like.class);
        when(likeRepository.findBySnippetIdAndUserId("snippet_1", "user_1")).thenReturn(Optional.of(like));

        ResponseEntity<?> response = likeController.destroy("snippet_1", "user_1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(likeRepository).delete(like);
        verify(snippet).unlike();
        verify(snippetRepository).save(snippet);
    }

    @Test
    void bookmarkStoreCreatesBookmark() {
        when(bookmarkRepository.findBySnippetIdAndUserId("snippet_1", "user_1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = bookmarkController.store("snippet_1", "user_1");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(bookmarkRepository).save(any(Bookmark.class));
    }

    @Test
    void bookmarkDestroyDeletesBookmark() {
        Bookmark bookmark = mock(Bookmark.class);
        when(bookmarkRepository.findBySnippetIdAndUserId("snippet_1", "user_1")).thenReturn(Optional.of(bookmark));

        ResponseEntity<?> response = bookmarkController.destroy("snippet_1", "user_1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(bookmarkRepository).delete(bookmark);
    }

    @Test
    void voteStoreNewVoteUpvotesSolution() {
        when(voteRepository.findBySolutionIdAndUserId("solution_1", "user_1")).thenReturn(Optional.empty());

        ResponseEntity<?> response = voteController.store("solution_1", "user_1", "up");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(voteRepository).save(any(Vote.class));
        verify(solution).upvote();
        verify(solutionRepository).save(solution);
    }

    @Test
    void voteStoreChangesVoteDownToUp() {
        Vote existingVote = mock(Vote.class);
        when(existingVote.direction()).thenReturn("down");
        when(voteRepository.findBySolutionIdAndUserId("solution_1", "user_1")).thenReturn(Optional.of(existingVote));

        ResponseEntity<?> response = voteController.store("solution_1", "user_1", "up");

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(existingVote).updateDirection("up");
        verify(voteRepository).save(existingVote);
        verify(solution, times(2)).upvote();
        verify(solutionRepository).save(solution);
    }

    @Test
    void voteDestroyRemovesUpvote() {
        Vote existingVote = mock(Vote.class);
        when(existingVote.direction()).thenReturn("up");
        when(voteRepository.findBySolutionIdAndUserId("solution_1", "user_1")).thenReturn(Optional.of(existingVote));

        ResponseEntity<?> response = voteController.destroy("solution_1", "user_1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(solution).downvote();
        verify(voteRepository).delete(existingVote);
        verify(solutionRepository).save(solution);
    }
}

