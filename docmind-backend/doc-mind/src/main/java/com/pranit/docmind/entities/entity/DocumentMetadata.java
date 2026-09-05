package com.pranit.docmind.entities.entity;

import com.pranit.docmind.audit.entity.AuditEntity;
import com.pranit.docmind.entities.constant.FileStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "document_metadata",
        schema = "doc",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_file_name", columnNames = "file_name"),
        },
        indexes = {
                @Index(name = "idx_document_file_status", columnList = "file_status")
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class DocumentMetadata extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(name = "file_status", nullable = false)
    private FileStatus fileStatus;

    @Column(name = "chunks_created", nullable = false)
    @Builder.Default
    private long chunksCreated = 0L;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Version
    @Builder.Default
    private Long version = 0L;
}