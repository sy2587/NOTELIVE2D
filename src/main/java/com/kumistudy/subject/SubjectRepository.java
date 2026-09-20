package com.kumistudy.subject;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {

    List<Subject> findAllByOwnerIdAndDeletedAtIsNullOrderByUpdatedAtDesc(Long ownerId);

    Optional<Subject> findByIdAndOwnerIdAndDeletedAtIsNull(Long id, Long ownerId);

    boolean existsByOwnerIdAndNameIgnoreCaseAndDeletedAtIsNull(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNotAndDeletedAtIsNull(Long ownerId, String name, Long id);
}
