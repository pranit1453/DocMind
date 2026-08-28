package com.pranit.docmind.authentication.repository;

import com.pranit.docmind.entities.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUserId(UUID userId);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("""
                SELECT u.userId
                FROM User u
                WHERE u.deleted = true
                  AND u.scheduledDeletionAt IS NOT NULL
                  AND u.scheduledDeletionAt <= :now
            """)
    List<UUID> findAllIdsScheduledForPermanentDeletion(@Param("now") Instant now);

    @Modifying
    @Query("""
                DELETE FROM User u
                WHERE u.userId IN :userIds
            """)
    int deleteAllByUserIds(@Param("userIds") List<UUID> userIds);

    Optional<User> findByEmail(String email);

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    @Query("""
                SELECT DISTINCT u
                FROM User u
                LEFT JOIN u.userRoles ur
                LEFT JOIN ur.role r
                WHERE (
                    :keyword IS NULL
                    OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                )
            """)
    Page<User> findAllUsers(@Param("keyword") String keyword, Pageable pageable);
}
