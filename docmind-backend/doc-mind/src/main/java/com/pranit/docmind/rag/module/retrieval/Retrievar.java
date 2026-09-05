package com.pranit.docmind.rag.module.retrieval;

import com.pranit.docmind.ai.dto.RetrievalOptions;
import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.UUID;

public interface Retrievar {

    DocumentRetriever vectorStoreRetriever(UUID documentId, RetrievalOptions options);

    DocumentJoiner joinStoreRetriever();
}
