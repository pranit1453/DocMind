import { useState, useMemo, useEffect } from "react";
import type { DocumentItem, DocumentStatus } from "@/types/document";
import type { Message } from "@/types/chat";
import { formatSize, getCurrentTime } from "@/utils/format";
import { buildMarkdown } from "@/utils/markdown";
import { initialDocuments, initialMessages } from "@/data/initialData";
import {
  fetchAllDocumentsApi,
  uploadDocumentApi,
  deleteDocumentApi,
} from "@/api/documentApi";
import { queryAssistantApi, streamQueryAssistantApi } from "@/api/chatApi";

export function useKnowledgeBase(enabled: boolean = true) {
  const [documents, setDocuments] = useState<DocumentItem[]>(initialDocuments);
  const [selectedId, setSelectedId] = useState<string | null>(
    initialDocuments.length > 0 ? initialDocuments[0].id : null
  );
  const [search, setSearch] = useState<string>("");
  const [input, setInput] = useState<string>("");
  const [messages, setMessages] = useState<Message[]>(initialMessages);
  const [isDragging, setIsDragging] = useState<boolean>(false);
  const [markdownOpen, setMarkdownOpen] = useState<boolean>(false);
  const [settingsOpen, setSettingsOpen] = useState<boolean>(false);
  const [copied, setCopied] = useState<boolean>(false);

  // Dynamic Conversation UUID State
  const [conversationId, setConversationId] = useState<string>(() => {
    try {
      return crypto.randomUUID();
    } catch {
      return "123e4567-e89b-12d3-a456-426614174000";
    }
  });

  // Streaming UI Toggle Mode
  const [useStreamApi, setUseStreamApi] = useState<boolean>(true);

  // Uploading and Indexing Indicator State
  const [isUploading, setIsUploading] = useState<boolean>(false);
  const [isFetchingDocuments, setIsFetchingDocuments] = useState<boolean>(false);
  const [uploadMessage, setUploadMessage] = useState<string>(
    "Document is uploading and indexing..."
  );

  const extractDocList = (raw: any): any[] => {
    if (!raw) return [];
    if (Array.isArray(raw)) return raw;
    if (Array.isArray(raw.contents)) return raw.contents;
    if (Array.isArray(raw.content)) return raw.content;
    if (Array.isArray(raw.data)) return raw.data;
    if (raw.data && Array.isArray(raw.data.contents)) return raw.data.contents;
    if (raw.data && Array.isArray(raw.data.content)) return raw.data.content;
    return [];
  };

  const mapDocumentResponseToItem = (doc: any): DocumentItem => {
    const docId = String(doc.documentId || doc.id || doc.docId || `doc_${Date.now()}`);
    const name = String(doc.fileName || doc.name || doc.filename || doc.title || "Untitled Document");
    const sizeVal = doc.fileSize ?? doc.size ?? doc.bytes ?? 0;
    const chunksVal = doc.chunksCreated ?? doc.chunks ?? doc.chunksCount ?? 0;
    const statusVal = String(doc.status || doc.fileStatus || "Indexed");

    const ext = name ? name.split(".").pop()?.toLowerCase() || "file" : "file";
    let formattedStatus: DocumentStatus = "Indexed";
    if (statusVal.toUpperCase().includes("PROCESS")) formattedStatus = "Processing";
    else if (statusVal.toUpperCase().includes("FAIL")) formattedStatus = "Failed";

    return {
      id: docId,
      name,
      size: typeof sizeVal === "number" ? formatSize(sizeVal) : String(sizeVal),
      chunks: Number(chunksVal) || 0,
      status: formattedStatus,
      type: ext,
    };
  };

  // Load initial documents from backend API if user is authenticated
  const refetchDocuments = async () => {
    if (!enabled) return;
    try {
      const res = await fetchAllDocumentsApi(undefined, undefined, 5);
      const rawDocs = extractDocList(res);
      if (rawDocs && rawDocs.length > 0) {
        const apiDocs = rawDocs.map(mapDocumentResponseToItem);
        setDocuments((prev) => {
          const apiDocsMap = new Map(apiDocs.map((d) => [d.id, d]));
          const updatedPrev = prev.map((d) => apiDocsMap.get(d.id) || d);
          const prevIds = new Set(prev.map((d) => d.id));
          const newFromApi = apiDocs.filter((d) => !prevIds.has(d.id));
          return [...updatedPrev, ...newFromApi];
        });
      }
    } catch {
      // Ignored refetch failure
    }
  };

  useEffect(() => {
    if (!enabled) return;

    let isMounted = true;
    async function loadDocs() {
      setIsFetchingDocuments(true);
      try {
        const res = await fetchAllDocumentsApi(undefined, undefined, 5);
        const rawDocs = extractDocList(res);
        if (isMounted && rawDocs && rawDocs.length > 0) {
          const apiDocs = rawDocs.map(mapDocumentResponseToItem);
          setDocuments(apiDocs);
          if (apiDocs.length > 0) {
            setSelectedId((prev) => prev ?? apiDocs[0].id);
          }
        }
      } catch {
        // Ignored initial doc load exception
      } finally {
        if (isMounted) setIsFetchingDocuments(false);
      }
    }
    loadDocs();
  }, [enabled]);

  const startNewConversation = () => {
    const newId = crypto.randomUUID();
    setConversationId(newId);
    setMessages([]);
  };

  /**
   * Select a document from the Knowledge Base sidebar
   * Automatically resets conversation ID with crypto.randomUUID()
   */
  const selectDocument = (id: string) => {
    setSelectedId(id);
    startNewConversation();
  };

  const selectedDocument = useMemo(() => {
    if (selectedId === null) return undefined;
    return documents.find((doc) => doc.id === selectedId);
  }, [documents, selectedId]);

  const filteredDocuments = useMemo(() => {
    return documents.filter((doc) =>
      doc.name.toLowerCase().includes(search.toLowerCase())
    );
  }, [documents, search]);

  const toggleStreamApi = () => {
    setUseStreamApi((prev) => !prev);
  };

  const extractDocItemFromResponse = (res: any, file: File): DocumentItem => {
    const target = res?.data?.data || res?.data || res?.document || res?.content || res?.result || res || {};
    const docId = String(target.documentId || target.id || target.docId || target.uuid || `doc_${Date.now()}`);
    const fileName = String(target.fileName || target.name || target.filename || target.title || file.name);
    const fileSize = target.fileSize ?? target.size ?? target.bytes ?? file.size;
    const chunksCount = target.chunksCreated ?? target.chunks ?? target.chunksCount ?? 0;
    const statusStr = String(target.status || target.fileStatus || "Indexed");

    const ext = fileName.split(".").pop()?.toLowerCase() || "file";
    let formattedStatus: DocumentStatus = "Indexed";
    if (statusStr.toUpperCase().includes("PROCESS")) formattedStatus = "Processing";
    else if (statusStr.toUpperCase().includes("FAIL")) formattedStatus = "Failed";

    return {
      id: docId,
      name: fileName,
      size: typeof fileSize === "number" ? formatSize(fileSize) : String(fileSize),
      chunks: Number(chunksCount) || 0,
      status: formattedStatus,
      type: ext,
    };
  };

  /**
   * Upload Flow: Upload file via POST /api/v1/documents
   * Immediately adds returned document to Available Files section and sets as active
   */
  const uploadFiles = async (files: FileList | null) => {
    if (!files?.length) return;

    setIsUploading(true);
    const fileList = Array.from(files);

    for (let i = 0; i < fileList.length; i++) {
      const file = fileList[i];
      setUploadMessage(`Indexing "${file.name}" into knowledge base...`);

      try {
        const res = await uploadDocumentApi(file);
        const newDoc = extractDocItemFromResponse(res, file);
        setDocuments((prev) => [newDoc, ...prev.filter((d) => d.id !== newDoc.id && d.name !== newDoc.name)]);
        selectDocument(newDoc.id);
      } catch {
        const fallbackDoc: DocumentItem = {
          id: `doc_${Date.now()}_${i}`,
          name: file.name,
          size: formatSize(file.size),
          chunks: 0,
          status: "Indexed" as DocumentStatus,
          type: file.name.split(".").pop()?.toLowerCase() || "file",
        };
        setDocuments((prev) => [fallbackDoc, ...prev.filter((d) => d.name !== fallbackDoc.name)]);
        selectDocument(fallbackDoc.id);
      }
    }

    await refetchDocuments();
    setIsUploading(false);
  };

  /**
   * Delete document via DELETE /api/v1/documents/{documentId}
   */
  const removeDocument = async (id: string, event?: React.MouseEvent) => {
    event?.stopPropagation();

    try {
      await deleteDocumentApi(id);
    } catch {
      // Continue UI state cleanup on error
    }

    setDocuments((prev) => prev.filter((doc) => doc.id !== id));

    if (selectedId === id) {
      const remaining = documents.filter((doc) => doc.id !== id);
      if (remaining.length > 0) {
        setSelectedId(remaining[0].id);
      } else {
        setSelectedId(null);
      }
    }
  };

  /**
   * Send Message: Executes RAG Assistant query with X-Conversation-ID, provider: "NVIDIA", topK & similarityThreshold
   */
  const sendMessage = async (text?: string) => {
    const content = (text ?? input).trim();
    if (!content) return;

    const userMessage: Message = {
      id: Date.now(),
      role: "user",
      content,
      timestamp: getCurrentTime(),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");

    const assistantMsgId = Date.now() + 1;

    const assistantMessagePlaceholder: Message = {
      id: assistantMsgId,
      role: "assistant",
      content: "",
      timestamp: getCurrentTime(),
      sources: undefined,
    };

    setMessages((prev) => [...prev, assistantMessagePlaceholder]);

    // Retrieve user vector settings from localStorage
    const storedTopK = localStorage.getItem("docmind-topk");
    const storedThreshold = localStorage.getItem("docmind-similarity-threshold");
    const topK = storedTopK ? parseInt(storedTopK, 10) : 4;
    const similarityThreshold = storedThreshold ? parseFloat(storedThreshold) : 0.70;

    const startTime = performance.now();

    if (useStreamApi) {
      try {
        await streamQueryAssistantApi(
          selectedId ?? undefined,
          content,
          conversationId,
          (chunk) => {
            setMessages((prev) =>
              prev.map((msg) =>
                msg.id === assistantMsgId
                  ? { ...msg, content: msg.content + chunk }
                  : msg
              )
            );
          },
          "NVIDIA",
          { topK, similarityThreshold }
        );
        const endTime = performance.now();
        const calcTime = `${((endTime - startTime) / 1000).toFixed(2)}s`;
        setMessages((prev) =>
          prev.map((msg) =>
            msg.id === assistantMsgId
              ? { ...msg, executionTime: msg.executionTime || calcTime }
              : msg
          )
        );
      } catch {
        const endTime = performance.now();
        const calcTime = `${((endTime - startTime) / 1000).toFixed(2)}s`;
        setMessages((prev) =>
          prev.map((msg) =>
            msg.id === assistantMsgId
              ? {
                  ...msg,
                  content: msg.content || "Service unavailable. Please try again later.",
                  executionTime: calcTime,
                }
              : msg
          )
        );
      }
    } else {
      try {
        const res: any = await queryAssistantApi(
          selectedId ?? undefined,
          content,
          conversationId,
          "NVIDIA",
          { topK, similarityThreshold }
        );
        const endTime = performance.now();
        const calcTime = `${((endTime - startTime) / 1000).toFixed(2)}s`;
        const execTime = res?.executionTime || res?.data?.executionTime || calcTime;
        const responseText =
          (typeof res === "string" ? res : null) ||
          res?.content ||
          res?.answer ||
          res?.message ||
          res?.data?.content ||
          res?.data?.answer ||
          res?.data?.message ||
          "";

        setMessages((prev) =>
          prev.map((msg) =>
            msg.id === assistantMsgId
              ? {
                  ...msg,
                  content: responseText || "Service unavailable. Please try again later.",
                  executionTime: execTime,
                  sources: res?.sources && res.sources.length > 0 ? res.sources : msg.sources,
                }
              : msg
          )
        );
      } catch {
        const endTime = performance.now();
        const calcTime = `${((endTime - startTime) / 1000).toFixed(2)}s`;
        setMessages((prev) =>
          prev.map((msg) =>
            msg.id === assistantMsgId
              ? {
                  ...msg,
                  content: "Service unavailable. Please try again later.",
                  executionTime: calcTime,
                }
              : msg
          )
        );
      }
    }
  };

  const copyConversationAsMarkdown = async () => {
    const markdown = buildMarkdown(messages);
    await navigator.clipboard.writeText(markdown);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 2000);
  };

  const exportConversationAsMarkdown = () => {
    const markdown = buildMarkdown(messages);
    const blob = new Blob([markdown], { type: "text/markdown;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `docmind-conversation-${new Date().toISOString().slice(0, 10)}.md`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  const clearChat = () => {
    startNewConversation();
  };

  return {
    documents,
    selectedId,
    setSelectedId: selectDocument,
    selectedDocument,
    filteredDocuments,
    search,
    setSearch,
    input,
    setInput,
    messages,
    conversationId,
    startNewConversation,
    isDragging,
    setIsDragging,
    markdownOpen,
    setMarkdownOpen,
    settingsOpen,
    setSettingsOpen,
    copied,
    useStreamApi,
    toggleStreamApi,
    isUploading,
    isFetchingDocuments,
    uploadMessage,
    uploadFiles,
    removeDocument,
    sendMessage,
    copyConversationAsMarkdown,
    exportConversationAsMarkdown,
    clearChat,
  };
}

