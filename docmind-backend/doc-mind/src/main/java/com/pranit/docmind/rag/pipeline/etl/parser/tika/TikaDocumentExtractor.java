package com.pranit.docmind.rag.pipeline.etl.parser.tika;

import com.pranit.docmind.rag.pipeline.etl.parser.Extractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TikaDocumentExtractor implements Extractor {

    @Override
    public List<Document> extract(final Resource resource) {
        final var tikaReader = new TikaDocumentReader(resource);
        return tikaReader.read();
    }
}
