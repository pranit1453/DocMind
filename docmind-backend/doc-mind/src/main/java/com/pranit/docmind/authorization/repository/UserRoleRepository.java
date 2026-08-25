package com.pranit.docmind.authorization.repository;

import com.pranit.docmind.entities.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    boolean existsByRole_RoleId(Long roleId);
//
//    @Query("""
//            SELECT ur
//            FROM UserRole ur
//            JOIN FETCH ur.role r
//            WHERE ur.user.userId IN :userIds
//            """)
//    List<UserRole> findRolesByUserIds(@Param("userIds") List<UUID> userIds);
//
//    @Query("""
//                SELECT new com.pranit.rag.authorization.UserRoleProjection(
//                     u.userId,
//                     u.username,
//                     r.roleId,
//                     r.roleName,
//                     r.roleDescription,
//                     ur.status
//                 )
//                 FROM UserRole ur
//                 JOIN ur.user u
//                 JOIN ur.role r
//                 WHERE u.userId = (
//                     SELECT ur2.user.userId
//                     FROM UserRole ur2
//                     WHERE ur2.userRoleId = :userRoleId
//                 )
//            """)
//    List<UserRoleProjection> findAllRolesByUserRoleId(@Param("userRoleId") Long userRoleId);
//
//    @Query("""
//                SELECT new com.pranit.rag.authorization.UserRoleProjection(
//                    u.userId,
//                    u.username,
//                    r.roleId,
//                    r.roleName,
//                    r.roleDescription,
//                    ur.status
//                )
//                FROM UserRole ur
//                JOIN ur.user u
//                JOIN ur.role r
//                WHERE (
//                    :keyword IS NULL OR
//                    LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
//                    OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%'))
//                )
//            """)
//    Page<UserRoleProjection> findAllUsersWithRoles(@Param("keyword") String keyword, Pageable pageable);

}
