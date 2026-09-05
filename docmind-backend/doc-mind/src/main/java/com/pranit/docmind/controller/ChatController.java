package com.pranit.docmind.controller;

import com.pranit.docmind.ai.dto.QueryRequest;
import com.pranit.docmind.ai.dto.QueryResponse;
import com.pranit.docmind.ai.exception.AiServiceUnavailableException;
import com.pranit.docmind.ai.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat/documents")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "Chat Management",
        description = "Endpoints for querying documents using AI-powered chat, including standard and streaming responses."
)
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "Ask a question about a document", description = "Sends a question to the AI assistant and returns a response based on the specified document and conversation context.")
    @PostMapping(value = "/{documentId}/query", version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<QueryResponse> getResponse(
            @Valid @RequestBody QueryRequest request, @PathVariable UUID documentId,
            @RequestHeader("X-Conversation-ID") UUID conversationId) {
        final QueryResponse response = chatService.getResponseFromAssistant(request.provider(), request.query(), conversationId, documentId, request.options());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "Stream a response about a document", description = "Sends a question to the AI assistant and streams the generated response incrementally using Server-Sent Events (SSE).")
    @PostMapping(value = "/{documentId}/query/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE, version = "v1")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Flux<ServerSentEvent<String>> streamChat(
            @Valid @RequestBody QueryRequest request, @PathVariable UUID documentId,
            @RequestHeader("X-Conversation-ID") UUID conversationId) {
        return chatService.getStreamResponseFromAssistant(request.provider(), request.query(), conversationId, documentId, request.options())
                .map(chunk -> ServerSentEvent.<String>builder()
                        .event("message")
                        .data(chunk)
                        .build())
                .onErrorResume(AiServiceUnavailableException.class, ex ->
                        Flux.just(ServerSentEvent.<String>builder()
                                .event("error")
                                .data(ex.getMessage())
                                .build()));
    }

}
