package com.kumistudy.note;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(Long ownerId);
    List<Note> findTop5ByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long ownerId);
    long countByOwnerIdAndDeletedAtIsNull(Long ownerId);
    Optional<Note> findByIdAndOwnerIdAndDeletedAtIsNull(Long id, Long ownerId);

    @Modifying
    @Query("update Note n set n.folder = null where n.owner.id = :ownerId and n.folder.id = :folderId")
    void clearFolder(@Param("ownerId") Long ownerId, @Param("folderId") Long folderId);
}
