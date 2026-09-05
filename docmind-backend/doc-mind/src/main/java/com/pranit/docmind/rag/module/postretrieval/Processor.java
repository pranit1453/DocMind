package com.pranit.docmind.rag.module.postretrieval;

import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;

public interface Processor {

    DocumentPostProcessor reRankProcessor();
}
