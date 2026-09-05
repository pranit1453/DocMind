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
    throw new Error(errorData.message || `Chat query failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * Helper function to parse Server-Sent Events (SSE) data lines.
 * Strips 'data:' prefixes, parses JSON payloads if present, and ignores [DONE] signals.
 */
function parseSseLine(line: string): string | null {
  const trimmed = line.trim();
  if (!trimmed) return null;

  if (line.startsWith("data:")) {
    const afterData = line.substring(5);
    if (afterData.trim() === "[DONE]") {
      return null;
    }

    const payload = afterData.trim();
    if (payload.startsWith("{") && payload.endsWith("}")) {
      try {
        const parsed = JSON.parse(payload);
        const jsonText =
          parsed.content ??
          parsed.text ??
          parsed.delta ??
          parsed.answer ??
          parsed.message;
        if (jsonText !== undefined && jsonText !== null) {
          return String(jsonText);
        }
      } catch {
        // Fallback to afterData string
      }
    }
    return afterData;
  }

  if (trimmed === "[DONE]") return null;
  return line;
}

/**
 * PROTECTED CHAT STREAMING ENDPOINT: POST /api/chat/documents/{documentId}/query/stream (X-API-Version: v1)
 * Headers: X-API-Version: v1, X-Conversation-ID: <UUID>
 * Stream assistant response token by token via SSE / ReadableStream.
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
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `Streaming failed (${response.status})`);
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    let buffer = "";

    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        if (buffer.length > 0) {
          const token = parseSseLine(buffer);
          if (token !== null && onChunk) {
            onChunk(token);
          }
        }
        break;
      }

      buffer += decoder.decode(value, { stream: true });
      const lines = buffer.split("\n");
      // Retain incomplete trailing line in buffer
      buffer = lines.pop() ?? "";

      for (const line of lines) {
        const token = parseSseLine(line);
        if (token !== null && onChunk) {
          onChunk(token);
        }
      }
    }
  } catch (err: any) {
    if (err.name === "AbortError") return;
    throw err;
  }
}
