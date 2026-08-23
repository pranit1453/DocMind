package com.pranit.docmind.rag.pipeline.factory;

import com.pranit.docmind.entities.constant.DocumentType;
import com.pranit.docmind.rag.pipeline.document.DocumentPipeline;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public final class DocumentPipelineFactory {

    private final Map<DocumentType, DocumentPipeline> pipelines;

    public DocumentPipelineFactory(List<DocumentPipeline> pipelines) {
        this.pipelines = pipelines.stream()
                .collect(Collectors.toMap(
                        DocumentPipeline::getFileType,
                        Function.identity()));
    }

    public long getPipeline(UUID documentId, Resource resource) {
        DocumentType type = getType(resource);
        DocumentPipeline pipeline = pipelines.get(type);
        if (pipeline == null) {
            throw new IllegalArgumentException("Unsupported file: " + resource.getFilename());
        }
        return pipeline.process(documentId, resource);
    }

    private DocumentType getType(Resource resource) {
        String name = Objects.requireNonNull(resource.getFilename()).toLowerCase();
        if (name.endsWith(".json")) return DocumentType.JSON;
        if (name.endsWith(".pdf")) return DocumentType.PDF;
        if (name.endsWith(".txt")) return DocumentType.TXT;

        throw new IllegalArgumentException(
                "Unsupported file: " + resource.getFilename()
        );
    }
}
