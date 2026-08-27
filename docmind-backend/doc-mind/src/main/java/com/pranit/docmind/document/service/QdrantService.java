package com.pranit.docmind.document.service;

import java.util.UUID;

@FunctionalInterface
public interface QdrantService {

    void deleteByDocumentId(UUID documentId);
}
