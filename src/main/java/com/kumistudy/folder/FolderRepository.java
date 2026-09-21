package com.kumistudy.folder;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findAllByOwnerIdOrderByNameAsc(Long ownerId);
    Optional<Folder> findByIdAndOwnerId(Long id, Long ownerId);
    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);
    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);
}
