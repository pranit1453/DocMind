package com.pranit.docmind.authorization.repository;

import com.pranit.docmind.entities.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long>, JpaSpecificationExecutor<UserRole> {

    @Query(value = """
                SELECT CONCAT('ROLE_', r.role_name) AS authority
                FROM auth.user_roles ur
                JOIN auth.roles r
                    ON ur.role_id = r.role_id
                JOIN auth.users u
                    ON ur.user_id = u.user_id
                WHERE u.user_id = :userId
                  AND u.enabled = TRUE
                  AND u.deleted = FALSE
                  AND ur.status = 'ACTIVE'
            """, nativeQuery = true)
    List<String> findAuthoritiesByUserId(@Param("userId") UUID userId);

    boolean existsByUser_UserIdAndRole_RoleId(UUID userId, Long roleId);

    Optional<UserRole> findByUserRoleIdAndUser_UserIdAndRole_RoleId(Long userRoleId, UUID userId, Long roleId);

    @Query("""
            SELECT ur
            FROM UserRole ur
            JOIN FETCH ur.user u
            JOIN FETCH ur.role r
            WHERE ur.userRoleId = :userRoleId
            """)
    Optional<UserRole> findByUserRoleId(@Param("userRoleId") Long userRoleId);

    @EntityGraph(attributePaths = {"user", "role"})
    @Query("""
            SELECT ur
            FROM UserRole ur
            WHERE (
                    :keyword IS NULL
                    OR LOWER(ur.user.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(ur.role.roleName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<UserRole> findAllUserRoles(@Param("keyword") String keyword, Pageable pageable);
}
