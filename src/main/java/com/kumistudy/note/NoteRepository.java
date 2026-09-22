package com.kumistudy.note;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByOwnerIdAndDeletedAtIsNullOrderByPinnedDescUpdatedAtDesc(Long ownerId);
    List<Note> findTop5ByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long ownerId);
    long countByOwnerIdAndDeletedAtIsNull(Long ownerId);
    Optional<Note> findByIdAndOwnerIdAndDeletedAtIsNull(Long id, Long ownerId);

    @Query(value = """
            select distinct n from Note n left join n.tags t
            where n.owner.id = :ownerId and n.deletedAt is null
              and (:query is null or lower(n.title) like lower(concat('%', :query, '%'))
                   or lower(n.content) like lower(concat('%', :query, '%')))
              and (:folderId is null or (:folderId = -1 and n.folder is null) or n.folder.id = :folderId)
              and (:tagId is null or t.id = :tagId)
              and (:favorite is null or n.favorite = :favorite)
            order by n.pinned desc, n.updatedAt desc
            """,
            countQuery = """
            select count(distinct n.id) from Note n left join n.tags t
            where n.owner.id = :ownerId and n.deletedAt is null
              and (:query is null or lower(n.title) like lower(concat('%', :query, '%'))
                   or lower(n.content) like lower(concat('%', :query, '%')))
              and (:folderId is null or (:folderId = -1 and n.folder is null) or n.folder.id = :folderId)
              and (:tagId is null or t.id = :tagId)
              and (:favorite is null or n.favorite = :favorite)
            """)
    Page<Note> search(@Param("ownerId") Long ownerId, @Param("query") String query,
                      @Param("folderId") Long folderId, @Param("tagId") Long tagId,
                      @Param("favorite") Boolean favorite, Pageable pageable);

    @Modifying
    @Query("update Note n set n.folder = null where n.owner.id = :ownerId and n.folder.id = :folderId")
    void clearFolder(@Param("ownerId") Long ownerId, @Param("folderId") Long folderId);
}
