package com.example.api.domains.snippets;

import com.example.api.domains.snippets.catalogs.Solution;
import com.example.api.domains.snippets.requests.CreateSolutionRequest;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class SolutionControllerTests {

    private Solution service;
    private VoteRepository votes;
    private SnippetSolutionController snippetSolutionController;
    private AcceptedSolutionController acceptedSolutionController;

    @BeforeEach
    void setUp() {
        service = mock(Solution.class);
        votes = mock(VoteRepository.class);
        snippetSolutionController = new SnippetSolutionController(service, votes);
        acceptedSolutionController = new AcceptedSolutionController(service, votes);
    }

    @Test
    void storeReturnsCreatedStatus() {
        com.example.api.domains.snippets.domain.Solution mockSolution = mock(com.example.api.domains.snippets.domain.Solution.class);
        com.example.api.domains.auth.User mockUser = mock(com.example.api.domains.auth.User.class);
        when(mockSolution.author()).thenReturn(mockUser);
        when(mockUser.name()).thenReturn("Author");
        when(mockUser.avatar()).thenReturn("avatar");
        when(mockUser.reputation()).thenReturn(100);
        when(mockSolution.created()).thenReturn(java.time.LocalDateTime.now());

        CreateSolutionRequest request = new CreateSolutionRequest("user_1", "content", "code");
        when(service.submit(eq("snippet_1"), any(CreateSolutionRequest.class))).thenReturn(mockSolution);

        ResponseEntity<?> response = snippetSolutionController.store("snippet_1", request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(service).submit("snippet_1", request);
    }

    @Test
    void acceptReturnsOkStatus() {
        com.example.api.domains.snippets.domain.Solution mockSolution = mock(com.example.api.domains.snippets.domain.Solution.class);
        com.example.api.domains.auth.User mockUser = mock(com.example.api.domains.auth.User.class);
        when(mockSolution.author()).thenReturn(mockUser);
        when(mockUser.name()).thenReturn("Author");
        when(mockUser.avatar()).thenReturn("avatar");
        when(mockUser.reputation()).thenReturn(100);
        when(mockSolution.created()).thenReturn(java.time.LocalDateTime.now());

        when(service.accept("solution_1", "user_1")).thenReturn(mockSolution);

        ResponseEntity<?> response = acceptedSolutionController.store("solution_1", "user_1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(service).accept("solution_1", "user_1");
    }

    @Test
    void storeReturnsBadRequestWhenSnippetIsMissing() {
        CreateSolutionRequest request = new CreateSolutionRequest("user_1", "content", "code");
        when(service.submit("snippet_missing", request))
            .thenThrow(new IllegalArgumentException("Snippet not found with id: snippet_missing"));

        ResponseEntity<?> response = snippetSolutionController.store("snippet_missing", request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(SnippetSolutionController.ErrorResponse.class, response.getBody());
    }

    @Test
    void acceptReturnsBadRequestWhenSolutionIsMissing() {
        when(service.accept("solution_missing", "user_1"))
            .thenThrow(new IllegalArgumentException("Solution not found with id: solution_missing"));

        ResponseEntity<?> response = acceptedSolutionController.store("solution_missing", "user_1");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(AcceptedSolutionController.ErrorResponse.class, response.getBody());
    }

    @Test
    void acceptReturnsBadRequestWhenViewerIsNotSnippetAuthor() {
        when(service.accept("solution_1", "user_2"))
            .thenThrow(new IllegalArgumentException("Only the author of the snippet can accept this solution"));

        ResponseEntity<?> response = acceptedSolutionController.store("solution_1", "user_2");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(AcceptedSolutionController.ErrorResponse.class, response.getBody());
    }

    @Test
    void acceptReturnsConflictWhenSnippetAlreadyHasAcceptedSolution() {
        when(service.accept("solution_1", "user_1"))
            .thenThrow(new IllegalStateException("A solution has already been accepted for this snippet"));

        ResponseEntity<?> response = acceptedSolutionController.store("solution_1", "user_1");

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertInstanceOf(AcceptedSolutionController.ErrorResponse.class, response.getBody());
    }
}
