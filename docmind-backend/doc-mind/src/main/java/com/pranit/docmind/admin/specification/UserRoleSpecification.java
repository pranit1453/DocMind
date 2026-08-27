package com.pranit.docmind.admin.specification;

import com.pranit.docmind.constant.UserMetadata;
import com.pranit.docmind.entities.entity.UserRole;
import org.springframework.data.jpa.domain.Specification;

public final class UserRoleSpecification {

    private UserRoleSpecification() {
    }

    public static Specification<UserRole> searchKeyword(final String keyword) {
        return ((root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return cb.conjunction();
            final String likePattern = "%" + keyword.trim().toLowerCase() + "%";
            return cb.like(cb.lower(root.get(UserMetadata.USER).get(UserMetadata.USERNAME)), likePattern);
        });
    }
}
