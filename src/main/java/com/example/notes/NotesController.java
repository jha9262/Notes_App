package com.example.notes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// Main controller for handling all note-related API requests
// @RestController makes this return JSON automatically - pretty cool!
@RestController
@RequestMapping("/api") // All endpoints start with /api
@CrossOrigin(origins = "http://localhost:3000") // Allow React app to call our API
public class NotesController {
    
    // Spring will inject these repositories for us - dependency injection is magic!
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private SharedNoteRepository sharedNoteRepository;
    
    // GET /api/notes - returns all notes as JSON
    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteRepository.findAll(); // JPA makes this so easy!
    }
    
    // POST /api/notes - creates a new note
    @PostMapping("/notes")
    public Note createNote(@RequestBody Note note) {
        return noteRepository.save(note); // Save and return the note with generated ID
    }
    
    // GET /api/notes/{id} - get a specific note by ID
    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNote(@PathVariable String id) {
        // Using Optional here - returns 404 if note doesn't exist
        return noteRepository.findById(id)
            .map(ResponseEntity::ok) // Found it! Return 200 with note
            .orElse(ResponseEntity.notFound().build()); // Not found, return 404
    }
    
    // PUT /api/notes/{id} - update an existing note
    @PutMapping("/notes/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable String id, @RequestBody Note noteDetails) {
        return noteRepository.findById(id)
            .map(note -> {
                // Update the fields we care about
                note.setTitle(noteDetails.getTitle());
                note.setContent(noteDetails.getContent());
                // @PreUpdate will automatically set updatedAt timestamp
                return ResponseEntity.ok(noteRepository.save(note));
            })
            .orElse(ResponseEntity.notFound().build()); // Note doesn't exist
    }
    
    // DELETE /api/notes/{id} - remove a note completely
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable String id) {
        return noteRepository.findById(id)
            .map(note -> {
                noteRepository.delete(note); // Bye bye note!
                return ResponseEntity.ok(Map.of("message", "Note deleted"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/notes/{id}/share - create a shareable link for a note
    @GetMapping("/notes/{id}/share")
    public ResponseEntity<Map<String, Object>> shareNote(@PathVariable String id) {
        return noteRepository.findById(id)
            .map(note -> {
                // Create a new share record
                SharedNote sharedNote = new SharedNote();
                sharedNote.setNoteId(id);
                sharedNote.setShareToken(UUID.randomUUID().toString()); // Random token for security
                sharedNote.setExpiresAt(LocalDateTime.now().plusDays(7)); // Expires in 7 days
                
                SharedNote saved = sharedNoteRepository.save(sharedNote);
                
                // Return the shareable URL - had to fix the type issue here
                Map<String, Object> response = Map.of(
                    "share_url", "http://localhost:3000/shared/" + saved.getShareToken(),
                    "expires_at", saved.getExpiresAt()
                );
                return ResponseEntity.ok(response);
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    // GET /api/shared/{token} - access a shared note via its token
    @GetMapping("/shared/{shareToken}")
    public ResponseEntity<Note> getSharedNote(@PathVariable String shareToken) {
        return sharedNoteRepository.findByShareToken(shareToken)
            .filter(shared -> LocalDateTime.now().isBefore(shared.getExpiresAt())) // Check if not expired
            .flatMap(shared -> noteRepository.findById(shared.getNoteId())) // Get the actual note
            .map(ResponseEntity::ok) // Return the note
            .orElse(ResponseEntity.notFound().build()); // Token invalid or expired
    }
}