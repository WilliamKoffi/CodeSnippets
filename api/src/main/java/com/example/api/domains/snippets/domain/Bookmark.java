package com.example.api.domains.snippets.domain;

import com.example.api.domains.auth.User;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(
    name = "snippet_bookmarks",
    uniqueConstraints = @UniqueConstraint(columnNames = {"snippet_id", "user_id"})
)
public class Bookmark {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "snippet_id", nullable = false)
    private Snippet snippet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void initialize() {
        if (id == null || id.isEmpty()) {
            id = "bookmark_" + UUID.randomUUID().toString().replace("-", "");
        }
    }

    protected Bookmark() {}

    public Bookmark(Snippet snippet, User user) {
        this.snippet = snippet;
        this.user = user;
    }

    public String id() { return id; }
    public Snippet snippet() { return snippet; }
    public User user() { return user; }
}
