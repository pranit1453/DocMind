package com.pranit.docmind.rag.module.retrieval;

import org.springframework.ai.rag.retrieval.join.DocumentJoiner;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;

import java.util.UUID;

public interface Retrievar {

    DocumentRetriever vectorStoreRetriever(UUID documentId);

    DocumentJoiner joinStoreRetriever();
}
