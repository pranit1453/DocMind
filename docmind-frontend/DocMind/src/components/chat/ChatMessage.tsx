import type { Message, Source, Citation } from "@/types/chat";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { User, Loader2, Clock, Sparkles, AlertCircle, FileText, Bookmark, Layers } from "lucide-react";
import { AIResponseRenderer } from "./AIResponseRenderer";
import { cn } from "@/lib/utils";

interface ChatMessageProps {
  message: Message;
}

export function ChatMessage({ message }: ChatMessageProps) {
  const isUser = message.role === "user";
  const isError = message.status === "error" || Boolean(message.error);
  const isStreaming = message.status === "streaming";

  return (
    <div
      className={cn(
        "flex gap-3 text-xs leading-relaxed animate-in fade-in-0 duration-300",
        isUser ? "justify-end" : "justify-start"
      )}
    >
      {/* Message Bubble */}
      <div
        className={cn(
          "relative max-w-[88%] sm:max-w-[75%] md:max-w-[70%] rounded-2xl px-4 py-3 shadow-xs transition-all",
          isUser
            ? "rounded-br-xs bg-primary text-primary-foreground font-medium"
            : "rounded-bl-xs border border-border/80 bg-card text-card-foreground"
        )}
      >
        {/* Header line for assistant */}
        {!isUser && (
          <div className="mb-2 flex items-center justify-between border-b border-border/40 pb-1.5 text-[10px] text-muted-foreground">
            <span
              className="font-semibold text-foreground flex items-center gap-1.5"
              title="DocMind Assistant"
            >
              <Sparkles size={14} className="text-primary" />
              <span>DocMind Assistant</span>
            </span>
            {message.timestamp && <span>{message.timestamp}</span>}
          </div>
        )}

        {/* Content Body */}
        {isUser ? (
          <div className="whitespace-pre-wrap text-xs">{message.content}</div>
        ) : (
          <div className="space-y-3">
            {/* Render AI Answer Content using Shared AIResponseRenderer */}
            {message.content ? (
              <AIResponseRenderer
                content={message.content}
                status={message.status}
              />
            ) : isStreaming ? (
              // Initial Thinking Animation before first chunk arrives
              <div className="flex items-center gap-2.5 py-1 text-xs text-muted-foreground select-none">
                <div className="relative flex h-4.5 w-4.5 items-center justify-center">
                  <Loader2
                    size={15}
                    className="animate-spin text-primary shrink-0"
                  />
                  <div className="absolute inset-0 rounded-full bg-primary/20 animate-ping opacity-40" />
                </div>
                <div className="inline-flex items-center gap-1.5 font-medium text-foreground/80 leading-none">
                  <span className="text-xs">Thinking & generating response</span>
                  <span className="inline-flex items-center gap-1">
                    <span className="h-1.5 w-1.5 rounded-full bg-primary animate-bounce [animation-delay:-0.3s]" />
                    <span className="h-1.5 w-1.5 rounded-full bg-primary animate-bounce [animation-delay:-0.15s]" />
                    <span className="h-1.5 w-1.5 rounded-full bg-primary animate-bounce" />
                  </span>
                </div>
              </div>
            ) : null}

            {/* Error UI Display */}
            {isError && (
              <div className="flex items-start gap-2 rounded-xl border border-destructive/30 bg-destructive/10 p-3 text-xs text-destructive">
                <AlertCircle size={15} className="mt-0.5 shrink-0" />
                <div className="leading-snug font-medium">
                  {message.error || "AI service is temporarily unavailable. Please try again."}
                </div>
              </div>
            )}
          </div>
        )}

        {/* Document Sources Component */}
        {!isUser && message.sources && message.sources.length > 0 && (
          <SourcesList sources={message.sources} />
        )}

        {/* Response Metadata (Execution / Response Time) */}
        {!isUser && message.executionTime && !isStreaming && (
          <div className="mt-3 flex items-center justify-start border-t border-border/40 pt-2 text-[10px] text-muted-foreground select-none">
            <div className="flex items-center gap-1 text-emerald-500 font-mono font-semibold bg-emerald-500/10 px-2 py-0.5 rounded-md border border-emerald-500/20">
              <Clock size={11} className="shrink-0" />
              <span>Response Time: {message.executionTime}</span>
            </div>
          </div>
        )}

        {/* Response Grounding Citations List - Rendered below Response Time */}
        {!isUser && message.citations && message.citations.length > 0 && (
          <CitationsList citations={message.citations} />
        )}
      </div>

      {/* User Avatar */}
      {isUser && (
        <Avatar className="h-8 w-8 shrink-0 border border-border">
          <AvatarFallback className="bg-secondary text-secondary-foreground text-[11px] font-bold">
            <User size={15} />
          </AvatarFallback>
        </Avatar>
      )}
    </div>
  );
}

