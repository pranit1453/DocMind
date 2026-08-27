package com.pranit.docmind.rag.pipeline.etl.json;

import com.pranit.docmind.properties.RagProperties;
import com.pranit.docmind.rag.pipeline.etl.Transformer;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public final class JsonDocumentTransformer implements Transformer {

    private final RagProperties properties;

    @Override
    public List<Document> transform(final List<Document> documents) {
        final var config = properties.chunking();
        final var splitter = TokenTextSplitter.builder()
                .withChunkSize(config.chunkSize())
                .withMinChunkSizeChars(config.minChunkSizeChars())
                .withMinChunkLengthToEmbed(config.minChunkLengthToEmbed())
                .withMaxNumChunks(config.maxNumChunks())
                .withKeepSeparator(config.keepSeparator())
                .build();
        return splitter.transform(documents);
    }
}
