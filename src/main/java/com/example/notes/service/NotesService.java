package com.example.notes.service;

import com.example.notes.entity.Note;
import com.example.notes.entity.SharedNote;
import com.example.notes.repository.NoteRepository;
import com.example.notes.repository.SharedNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotesService {
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private SharedNoteRepository sharedNoteRepository;
    
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }
    
    public Note createNote(Note note) {
        return noteRepository.save(note);
    }
    
    public Optional<Note> getNoteById(String id) {
        return noteRepository.findById(id);
    }
    
    public Optional<Note> updateNote(String id, Note noteDetails) {
        return noteRepository.findById(id)
            .map(note -> {
                note.setTitle(noteDetails.getTitle());
                note.setContent(noteDetails.getContent());
                return noteRepository.save(note);
            });
    }
    
    public boolean deleteNote(String id) {
        return noteRepository.findById(id)
            .map(note -> {
                noteRepository.delete(note);
                return true;
            })
            .orElse(false);
    }
    
    public Optional<Map<String, Object>> shareNote(String id) {
        return noteRepository.findById(id)
            .map(note -> {
                SharedNote sharedNote = new SharedNote();
                sharedNote.setNoteId(id);
                sharedNote.setShareToken(UUID.randomUUID().toString());
                sharedNote.setExpiresAt(LocalDateTime.now().plusDays(7));
                
                SharedNote saved = sharedNoteRepository.save(sharedNote);
                
                return Map.of(
                    "share_url", "http://localhost:3000/shared/" + saved.getShareToken(),
                    "expires_at", saved.getExpiresAt()
                );
            });
    }
    
    public Optional<Note> getSharedNote(String shareToken) {
        return sharedNoteRepository.findByShareToken(shareToken)
            .filter(shared -> LocalDateTime.now().isBefore(shared.getExpiresAt()))
            .flatMap(shared -> noteRepository.findById(shared.getNoteId()));
    }
}