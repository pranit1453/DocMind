import { useRef, useEffect, useState } from "react";
import type { DocumentItem } from "@/types/document";
import type { QueryType } from "@/api/types";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Tooltip, TooltipTrigger, TooltipContent } from "@/components/ui/tooltip";
import { Send, Mic, ShieldCheck, Loader2, MessageSquare, FileText, Lightbulb } from "lucide-react";
import { cn } from "@/lib/utils";

interface ChatComposerProps {
  input: string;
  setInput: (value: string) => void;
  selectedDocument?: DocumentItem;
  onSendMessage: (text?: string, queryType?: QueryType) => void;
  isUploading?: boolean;
  uploadMessage?: string;
}

const QUERY_TYPES: { type: QueryType; label: string; icon: React.ReactNode; tooltip: string }[] = [
  {
    type: "NORMAL_QA",
    label: "Normal Q&A",
    icon: <MessageSquare size={11} />,
    tooltip: "Standard document question & answer workflow",
  },
  {
    type: "DOCUMENT_SUMMARY",
    label: "Summarize",
    icon: <FileText size={11} />,
    tooltip: "Generate structured executive summary",
  },
  {
    type: "TECHNICAL_INSIGHTS",
    label: "Technical Insights",
    icon: <Lightbulb size={11} />,
    tooltip: "Extract architectural, design & technical insights",
  },
];

export function ChatComposer({
  input,
  setInput,
  selectedDocument,
  onSendMessage,
  isUploading = false,
  uploadMessage = "Document is uploading and indexing...",
}: ChatComposerProps) {
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const [queryType, setQueryType] = useState<QueryType>("NORMAL_QA");

  // Auto resize textarea height based on content
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
      textareaRef.current.style.height = `${Math.min(
        textareaRef.current.scrollHeight,
        100
      )}px`;
    }
  }, [input]);

  const handleSend = () => {
    if (input.trim() && selectedDocument && !isUploading) {
      onSendMessage(input, queryType);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const getPlaceholder = () => {
    if (isUploading) {
      return uploadMessage;
    }
    if (selectedDocument) {
      if (queryType === "DOCUMENT_SUMMARY") {
        return `Summarize ${selectedDocument.name}...`;
      }
      if (queryType === "TECHNICAL_INSIGHTS") {
        return `Extract technical insights from ${selectedDocument.name}...`;
      }
      return `Ask anything about ${selectedDocument.name}...`;
    }
    return "Select a document to start chatting...";
  };

  return (
    <div className="shrink-0 bg-background/80 backdrop-blur-md px-4 pb-2.5 pt-1.5">
      <div className="mx-auto w-full max-w-6xl px-4 sm:px-6 md:px-8">
        {/* Query Type Selection Pills */}
        <div className="mb-2 flex items-center gap-1.5 select-none overflow-x-auto pb-0.5">
          <span className="text-[10px] font-semibold text-muted-foreground mr-1">
            Query Mode:
          </span>
          {QUERY_TYPES.map((qt) => {
            const isSelected = queryType === qt.type;
            return (
              <Tooltip key={qt.type}>
                <TooltipTrigger asChild>
                  <button
                    type="button"
                    onClick={() => setQueryType(qt.type)}
                    className={cn(
                      "flex items-center gap-1 rounded-lg px-2 py-0.5 text-[10px] font-semibold transition-all cursor-pointer border",
                      isSelected
                        ? "bg-primary/10 text-primary border-primary/30 ring-1 ring-primary/20 shadow-xs"
                        : "bg-card text-muted-foreground border-border/60 hover:bg-accent hover:text-foreground"
                    )}
                  >
                    {qt.icon}
                    <span>{qt.label}</span>
                  </button>
                </TooltipTrigger>
                <TooltipContent side="top" className="text-[10px]">
                  {qt.tooltip}
                </TooltipContent>
              </Tooltip>
            );
          })}
        </div>

        {/* Compact Rounded Input Container */}
        <div
          className={cn(
            "group relative flex items-center gap-2 rounded-[20px] border border-border bg-card px-3.5 py-1.5 shadow-sm transition-all duration-200",
            "focus-within:border-primary/50 focus-within:ring-2 focus-within:ring-primary/10 focus-within:shadow-md",
            isUploading && "border-amber-500/40 bg-amber-500/5 ring-1 ring-amber-500/20"
          )}
        >
          {isUploading && (
            <Loader2 size={16} className="text-amber-500 animate-spin shrink-0 ml-1" />
          )}

          {/* Text Area Input */}
          <Textarea
            ref={textareaRef}
            value={input}
            disabled={!selectedDocument || isUploading}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            rows={1}
            placeholder={getPlaceholder()}
            className={cn(
              "min-h-[36px] max-h-[100px] w-full resize-none border-0 bg-transparent px-1 py-1.5 text-xs leading-normal shadow-none outline-none focus-visible:ring-0 placeholder:text-muted-foreground/60 disabled:cursor-not-allowed disabled:opacity-70",
              isUploading && "placeholder:text-amber-500/80 font-medium"
            )}
          />

          {/* Right Inline Action Buttons */}
          <div className="flex items-center gap-1 shrink-0 select-none">
            {/* Mic Button */}
            <Tooltip>
              <TooltipTrigger asChild>
                <Button
                  type="button"
                  variant="ghost"
                  size="icon"
                  className="h-7 w-7 rounded-full text-muted-foreground/60 hover:text-muted-foreground hover:bg-accent/60 opacity-80"
                >
                  <Mic size={15} />
                </Button>
              </TooltipTrigger>
              <TooltipContent side="top" className="text-[10px] py-1 px-2">
                Voice input is currently not available
              </TooltipContent>
            </Tooltip>

            {/* Send Button */}
            <Button
              type="button"
              size="icon"
              className="h-7.5 w-7.5 rounded-full bg-primary text-primary-foreground shadow-xs transition-transform hover:scale-105 active:scale-95 disabled:opacity-40"
              disabled={!input.trim() || !selectedDocument || isUploading}
              onClick={handleSend}
              title="Send Message"
            >
              <Send size={13} />
            </Button>
          </div>
        </div>

        {/* Compact Footer Grounding Hint */}
        <div className="mt-1.5 flex items-center justify-center gap-1.5 text-[9px] text-muted-foreground/70 font-medium select-none">
          <ShieldCheck size={11} className="text-emerald-500" />
          <span>Responses are verified and grounded in local knowledge base vectors</span>
        </div>
      </div>
    </div>
  );
}
