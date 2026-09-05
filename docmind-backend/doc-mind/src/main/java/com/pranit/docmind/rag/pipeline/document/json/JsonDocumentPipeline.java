package com.pranit.docmind.rag.pipeline.document.json;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.orchestrator.AbstractDocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.chunker.DocumentChunker;
import com.pranit.docmind.rag.pipeline.etl.loader.DocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.parser.json.JsonDocumentExtractor;
import org.springframework.stereotype.Component;

@Component
public class JsonDocumentPipeline extends AbstractDocumentPipeline {

    public JsonDocumentPipeline(
            final JsonDocumentExtractor extractor,
            final DocumentChunker chunker,
            final DocumentLoader loader) {
        super(extractor, chunker, loader);
    }

    @Override
    public DocumentType getFileType() {
        return DocumentType.JSON;
    }
}
