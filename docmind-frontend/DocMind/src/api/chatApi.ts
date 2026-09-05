import type { Provider, RetrievalOptions, QueryResponse } from "./types";
import { API_BASE_URL, fetchWithAuth, safeJsonResponse } from "./apiClient";

/**
 * PROTECTED CHAT ASSISTANT ENDPOINT: POST /api/chat/documents/{documentId}/query
 * Headers: X-API-Version: v1, X-Conversation-ID: <UUID>
 * Payload: { query, provider: "NVIDIA", options: { topK, similarityThreshold } }
 */
export async function queryAssistantApi(
  documentId?: string,
  query?: string,
  conversationId?: string,
  provider: Provider = "NVIDIA",
  options?: RetrievalOptions
): Promise<QueryResponse> {
  const currentConversationId = conversationId || crypto.randomUUID();
  const docId = documentId || "default";

  const bodyPayload = {
    query: query || "",
    provider: provider || "NVIDIA",
    options: {
      topK: options?.topK ?? 4,
      similarityThreshold: options?.similarityThreshold ?? 0.70,
    },
  };

  const response = await fetchWithAuth(
    `${API_BASE_URL}/api/chat/documents/${docId}/query`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Conversation-ID": currentConversationId,
      },
      body: JSON.stringify(bodyPayload),
    }
  );

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || errorData.error || errorData.detail || `Chat query failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

export interface SseMessage {
  event: string;
  data: string;
  id?: string;
}

/**
 * Parses raw SSE text stream into structured Server-Sent Events.
 * Handles 'event: message' and 'event: error' events from WebFlux Flux<ServerSentEvent<String>>.
 */
export function parseSseChunk(buffer: string): { events: SseMessage[]; remaining: string } {
  const normalized = buffer.replace(/\r\n/g, "\n");
  const blocks = normalized.split("\n\n");
  const remaining = blocks.pop() ?? "";
  const events: SseMessage[] = [];

  for (const block of blocks) {
    if (!block.trim()) continue;

    const lines = block.split("\n");
    let eventName = "message";
    const dataParts: string[] = [];
    let id: string | undefined;

    for (const line of lines) {
      const trimmed = line.trim();
      if (!trimmed) continue;

      if (trimmed.startsWith("event:")) {
        eventName = trimmed.substring(6).trim();
      } else if (trimmed.startsWith("data:")) {
        const dataStr = trimmed.substring(5);
        const cleanData = dataStr.startsWith(" ") ? dataStr.substring(1) : dataStr;
        dataParts.push(cleanData);
      } else if (trimmed.startsWith("id:")) {
        id = trimmed.substring(3).trim();
      }
    }

    if (dataParts.length > 0) {
      events.push({
        event: eventName,
        data: dataParts.join("\n"),
        id,
      });
    }
  }

  return { events, remaining };
}

/**
 * PROTECTED CHAT STREAMING ENDPOINT: POST /api/chat/documents/{documentId}/query/stream (X-API-Version: v1)
 * Headers: X-API-Version: v1, X-Conversation-ID: <UUID>
 * Streams WebFlux Flux<ServerSentEvent<String>> tokens token-by-token.
 * Automatically catches 'event: error' events emitted by WebFlux onErrorResume and throws backend error messages.
 */
export async function streamQueryAssistantApi(
  documentId?: string,
  query?: string,
  conversationId?: string,
  onChunk?: (chunk: string) => void,
  provider: Provider = "NVIDIA",
  options?: RetrievalOptions,
  signal?: AbortSignal
): Promise<void> {
  const currentConversationId = conversationId || crypto.randomUUID();
  const docId = documentId || "default";

  const bodyPayload = {
    query: query || "",
    provider: provider || "NVIDIA",
    options: {
      topK: options?.topK ?? 4,
      similarityThreshold: options?.similarityThreshold ?? 0.70,
    },
  };

  try {
    const response = await fetchWithAuth(
      `${API_BASE_URL}/api/chat/documents/${docId}/query/stream`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "X-Conversation-ID": currentConversationId,
        },
        body: JSON.stringify(bodyPayload),
        signal,
      }
    );

    if (!response.ok || !response.body) {
      const errorData = await safeJsonResponse(response, {});
      const errorMsg =
        errorData.message ||
        errorData.error ||
        errorData.detail ||
        `Streaming failed (${response.status})`;
      throw new Error(errorMsg);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = "";

    const processEvents = (events: SseMessage[]) => {
      for (const sse of events) {
        const rawData = sse.data.trim();
        if (rawData === "[DONE]") continue;

        // If WebFlux onErrorResume emits event: error, extract and throw backend error message
        if (sse.event === "error") {
          let errorContent = rawData;
          if (rawData.startsWith("{") && rawData.endsWith("}")) {
            try {
              const parsed = JSON.parse(rawData);
              errorContent = parsed.message || parsed.error || parsed.detail || rawData;
            } catch {}
          }
          throw new Error(errorContent || "AI Assistant service error occurred.");
        }

        // Process standard message token payload
        let tokenChunk = rawData;
        if (rawData.startsWith("{") && rawData.endsWith("}")) {
          try {
            const parsed = JSON.parse(rawData);
            const jsonText =
              parsed.content ??
              parsed.text ??
              parsed.delta ??
              parsed.answer ??
              parsed.message;
            if (jsonText !== undefined && jsonText !== null) {
              tokenChunk = String(jsonText);
            }
          } catch {}
        }

        if (tokenChunk && onChunk) {
          onChunk(tokenChunk);
        }
      }
    };

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        if (buffer.trim()) {
          const { events } = parseSseChunk(buffer + "\n\n");
          processEvents(events);
        }
        break;
      }

      buffer += decoder.decode(value, { stream: true });
      const { events, remaining } = parseSseChunk(buffer);
      buffer = remaining;
      processEvents(events);
    }
  } catch (err: any) {
    if (err.name === "AbortError") return;
    throw err;
  }
}
