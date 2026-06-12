package com.example.api.domains.snippets;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    // RULE: One English Word
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created;

    @PrePersist
    protected void initialize() {
        created = LocalDateTime.now();
    }

    public Tag() {}

    public Tag(String name) {
        this.name = name;
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public LocalDateTime created() {
        return created;
    }
}
