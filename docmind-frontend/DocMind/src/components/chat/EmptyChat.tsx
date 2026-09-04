import type { DocumentItem } from "@/types/document";
import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";
import { Sparkles, FileText, Lightbulb } from "lucide-react";

interface EmptyChatProps {
  selectedDocument?: DocumentItem;
  onPrompt: (prompt: string) => void;
}

export function EmptyChat({ selectedDocument, onPrompt }: EmptyChatProps) {
  return (
    <div className="flex w-full flex-col items-center justify-center text-center py-6 px-4 select-none my-auto">
      {/* Icon Badge */}
      <div className="mb-4 flex h-14 w-14 items-center justify-center rounded-2xl bg-primary/10 text-primary border border-primary/20 shadow-md shadow-primary/5">
        <Sparkles size={26} className="animate-pulse" />
      </div>

      {/* Header */}
      <h2 className="text-xl font-bold tracking-tight text-foreground">
        DocMind RAG Assistant
      </h2>

      <p className="mt-2 max-w-md text-xs leading-relaxed text-muted-foreground">
        Ask questions about your uploaded knowledge documents.
        Answers are strictly grounded using vector embeddings and source references.
      </p>

      {/* Selected Document Chip */}
      {selectedDocument ? (
        <Badge
          variant="secondary"
          className="mt-4 max-w-[320px] gap-2 px-3 py-1 text-xs truncate border bg-card shadow-xs"
        >
          <FileText size={13} className="text-primary shrink-0" />
          <span className="truncate">{selectedDocument.name}</span>
        </Badge>
      ) : (
        <Badge
          variant="outline"
          className="mt-4 gap-1.5 px-3 py-1 text-xs text-amber-500 border-amber-500/30 bg-amber-500/10"
        >
          Please upload or select a document from the sidebar
        </Badge>
      )}

      {/* Quick Prompt Template Cards - Only Summarize & Key Technical Insights */}
      <div className="mt-8 grid w-full max-w-lg grid-cols-1 sm:grid-cols-2 gap-3 text-left">
        <PromptButton
          icon={<Sparkles size={16} className="text-blue-500" />}
          title="Summarize Document"
          description="Get an executive summary of key takeaways and main points"
          onClick={() =>
            onPrompt("Summarize the key points and main takeaways of this document.")
          }
        />

        <PromptButton
          icon={<Lightbulb size={16} className="text-amber-500" />}
          title="Key Technical Insights"
          description="Identify critical decisions, architecture & insights"
          onClick={() =>
            onPrompt("What are the most important technical insights and architectural decisions?")
          }
        />
      </div>
    </div>
  );
}

function PromptButton({
  icon,
  title,
  description,
  onClick,
}: {
  icon: React.ReactNode;
  title: string;
  description: string;
  onClick: () => void;
}) {
  return (
    <Card
      onClick={onClick}
      className="group cursor-pointer border-border/70 text-left transition-all duration-200 hover:border-primary/50 hover:bg-accent/60 hover:shadow-md active:scale-[0.98]"
    >
      <CardContent className="p-3.5 flex items-start gap-3">
        <div className="mt-0.5 flex h-8 w-8 shrink-0 items-center justify-center rounded-xl bg-muted group-hover:bg-background group-hover:border group-hover:border-primary/20 transition-all">
          {icon}
        </div>
        <div>
          <p className="text-xs font-semibold text-foreground group-hover:text-primary transition-colors">
            {title}
          </p>
          <p className="mt-0.5 text-[10px] text-muted-foreground leading-normal">
            {description}
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
