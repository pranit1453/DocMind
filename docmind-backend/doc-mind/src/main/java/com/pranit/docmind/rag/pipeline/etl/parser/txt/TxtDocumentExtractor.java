package com.pranit.docmind.rag.pipeline.etl.parser.txt;

import com.pranit.docmind.rag.pipeline.etl.parser.Extractor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class TxtDocumentExtractor implements Extractor {

    @Override
    public List<Document> extract(final Resource resource) {
        final var txtReader = new TextReader(resource);
        return txtReader.read();
    }
}
