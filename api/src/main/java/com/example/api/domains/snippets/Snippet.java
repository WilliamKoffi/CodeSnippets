package com.example.api.domains.snippets;

import com.example.api.domains.auth.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;


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
    private int likesCount = 0;

    @Column(name = "solutions_count", nullable = false)
    private int solutionsCount = 0;

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
    public void incrementLikes() {
        this.likesCount++;
    }

    public void decrementLikes() {
        if (this.likesCount > 0) this.likesCount--;
    }

    public void incrementSolutions() {
        this.solutionsCount++;
    }

    public void updateTags(Set<Tag> tags) {
        this.tags = tags;
    }

    // Getters
    public String id() { return id; }
    public String title() { return title; }
    public String description() { return description; }
    public String code() { return code; }
    public String language() { return language; }
    public String type() { return type; }
    public User author() { return author; }
    public int likesCount() { return likesCount; }
    public int solutionsCount() { return solutionsCount; }
    public Set<Tag> tags() { return tags; }
    public List<Solution> solutions() { return solutions; }
    public LocalDateTime created() { return created; }
    public LocalDateTime updated() { return updated; }
}
