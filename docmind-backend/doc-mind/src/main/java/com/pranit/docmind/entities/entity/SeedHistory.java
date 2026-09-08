package com.pranit.docmind.entities.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "seed_history", schema = "seed")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class SeedHistory {

    @Id
    @Column(name = "seed_name", nullable = false)
    private String seedName;

    @Column(name = "seeded_at", nullable = false)
    private Instant seededAt;

    @Version
    @Builder.Default
    private Long version = 0L;
}
