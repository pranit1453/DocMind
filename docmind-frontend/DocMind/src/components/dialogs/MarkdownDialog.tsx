import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import type { Message } from "@/types/chat";
import { buildMarkdown } from "@/utils/markdown";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";
import { FileCode2, Copy, Check } from "lucide-react";

interface MarkdownDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  messages: Message[];
  copied: boolean;
  onCopy: () => void;
}

export function MarkdownDialog({
  open,
  onOpenChange,
  messages,
  copied,
  onCopy,
}: MarkdownDialogProps) {
  const markdownText = buildMarkdown(messages);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-h-[85vh] max-w-4xl overflow-hidden p-0 rounded-2xl border-border">
        <DialogHeader className="border-b px-6 py-4 flex flex-row items-center justify-between">
          <DialogTitle className="flex items-center gap-2 text-base font-bold">
            <FileCode2 size={18} className="text-primary" />
            <span>Conversation Markdown Export</span>
          </DialogTitle>
          <Button
            variant="outline"
            size="xs"
            className="gap-1.5 mr-6 text-xs h-8"
            onClick={onCopy}
          >
            {copied ? (
              <>
                <Check size={14} className="text-emerald-500" />
                <span>Copied</span>
              </>
            ) : (
              <>
                <Copy size={14} />
                <span>Copy Markdown</span>
              </>
            )}
          </Button>
        </DialogHeader>

        <ScrollArea className="max-h-[calc(85vh-80px)] p-6 bg-muted/20">
          <article className="markdown-content text-xs leading-relaxed font-sans">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {markdownText}
            </ReactMarkdown>
          </article>
        </ScrollArea>
      </DialogContent>
    </Dialog>
  );
}