function CitationsList({ citations }: { citations: Citation[] }) {
  if (!citations || citations.length === 0) return null;

  return (
    <div className="mt-2.5 border-t border-border/40 pt-2.5 space-y-2 select-none">
      <div className="flex items-center gap-1.5 text-[11px] font-bold text-foreground">
        <Bookmark size={12} className="text-primary shrink-0" />
        <span>Source Citations ({citations.length}):</span>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-[10px]">
        {citations.map((cit, idx) => {
          const scorePercent =
            cit.similarityScore !== undefined && cit.similarityScore !== null
              ? cit.similarityScore <= 1
                ? (cit.similarityScore * 100).toFixed(1) + "% match"
                : cit.similarityScore.toFixed(2)
              : null;

          return (
            <div
              key={cit.documentId || idx}
              className="flex flex-col gap-1.5 rounded-xl border border-border/70 bg-muted/40 p-2.5 transition-all hover:bg-muted/70 hover:border-primary/30"
            >
              {/* File Name & Similarity Badge */}
              <div className="flex items-center justify-between gap-1.5">
                <div className="flex items-center gap-1.5 min-w-0 font-semibold text-foreground">
                  <FileText size={12} className="text-primary shrink-0" />
                  <span className="truncate" title={cit.fileName || "Document"}>
                    {cit.fileName || "Document"}
                  </span>
                </div>
                {scorePercent && (
                  <span className="shrink-0 font-mono text-[9px] font-bold text-primary bg-primary/10 px-1.5 py-0.5 rounded border border-primary/20">
                    {scorePercent}
                  </span>
                )}
              </div>

              {/* Page, Chunk & Context Info */}
              <div className="flex flex-wrap items-center gap-1 text-[9px] text-muted-foreground">
                {cit.pageNumber !== undefined && cit.pageNumber !== null && (
                  <span className="bg-background px-1.5 py-0.5 rounded border border-border/50 font-medium text-foreground/80">
                    Page {cit.pageNumber}
                  </span>
                )}
                {cit.chunkIndex !== undefined && cit.chunkIndex !== null && (
                  <span className="bg-background px-1.5 py-0.5 rounded border border-border/50 font-medium text-foreground/80 flex items-center gap-0.5">
                    <Layers size={9} className="text-muted-foreground" />
                    Chunk #{cit.chunkIndex}
                  </span>
                )}
                {cit.previousChunkIndex !== undefined && cit.previousChunkIndex !== null && (
                  <span className="bg-background/60 px-1.5 py-0.5 rounded text-[8.5px] text-muted-foreground">
                    Prev: #{cit.previousChunkIndex}
                  </span>
                )}
                {cit.nextChunkIndex !== undefined && cit.nextChunkIndex !== null && (
                  <span className="bg-background/60 px-1.5 py-0.5 rounded text-[8.5px] text-muted-foreground">
                    Next: #{cit.nextChunkIndex}
                  </span>
                )}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

function SourcesList({ sources }: { sources: Source[] }) {
  return (
    <div className="mt-3 flex flex-wrap items-center gap-1.5 pt-2 border-t border-border/40 text-[10px] select-none">
      <span className="font-semibold text-muted-foreground">Sources:</span>
      {sources.map((src, idx) => (
        <span
          key={idx}
          className="inline-flex items-center gap-1 bg-muted/80 px-2 py-0.5 rounded-md border border-border/50 text-foreground/80 font-medium"
        >
          <FileText size={10} className="text-primary" />
          <span>{src.document}</span>
          {src.page ? <span className="text-muted-foreground">(p. {src.page})</span> : null}
        </span>
      ))}
    </div>
  );
}
