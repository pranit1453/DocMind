package com.pranit.docmind.rag.pipeline.etl;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;

import java.util.List;

@FunctionalInterface
public interface Extractor {

    List<Document> extract(Resource resource);
}
