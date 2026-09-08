package com.pranit.docmind.document.repository;

import com.pranit.docmind.entities.entity.SeedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeedHistoryRepository extends JpaRepository<SeedHistory, String> {

    boolean existsBySeedName(String seedName);
}

