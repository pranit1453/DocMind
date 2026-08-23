package com.pranit.docmind.rag.pipeline.etl.json;

import com.pranit.docmind.rag.pipeline.etl.Transformer;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public final class JsonDocumentTransformer implements Transformer {

    @Override
    public List<Document> transform(final List<Document> documents) {
        final var splitter = TokenTextSplitter.builder()
                .withChunkSize(400)
                .withMinChunkSizeChars(175)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(5000)
                .withKeepSeparator(true)
                .build();
        return splitter.transform(documents);
    }
}
