package com.pranit.docmind.rag.pipeline.document;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.entities.entity.DocumentMetadata;
import org.springframework.core.io.Resource;

public interface DocumentPipeline {

    DocumentType getFileType();

    long process(DocumentMetadata metadata, Resource resource);
}
