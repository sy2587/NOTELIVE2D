package com.kumistudy.tag;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findAllByOwnerIdOrderByNameAsc(Long ownerId);
    List<Tag> findAllByIdInAndOwnerId(Collection<Long> ids, Long ownerId);
    Optional<Tag> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

    @Modifying
    @Query(value = "DELETE FROM note_tags WHERE tag_id = :tagId", nativeQuery = true)
    void deleteNoteLinks(@Param("tagId") Long tagId);
}
