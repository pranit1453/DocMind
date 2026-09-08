import type { DocumentItem } from "@/types/document";
import { Badge } from "@/components/ui/badge";
import { FileText, Layers, CheckCircle2 } from "lucide-react";

interface ContextBarProps {
  selectedDocument?: DocumentItem;
}

export function ContextBar({ selectedDocument }: ContextBarProps) {
  if (!selectedDocument) return null;

  return (
    <div className="shrink-0 border-b bg-muted/30 dark:bg-muted/15 px-5 py-2 select-none transition-colors">
      <div className="flex items-center gap-2 text-xs">
        <span className="relative flex h-2 w-2">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
        </span>

        <span className="text-[11px] font-semibold text-muted-foreground uppercase tracking-wider">
          Active Context
        </span>

        <span className="text-muted-foreground/40 font-light">/</span>

        <FileText size={13} className="text-primary shrink-0" />

        <span className="max-w-[280px] sm:max-w-[400px] truncate text-xs font-semibold text-foreground">
          {selectedDocument.name}
        </span>

        <div className="ml-auto flex items-center gap-2">
          <Badge
            variant="outline"
            className="text-[9px] gap-1 font-medium bg-background border-border"
          >
            <Layers size={10} className="text-muted-foreground" />
            {selectedDocument.chunks} vector chunks
          </Badge>
          <Badge
            variant="success"
            className="hidden sm:inline-flex text-[9px] gap-1"
          >
            <CheckCircle2 size={10} /> Grounded
          </Badge>
        </div>
      </div>
    </div>
  );
}
