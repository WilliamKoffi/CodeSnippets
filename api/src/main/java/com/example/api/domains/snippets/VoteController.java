package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import com.example.api.domains.auth.repositories.UserRepository;
import com.example.api.domains.snippets.domain.Solution;
import com.example.api.domains.snippets.domain.Vote;
import com.example.api.domains.snippets.repositories.SolutionRepository;
import com.example.api.domains.snippets.repositories.VoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class VoteController {

    private final VoteRepository votes;
    private final SolutionRepository solutions;
    private final UserRepository users;

    public VoteController(VoteRepository votes, SolutionRepository solutions, UserRepository users) {
        this.votes = votes;
        this.solutions = solutions;
        this.users = users;
    }

    @PostMapping("/solutions/{id}/votes")
    @Transactional
    public ResponseEntity<?> store(
            @PathVariable String id,
            @RequestParam String userId,
            @RequestParam String direction) {

        if (!"up".equals(direction) && !"down".equals(direction)) {
            return ResponseEntity.badRequest().body("Direction must be 'up' or 'down'");
        }

        Solution solution = solutions.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Solution not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<Vote> existing = votes.findBySolutionIdAndUserId(id, userId);
        if (existing.isPresent()) {
            Vote vote = existing.get();
            if (!vote.direction().equals(direction)) {
                // Direction changed, adjust solution votes counter
                if ("up".equals(direction)) {
                    solution.upvote(); // change from down to up: +2 (downvote was -1, upvote is +1)
                    solution.upvote();
                } else {
                    solution.downvote(); // change from up to down: -2 (upvote was +1, downvote is -1)
                    solution.downvote();
                }
                vote.updateDirection(direction);
                votes.save(vote);
                solutions.save(solution);
            }
        } else {
            // New vote
            Vote vote = new Vote(solution, user, direction);
            votes.save(vote);
            if ("up".equals(direction)) {
                solution.upvote();
            } else {
                solution.downvote();
            }
            solutions.save(solution);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/solutions/{id}/votes")
    @Transactional
    public ResponseEntity<?> destroy(@PathVariable String id, @RequestParam String userId) {
        Optional<Vote> existing = votes.findBySolutionIdAndUserId(id, userId);
        if (existing.isPresent()) {
            Vote vote = existing.get();
            Solution solution = solutions.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Solution not found"));

            if ("up".equals(vote.direction())) {
                solution.downvote();
            } else {
                solution.upvote();
            }
            votes.delete(vote);
            solutions.save(solution);
        }

        return ResponseEntity.ok().build();
    }
}
