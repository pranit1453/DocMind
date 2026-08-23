package com.pranit.docmind.rag.pipeline.etl.json;

import com.pranit.docmind.rag.pipeline.etl.Extractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class JsonDocumentExtractor implements Extractor {

    @Override
    public List<Document> extract(final Resource resource) {
        final var jsonReader = new JsonReader(resource);
        return jsonReader.read();
    }
}
