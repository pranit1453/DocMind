import { useRef, useEffect } from "react";
import type { DocumentItem } from "@/types/document";
import type { Message } from "@/types/chat";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { Tooltip, TooltipTrigger, TooltipContent } from "@/components/ui/tooltip";
import {
  Bot,
  Download,
  Copy,
  Check,
  Trash2,
  FileCode2,
  Zap,
  Radio,
} from "lucide-react";
import { ContextBar } from "./ContextBar";
import { EmptyChat } from "./EmptyChat";
import { ChatMessage } from "./ChatMessage";
import { ChatComposer } from "./ChatComposer";
import { cn } from "@/lib/utils";

interface ChatPanelProps {
  selectedDocument?: DocumentItem;
  messages: Message[];
  input: string;
  setInput: (value: string) => void;
  onSendMessage: (text?: string) => void;
  onCopyMarkdown: () => void;
  onExportMarkdown: () => void;
  onOpenMarkdownModal: () => void;
  onClearChat: () => void;
  copied: boolean;
  useStreamApi: boolean;
  onToggleStreamApi: () => void;
  isUploading?: boolean;
  uploadMessage?: string;
}

export function ChatPanel({
  selectedDocument,
  messages,
  input,
  setInput,
  onSendMessage,
  onCopyMarkdown,
  onExportMarkdown,
  onOpenMarkdownModal,
  onClearChat,
  copied,
  useStreamApi,
  onToggleStreamApi,
  isUploading = false,
  uploadMessage,
}: ChatPanelProps) {
  const scrollAreaRef = useRef<HTMLDivElement>(null);

  // Auto-scroll to bottom on new messages
  useEffect(() => {
    if (scrollAreaRef.current) {
      const scrollContainer = scrollAreaRef.current.querySelector(
        "[data-radix-scroll-area-viewport]"
      );
      if (scrollContainer) {
        scrollContainer.scrollTop = scrollContainer.scrollHeight;
      }
    }
  }, [messages]);

  // Show template cards when chat has 0 messages or only initial assistant welcome
  const isInitialState =
    messages.length === 0 || (messages.length === 1 && messages[0].role === "assistant");

  return (
    <section className="flex h-full min-h-0 flex-col bg-background">
      {/* Header Toolbar */}
      <div className="flex h-14 shrink-0 items-center justify-between border-b px-5 select-none">
        <div className="flex min-w-0 items-center gap-3">
          <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary border border-primary/20">
            <Bot size={17} />
          </div>

          <div className="min-w-0">
            <div className="flex items-center gap-2">
              <h2 className="text-xs font-bold text-foreground tracking-tight">
                DocMind Assistant
              </h2>
              <Badge variant="outline" className="text-[9px] font-semibold border-primary/30 text-primary bg-primary/5">
                RAG workflow
              </Badge>
            </div>
            <p className="truncate text-[10px] text-muted-foreground font-medium">
              {selectedDocument
                ? `Scoped to: ${selectedDocument.name}`
                : "No active document context"}
            </p>
          </div>
        </div>

        {/* Action Buttons & SSE Stream vs Normal API Toggle */}
        <div className="flex items-center gap-1.5">

          {/* Stream API vs Normal REST API Toggle Switch */}
          <Tooltip>
            <TooltipTrigger asChild>
              <button
                type="button"
                onClick={onToggleStreamApi}
                className="flex items-center gap-1.5 rounded-xl border border-border bg-card px-2.5 py-1 text-[10px] font-semibold transition-all hover:bg-accent cursor-pointer"
              >
                {useStreamApi ? (
                  <>
                    <Zap size={12} className="text-amber-500 fill-amber-500/20 animate-pulse" />
                    <span className="text-foreground">Streaming</span>
                  </>
                ) : (
                  <>
                    <Radio size={12} className="text-muted-foreground" />
                    <span className="text-muted-foreground">Blocking</span>
                  </>
                )}
              </button>
            </TooltipTrigger>
            <TooltipContent side="bottom" className="text-[10px]">
              {useStreamApi ? "Streaming response mode" : "Blocking response mode"}
            </TooltipContent>
          </Tooltip>

          <Separator orientation="vertical" className="h-4 mx-0.5" />

          {/* View Raw Markdown Modal */}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-muted-foreground hover:bg-accent hover:text-foreground"
            onClick={onOpenMarkdownModal}
            title="View Markdown Document"
          >
            <FileCode2 size={15} />
          </Button>

          {/* Export Markdown */}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-muted-foreground hover:bg-accent hover:text-foreground"
            onClick={onExportMarkdown}
            title="Export conversation as Markdown (.md)"
          >
            <Download size={15} />
          </Button>

          {/* Copy Markdown */}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-muted-foreground hover:bg-accent hover:text-foreground"
            onClick={onCopyMarkdown}
            title="Copy conversation to clipboard"
          >
            {copied ? (
              <Check size={15} className="text-emerald-500" />
            ) : (
              <Copy size={15} />
            )}
          </Button>

          {/* Clear Chat */}
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
            onClick={onClearChat}
            title="Clear current conversation"
          >
            <Trash2 size={15} />
          </Button>
        </div>
      </div>

      {/* Context bar */}
      <ContextBar selectedDocument={selectedDocument} />

      {/* Main Chat Scroll Container */}
      <ScrollArea ref={scrollAreaRef} className="min-h-0 flex-1">
        <div className={cn("mx-auto w-full max-w-6xl px-4 sm:px-6 md:px-8 py-6 flex flex-col", isInitialState && "min-h-[calc(100vh-11rem)] justify-center items-center")}>
          {isInitialState ? (
            <EmptyChat
              selectedDocument={selectedDocument}
              onPrompt={(text) => onSendMessage(text)}
            />
          ) : (
            <div className="space-y-6">
              {messages.map((message) => (
                <ChatMessage key={message.id} message={message} />
              ))}
            </div>
          )}
        </div>
      </ScrollArea>

      {/* Input Composer */}
      <ChatComposer
        input={input}
        setInput={setInput}
        selectedDocument={selectedDocument}
        onSendMessage={() => onSendMessage()}
        isUploading={isUploading}
        uploadMessage={uploadMessage}
      />
    </section>
  );
}
