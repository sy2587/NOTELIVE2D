package com.kumistudy.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    Optional<User> findByEmailAndDeletedAtIsNull(String email);

    boolean existsByUsernameAndDeletedAtIsNull(String username);

    boolean existsByEmailAndDeletedAtIsNull(String email);

    default boolean existsByUsername(String username) {
        return existsByUsernameAndDeletedAtIsNull(username);
    }

    default boolean existsByEmail(String email) {
        return existsByEmailAndDeletedAtIsNull(email);
    }
}
