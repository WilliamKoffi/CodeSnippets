package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "solutions")
public class Solution {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    private Snippet snippet;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false)
    private int votes = 0;

    @Column(nullable = false)
    private boolean accepted = false;

    // RULE: One English Word
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created;

    // RULE: One English Word
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated;

    @PrePersist
    protected void initialize() {
        if (id == null || id.isEmpty()) {
            id = "solution_" + UUID.randomUUID().toString().replace("-", "");
        }
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void refresh() {
        updated = LocalDateTime.now();
    }

    public Solution() {}

    public Solution(String id, Snippet snippet, User author, String content, String code, int votes, boolean accepted) {
        this.id = id;
        this.snippet = snippet;
        this.author = author;
        this.content = content;
        this.code = code;
        this.votes = votes;
        this.accepted = accepted;
    }

    // Affordances
    public void voteUp() {
        this.votes++;
    }

    public void voteDown() {
        this.votes--;
    }

    public void accept() {
        this.accepted = true;
    }

    // Getters
    public String id() { return id; }
    public Snippet snippet() { return snippet; }
    public User author() { return author; }
    public String content() { return content; }
    public String code() { return code; }
    public int votes() { return votes; }
    public boolean accepted() { return accepted; }
    public LocalDateTime created() { return created; }
    public LocalDateTime updated() { return updated; }
}
