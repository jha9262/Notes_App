package com.example.notes.controller;

import com.example.notes.entity.Note;
import com.example.notes.service.NotesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8000", "*"})
public class NotesController {
    
    @Autowired
    private NotesService notesService;
    
    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return notesService.getAllNotes();
    }
    
    @PostMapping("/notes")
    public Note createNote(@RequestBody Note note) {

        return notesService.createNote(note);
    }
    
    @GetMapping("/notes/{id}")
    public ResponseEntity<Note> getNote(@PathVariable String id) {
        return notesService.getNoteById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/notes/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable String id, @RequestBody Note noteDetails) {
        return notesService.updateNote(id, noteDetails)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/notes/{id}")
    public ResponseEntity<Map<String, String>> deleteNote(@PathVariable String id) {
        if (notesService.deleteNote(id)) {
            return ResponseEntity.ok(Map.of("message", "Note deleted"));
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/notes/{id}/share")
    public ResponseEntity<Map<String, Object>> shareNote(@PathVariable String id) {
        return notesService.shareNote(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/shared/{shareToken}")
    public ResponseEntity<Note> getSharedNote(@PathVariable String shareToken) {
        return notesService.getSharedNote(shareToken)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}