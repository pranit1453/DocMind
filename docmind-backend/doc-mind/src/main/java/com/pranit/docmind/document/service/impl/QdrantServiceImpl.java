package com.pranit.docmind.document.service.impl;

import com.pranit.docmind.constant.DocMetadata;
import com.pranit.docmind.document.service.QdrantService;
import io.qdrant.client.QdrantClient;
import io.qdrant.client.grpc.Common.Condition;
import io.qdrant.client.grpc.Common.FieldCondition;
import io.qdrant.client.grpc.Common.Filter;
import io.qdrant.client.grpc.Common.Match;
import io.qdrant.client.grpc.Points.DeletePoints;
import io.qdrant.client.grpc.Points.PointsSelector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class QdrantServiceImpl implements QdrantService {

    private final QdrantClient qdrantClient;
    private final String collectionName;

    public QdrantServiceImpl(
            QdrantClient qdrantClient,
            @Value("${spring.ai.vectorstore.qdrant.collection-name}") String collectionName) {
        this.qdrantClient = qdrantClient;
        this.collectionName = collectionName;
    }

    @Override
    public void deleteByDocumentId(final UUID documentId) {
        Filter filter = Filter.newBuilder()
                .addMust(Condition.newBuilder()
                        .setField(FieldCondition.newBuilder()
                                .setKey(DocMetadata.DOCUMENT_ID)
                                .setMatch(Match.newBuilder()
                                        .setKeyword(documentId.toString())
                                        .build())
                                .build())
                        .build())
                .build();

        qdrantClient.deleteAsync(DeletePoints.newBuilder()
                .setCollectionName(collectionName)
                .setPoints(PointsSelector.newBuilder()
                        .setFilter(filter)
                        .build())
                .build());
    }
}
