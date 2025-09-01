package com.example.notes;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// This is our main Note class - basically represents a single note in the database
// I'm using JPA annotations here because they make database stuff way easier
@Entity
@Table(name = "notes")
public class Note {
    // Auto-generated ID - Spring will handle this for us
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    // Title is required - can't be null
    @Column(nullable = false)
    private String title;
    
    // Using TEXT type for longer content - learned this from Stack Overflow
    @Column(columnDefinition = "TEXT")
    private String content;
    
    // Timestamps - these will track when notes are created/modified
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // This runs automatically when we save a new note
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
    }
    
    // This runs automatically when we update an existing note
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Standard getters and setters - IDE generated these for me
    // Probably could use Lombok to make this cleaner but keeping it simple for now
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}