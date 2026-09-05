import type {
  ApiResponse,
  DocumentResponse,
} from "./types";
import { API_BASE_URL, fetchWithAuth, safeJsonResponse } from "./apiClient";

/**
 * PROTECTED ENDPOINT: POST /api/documents/upload (X-API-Version: v1)
 * Uploads a document file for vector store indexing.
 */
export async function uploadDocumentApi(file: File): Promise<ApiResponse<DocumentResponse>> {
  const formData = new FormData();
  formData.append("file", file);

  const response = await fetchWithAuth(`${API_BASE_URL}/api/documents/upload`, {
    method: "POST",
    body: formData,
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    const errMsg = errorData.message || errorData.error || errorData.detail || `Upload failed (${response.status})`;
    throw new Error(errMsg);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PROTECTED ENDPOINT: GET /api/documents/{documentId} (X-API-Version: v1)
 * Fetches detailed metadata for a single document by ID.
 */
export async function fetchDocumentByIdApi(documentId: string): Promise<ApiResponse<DocumentResponse>> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/documents/${documentId}`, {
    method: "GET",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to fetch document (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PROTECTED ENDPOINT: GET /api/documents (X-API-Version: v1)
 * Fetches document list with scroll-based pagination.
 */
export async function fetchAllDocumentsApi(
  keyword?: string,
  scrollId?: string,
  pageSize = 5,
  sortBy?: string,
  sortDirection?: string,
  scrollDirection?: string
): Promise<any> {
  const safePageSize = Math.min(Math.max(pageSize, 1), 5);
  const params = new URLSearchParams();
  if (keyword && keyword.trim()) params.append("keyword", keyword.trim());
  if (scrollId && scrollId.trim()) params.append("scrollId", scrollId.trim());
  params.append("pageSize", String(safePageSize));
  if (sortBy && sortBy.trim()) params.append("sortBy", sortBy.trim());
  if (sortDirection && sortDirection.trim()) params.append("sortDirection", sortDirection.trim());
  if (scrollDirection && scrollDirection.trim()) params.append("scrollDirection", scrollDirection.trim());

  const queryString = params.toString() ? `?${params.toString()}` : "";
  const response = await fetchWithAuth(`${API_BASE_URL}/api/documents${queryString}`, {
    method: "GET",
  });

  if (!response.ok) {
    return {
      contents: [],
      hasNext: false,
      hasPrevious: false,
      pageSize: safePageSize,
    };
  }

  return await safeJsonResponse(response, { contents: [], hasNext: false, hasPrevious: false, pageSize: safePageSize });
}

/**
 * PROTECTED ENDPOINT: DELETE /api/documents/{documentId}/delete (X-API-Version: v1)
 * Deletes a document from the vector store.
 */
export async function deleteDocumentApi(documentId: string): Promise<void> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/documents/${documentId}/delete`, {
    method: "DELETE",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Delete document failed (${response.status})`);
  }
}
