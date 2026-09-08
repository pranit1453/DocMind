package com.pranit.docmind.rag.pipeline.document.tika;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.orchestrator.AbstractDocumentPipeline;
import com.pranit.docmind.rag.pipeline.etl.chunker.DocumentChunker;
import com.pranit.docmind.rag.pipeline.etl.loader.DocumentLoader;
import com.pranit.docmind.rag.pipeline.etl.parser.txt.TxtDocumentExtractor;
import org.springframework.stereotype.Component;

@Component
public class TikaDocumentPipeline extends AbstractDocumentPipeline {

    public TikaDocumentPipeline(
            final TxtDocumentExtractor extractor,
            final DocumentChunker chunker,
            final DocumentLoader loader) {
        super(extractor, chunker, loader);
    }

    @Override
    public DocumentType getFileType() {
        return DocumentType.TIKA;
    }
}
