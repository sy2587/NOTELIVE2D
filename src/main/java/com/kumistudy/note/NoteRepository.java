package com.kumistudy.note;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(Long ownerId);
    Optional<Note> findByIdAndOwnerIdAndDeletedAtIsNull(Long id, Long ownerId);
}
