package com.pranit.docmind.rag.module.postretrieval;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessorImpl implements Processor {

    @Override
    public DocumentPostProcessor reRankProcessor() {
        return null;
    }

}
