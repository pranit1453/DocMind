package com.pranit.docmind.rag.module.generation;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryAugmenter {

    private static final PromptTemplate RAG_PROMPT_TEMPLATE = PromptTemplate.builder().template("""
            You are a document question-answering assistant.
            
            Use the retrieved context below to answer the user's question.
            
            ---------------------
            Retrieved Context:
            {context}
            ---------------------
            
            Rules:
            1. Answer the question using the retrieved context.
            2. Do not use outside knowledge when answering questions
               about the provided documents.
            3. If the answer cannot be found in the retrieved context,
               say that you don't know.
            4. Do not invent, assume, or infer unsupported facts.
            5. Answer clearly and directly.
            
            User Question:
            {query}
            
            Answer:
            """).build();

    private static final PromptTemplate EMPTY_CONTEXT_PROMPT = PromptTemplate.builder().template("""
            The user's question could not be answered from the
            available document context.
            
            Politely tell the user that you don't have enough
            information in the provided documents to answer the question.
            
            Do not invent an answer.
            """).build();

    private final ContextualQueryAugmenter augmenter;

    public QueryAugmenter(ContextualQueryAugmenter augmenter) {
        this.augmenter = ContextualQueryAugmenter.builder()
                .promptTemplate(RAG_PROMPT_TEMPLATE)
                .emptyContextPromptTemplate(EMPTY_CONTEXT_PROMPT)
                .allowEmptyContext(false)
                .build();
    }

    public String augment(final String query, final List<Document> documents) {
        return augmenter.augment(new Query(query), documents).text();
    }
}
