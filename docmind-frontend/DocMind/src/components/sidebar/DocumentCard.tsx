import React from "react";
import type { DocumentItem } from "@/types/document";
import { Card, CardContent } from "@/components/ui/card";
import { Progress } from "@/components/ui/progress";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  FileText,
  FileCode2,
  File,
  MoreHorizontal,
  Trash2,
  CheckCircle2,
  Clock,
  AlertCircle,
} from "lucide-react";
import { cn } from "@/lib/utils";

interface DocumentCardProps {
  document: DocumentItem;
  selected: boolean;
  onClick: () => void;
  onRemove: (id: string, e: React.MouseEvent) => void;
}

export function DocumentCard({
  document,
  selected,
  onClick,
  onRemove,
}: DocumentCardProps) {
  return (
    <Card
      onClick={onClick}
      className={cn(
        "group relative cursor-pointer border transition-all duration-200 hover:shadow-sm",
        selected
          ? "border-primary/40 bg-accent/60 dark:bg-accent/40 shadow-xs ring-1 ring-primary/20"
          : "border-border/60 bg-card hover:bg-accent/30 hover:border-border"
      )}
    >
      <CardContent className="p-3">
        <div className="flex items-start gap-3">
          {/* Document Icon */}
          <div
            className={cn(
              "mt-0.5 flex h-9 w-9 shrink-0 items-center justify-center rounded-xl transition-colors",
              selected
                ? "bg-primary text-primary-foreground shadow-xs"
                : "bg-muted text-muted-foreground group-hover:bg-accent group-hover:text-foreground"
            )}
          >
            {getDocIcon(document.type)}
          </div>

          {/* Info & Details */}
          <div className="min-w-0 flex-1">
            <div className="flex items-center justify-between gap-1">
              <p className="truncate text-xs font-semibold text-foreground group-hover:text-primary transition-colors">
                {document.name}
              </p>

              {/* Document Options Menu */}
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button
                    variant="ghost"
                    size="icon"
                    className="h-6 w-6 shrink-0 text-muted-foreground opacity-0 group-hover:opacity-100 hover:text-foreground transition-opacity"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <MoreHorizontal size={13} />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end" className="w-36">
                  <DropdownMenuItem
                    className="text-destructive focus:text-destructive focus:bg-destructive/10 text-xs gap-2"
                    onClick={(e) => onRemove(document.id, e)}
                  >
                    <Trash2 size={13} />
                    <span>Delete document</span>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>

            <div className="mt-1 flex items-center gap-2 text-[10px] text-muted-foreground font-medium">
              <span>{document.size}</span>
              <span>•</span>
              <span>{document.chunks} chunks</span>
            </div>

            {/* Status Indicator / Progress */}
            {document.status === "Processing" ? (
              <div className="mt-2 space-y-1">
                <div className="flex items-center justify-between text-[9px] text-muted-foreground">
                  <span className="flex items-center gap-1 font-medium text-amber-500">
                    <Clock size={10} className="animate-spin" /> Indexing...
                  </span>
                  <span>{document.progress ?? 0}%</span>
                </div>
                <Progress value={document.progress ?? 0} className="h-1" />
              </div>
            ) : (
              <div className="mt-2 flex items-center gap-1.5">
                {document.status === "Indexed" ? (
                  <CheckCircle2 size={11} className="text-emerald-500" />
                ) : (
                  <AlertCircle size={11} className="text-destructive" />
                )}
                <span
                  className={cn(
                    "text-[9px] font-medium tracking-wide",
                    document.status === "Indexed"
                      ? "text-emerald-600 dark:text-emerald-400"
                      : "text-destructive"
                  )}
                >
                  {document.status}
                </span>
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}

function getDocIcon(type: string) {
  switch (type.toLowerCase()) {
    case "md":
    case "markdown":
    case "json":
    case "xml":
    case "html":
    case "htm":
      return <FileCode2 size={16} />;
    case "txt":
    case "pdf":
    case "doc":
    case "docx":
    case "xls":
    case "xlsx":
    case "ppt":
    case "pptx":
    case "csv":
      return <FileText size={16} />;
    default:
      return <File size={16} />;
  }
}
