package com.example.api.domains.snippets.domain;

import com.example.api.domains.auth.User;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "solution_votes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"solution_id", "user_id"})
)
public class Vote {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solution_id", nullable = false)
    private Solution solution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 10)
    private String direction; // "up" or "down"

    @PrePersist
    protected void initialize() {
        if (id == null || id.isEmpty()) {
            id = "vote_" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    protected Vote() {}

    public Vote(Solution solution, User user, String direction) {
        this.solution = solution;
        this.user = user;
        this.direction = direction;
    }

    public void updateDirection(String direction) {
        this.direction = direction;
    }

    public String id() { return id; }
    public Solution solution() { return solution; }
    public User user() { return user; }
    public String direction() { return direction; }
}
