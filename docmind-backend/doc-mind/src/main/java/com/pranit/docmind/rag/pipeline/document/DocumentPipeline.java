package com.pranit.docmind.rag.pipeline.document;

import com.pranit.docmind.entities.constant.DocumentType;
import org.springframework.core.io.Resource;

import java.util.UUID;

public interface DocumentPipeline {

    DocumentType getFileType();

    long process(UUID documentId, Resource resource);
}
