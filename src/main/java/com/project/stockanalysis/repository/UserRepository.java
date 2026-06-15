package com.project.stockanalysis.repository;

import com.project.stockanalysis.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity providing database access operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /** Find a user by their username (used by Spring Security). */
    Optional<User> findByUsername(String username);

    /** Find a user by their email address. */
    Optional<User> findByEmail(String email);

    /** Check if a username is already taken. */
    boolean existsByUsername(String username);

    /** Check if an email is already registered. */
    boolean existsByEmail(String email);
}
