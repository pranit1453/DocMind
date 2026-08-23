package com.pranit.docmind.document.specification;

import com.pranit.docmind.entities.entity.Document;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DocumentSpecification {

    private DocumentSpecification() {
    }

    public static Specification<Document> searchKeyword(final String keyword, final UUID userId) {
        return ((root, query, cb) -> {
            final List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("userId"), userId));
            if (keyword != null && !keyword.trim().isEmpty()) {
                final String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("fileName")), likePattern));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }
}
