package com.pranit.docmind.authorization.repository;

import com.pranit.docmind.entities.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {
    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleId(Long roleId);

    Set<Role> findAllByRoleNameIn(Set<String> roleNames);

    boolean existsByRoleId(Long aLong);

    @Query("""
                SELECT r
                FROM Role r
                WHERE (:keyword IS NULL OR LOWER(r.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')))
            """)
    Page<Role> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
