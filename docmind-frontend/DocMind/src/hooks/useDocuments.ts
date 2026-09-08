import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchAllDocumentsApi,
  fetchDocumentByIdApi,
  uploadDocumentApi,
  deleteDocumentApi,
} from "@/api/documentApi";
import type { ScrollResponseDocumentResponse, ApiResponse, DocumentResponse } from "@/api/types";

export const DOCUMENT_QUERY_KEYS = {
  all: ["documents"] as const,
  list: (keyword?: string, scrollId?: string, pageSize?: number, sortBy?: string, sortDirection?: string) =>
    [...DOCUMENT_QUERY_KEYS.all, "list", { keyword, scrollId, pageSize, sortBy, sortDirection }] as const,
  detail: (id: string) => [...DOCUMENT_QUERY_KEYS.all, "detail", id] as const,
};

/**
 * Query hook to fetch documents list with TanStack Query caching & background revalidation
 */
export function useDocumentsQuery(
  keyword?: string,
  scrollId?: string,
  pageSize = 5,
  sortBy = "fileName",
  sortDirection = "ASC",
  enabled = true
) {
  return useQuery<ScrollResponseDocumentResponse, Error>({
    queryKey: DOCUMENT_QUERY_KEYS.list(keyword, scrollId, pageSize, sortBy, sortDirection),
    queryFn: () => fetchAllDocumentsApi(keyword, scrollId, pageSize, sortBy, sortDirection),
    enabled,
    staleTime: 1000 * 30, // 30 seconds fresh cache
    refetchOnWindowFocus: false,
  });
}

/**
 * Query hook to fetch single document by ID
 */
export function useDocumentByIdQuery(documentId: string | null, enabled = true) {
  return useQuery<ApiResponse<DocumentResponse>, Error>({
    queryKey: DOCUMENT_QUERY_KEYS.detail(documentId || ""),
    queryFn: () => fetchDocumentByIdApi(documentId!),
    enabled: enabled && !!documentId,
    staleTime: 1000 * 60, // 1 minute fresh cache
  });
}

/**
 * Mutation hook to upload a document with automatic query cache invalidation
 */
export function useUploadDocumentMutation() {
  const queryClient = useQueryClient();

  return useMutation<ApiResponse<DocumentResponse>, Error, File>({
    mutationFn: (file: File) => uploadDocumentApi(file),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENT_QUERY_KEYS.all });
    },
  });
}

/**
 * Mutation hook to delete a document with automatic query cache invalidation
 */
export function useDeleteDocumentMutation() {
  const queryClient = useQueryClient();

  return useMutation<void, Error, string>({
    mutationFn: (documentId: string) => deleteDocumentApi(documentId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: DOCUMENT_QUERY_KEYS.all });
    },
  });
}
