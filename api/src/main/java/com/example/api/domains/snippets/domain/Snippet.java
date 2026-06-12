package com.example.api.domains.snippets.domain;

import com.example.api.domains.auth.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;


@Entity
@Table(name = "snippets")
public class Snippet {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false, length = 50)
    private String language;

    @Column(nullable = false, length = 10)
    private String type; // 'bug' or 'snippet'

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "likes_count", nullable = false)
    private int likes = 0;

    @Column(name = "solutions_count", nullable = false)
    private int answers = 0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "snippet_tags",
        joinColumns = @JoinColumn(name = "snippet_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "snippet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("votes DESC, created ASC")
    private List<Solution> solutions = new ArrayList<>();


    // RULE: One English Word
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created;

    // RULE: One English Word
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated;

    @PrePersist
    protected void initialize() {
        if (id == null || id.isEmpty()) {
            id = "snippet_" + UUID.randomUUID().toString().replace("-", "");
        }
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void refresh() {
        updated = LocalDateTime.now();
    }

    public Snippet() {}

    public Snippet(String id, String title, String description, String code, String language, String type, User author) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.language = language;
        this.type = type;
        this.author = author;
    }

    // Affordances
    public void like() {
        this.likes++;
    }

    public void unlike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public void solve(Solution solution) {
        Objects.requireNonNull(solution, "solution must not be null");

        String solutionSnippetId = solution.snippet() != null ? solution.snippet().id() : null;
        if (solutionSnippetId != null) {
            if (!solutionSnippetId.equals(this.id)) {
                throw new IllegalArgumentException("Solution already belongs to another snippet");
            }
        } else {
            if (solution.snippet() != null && solution.snippet() != this) {
                throw new IllegalArgumentException("Solution already belongs to another snippet");
            }
        }

        if (this.solutions.contains(solution)) {
            return;
        }

        solution.attach(this);
        this.solutions.add(solution);
        this.answers++;
    }

    public boolean accept(Solution solution, String viewer) {
        Objects.requireNonNull(solution, "solution must not be null");

        String solutionSnippetId = solution.snippet() != null ? solution.snippet().id() : null;
        if (solutionSnippetId == null) {
            if (solution.snippet() != this) {
                throw new IllegalArgumentException("Solution does not belong to this snippet");
            }
        } else {
            if (!solutionSnippetId.equals(this.id)) {
                throw new IllegalArgumentException("Solution does not belong to this snippet");
            }
        }

        if (!author.id().equals(viewer)) {
            throw new IllegalArgumentException("Only the author of the snippet can accept this solution");
        }

        if (solution.accepted()) {
            return false;
        }

        boolean accepted = solutions.stream()
            .anyMatch(candidate -> {
                if (candidate == solution) {
                    return false;
                }
                String candidateId = candidate.id();
                String solutionId = solution.id();
                if (candidateId != null && solutionId != null) {
                    if (candidateId.equals(solutionId)) {
                        return false;
                    }
                }
                return candidate.accepted();
            });
        if (accepted) {
            throw new IllegalStateException("A solution has already been accepted for this snippet");
        }

        solution.accept();
        return true;
    }

    public void tag(Set<Tag> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    // Getters
    public String id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public String code() { return code; }
    public String language() { return language; }
    public String type() { return type; }
    public User author() { return author; }
    public int likes() { return likes; }
    public int answers() { return answers; }
    public Set<Tag> tags() { return tags; }
    public List<Solution> solutions() { return solutions; }
    public LocalDateTime created() { return created; }
    public LocalDateTime updated() { return updated; }
}
