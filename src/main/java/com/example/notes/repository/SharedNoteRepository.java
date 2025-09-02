package com.example.notes.repository;

import com.example.notes.entity.SharedNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SharedNoteRepository extends JpaRepository<SharedNote, String> {
    Optional<SharedNote> findByShareToken(String shareToken);
}