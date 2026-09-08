package com.pranit.docmind.rag.pipeline.etl.chunker.impl;

import com.pranit.docmind.properties.RagProperties;
import com.pranit.docmind.rag.pipeline.etl.chunker.DocumentChunker;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public final class TokenTextDocumentChunker implements DocumentChunker {

    private final RagProperties properties;

    @Override
    public List<Document> chunk(final List<Document> documents) {
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