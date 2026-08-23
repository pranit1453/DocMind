package com.pranit.docmind.document.service.impl;

import com.pranit.docmind.document.repository.DocumentRepository;
import com.pranit.docmind.document.service.DocumentStatusService;
import com.pranit.docmind.entities.constant.FileStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentStatusServiceImpl implements DocumentStatusService {

    private final DocumentRepository documentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void markProcessing(final UUID documentId) {
        documentRepository.updateFileStatus(documentId, FileStatus.PROCESSING);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void markFailed(final UUID documentId) {
        documentRepository.updateFileStatus(documentId, FileStatus.FAILED);
    }
}
