package com.example.api.domains.auth;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(length = 50)
    private String id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String handle;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(columnDefinition = "TEXT")
    private String avatar;

    @Column(nullable = false)
    private int reputation = 0;

    @Column(length = 20)
    private String role; // 'frontend', 'backend', 'fullstack', 'devops', ''

    @Column(length = 50)
    private String level;

    // RULE: One English Word
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created;

    // RULE: One English Word
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated;

    @PrePersist
    protected void initialize() {
        created = LocalDateTime.now();
        updated = LocalDateTime.now();
    }

    @PreUpdate
    protected void refresh() {
        updated = LocalDateTime.now();
    }

    public User() {
    }

    public User(String id, String name, String handle, String email, String password, String avatar, int reputation, String role, String level) {
        this.id = id;
        this.name = name;
        this.handle = handle;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.reputation = reputation;
        this.role = role;
        this.level = level;
    }

    // --------------------------------------------------------
    // THE AFFORDANCES (Replacing passive setters for business logic)
    // --------------------------------------------------------

    public void praise(int amount) {
        this.reputation += amount;
    }

    public void penalize(int amount) {
        this.reputation -= amount;
        if (this.reputation < 0) this.reputation = 0;
    }

    public void promote(String rank) {
        this.level = rank;
    }

    public void reassign(String job) {
        this.role = job;
    }

    public void update(String name, String handle, String avatar) {
        if (name != null) {
            this.name = name;
        }
        if (handle != null) {
            this.handle = handle;
        }
        if (avatar != null) {
            this.avatar = avatar;
        }
    }

    public String id() { return id; }
    public String name() { return name; }
    public String handle() { return handle; }
    public String email() { return email; }
    public String password() { return password; }
    public String avatar() { return avatar; }
    public int reputation() { return reputation; }
    public String role() { return role; }
    public String level() { return level; }
    public LocalDateTime created() { return created; }
    public LocalDateTime updated() { return updated; }
}
