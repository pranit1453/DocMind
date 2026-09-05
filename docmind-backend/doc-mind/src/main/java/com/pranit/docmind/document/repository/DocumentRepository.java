package com.pranit.docmind.document.repository;

import com.pranit.docmind.entities.constant.FileStatus;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentMetadata, UUID>, JpaSpecificationExecutor<DocumentMetadata> {

    @Modifying
    @Query("""
                update DocumentMetadata dm
                set dm.fileStatus = :status
                where dm.documentId = :documentId
            """)
    void updateFileStatus(@Param("documentId") UUID documentId, @Param("status") FileStatus status);

    boolean existsByDocumentId(UUID documentId);

    boolean existsByFileNameAndUser_UserId(String originalFilename, UUID userId);

    Optional<DocumentMetadata> findByDocumentIdAndUser_UserId(UUID documentId, UUID userId);

}
