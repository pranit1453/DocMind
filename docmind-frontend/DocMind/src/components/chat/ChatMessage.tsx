import type { Message, Source } from "@/types/chat";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { User, Loader2, Clock, Sparkles, AlertCircle, FileText } from "lucide-react";
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

            {/* Error UI Display (Requirement 9) */}
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

        {/* Document Sources Component (Requirement 8) */}
        {!isUser && message.sources && message.sources.length > 0 && (
          <SourcesList sources={message.sources} />
        )}

        {/* Response Metadata (Requirement 7) */}
        {!isUser && message.executionTime && !isStreaming && (
          <div className="mt-3 flex items-center justify-start border-t border-border/40 pt-2 text-[10px] text-muted-foreground select-none">
            <div className="flex items-center gap-1 text-emerald-500 font-mono font-semibold bg-emerald-500/10 px-2 py-0.5 rounded-md border border-emerald-500/20">
              <Clock size={11} className="shrink-0" />
              <span>Response Time: {message.executionTime}</span>
            </div>
          </div>
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
