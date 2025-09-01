package com.example.notes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SharedNoteRepository extends JpaRepository<SharedNote, String> {
    Optional<SharedNote> findByShareToken(String shareToken);
}