import { useRef } from "react";
import { Card, CardContent } from "@/components/ui/card";
import { Upload, Loader2 } from "lucide-react";
import { cn } from "@/lib/utils";

interface UploadZoneProps {
  isDragging: boolean;
  setIsDragging: (dragging: boolean) => void;
  onUpload: (files: FileList | null) => void;
  isUploading?: boolean;
}

export function UploadZone({
  isDragging,
  setIsDragging,
  onUpload,
  isUploading = false,
}: UploadZoneProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);

  return (
    <>
      <input
        ref={fileInputRef}
        type="file"
        multiple
        disabled={isUploading}
        accept=".pdf,.doc,.docx,.txt,.md,.csv,.json"
        className="hidden"
        onChange={(e) => {
          onUpload(e.target.files);
          if (fileInputRef.current) {
            fileInputRef.current.value = "";
          }
        }}
      />

      <Card
        onDragOver={(e) => {
          e.preventDefault();
          if (!isUploading) setIsDragging(true);
        }}
        onDragLeave={() => setIsDragging(false)}
        onDrop={(e) => {
          e.preventDefault();
          setIsDragging(false);
          if (!isUploading) onUpload(e.dataTransfer.files);
        }}
        onClick={() => {
          if (!isUploading) fileInputRef.current?.click();
        }}
        className={cn(
          "group cursor-pointer border-dashed transition-all duration-200 hover:border-primary/50 hover:bg-primary/5",
          isDragging
            ? "border-primary bg-primary/10 ring-2 ring-primary/20 scale-[0.99]"
            : "border-border/80 bg-muted/20",
          isUploading && "border-amber-500/40 bg-amber-500/5 cursor-wait"
        )}
      >
        <CardContent className="flex h-24 flex-col items-center justify-center p-4 text-center">
          <div
            className={cn(
              "mb-2 flex h-9 w-9 items-center justify-center rounded-xl bg-background border shadow-xs transition-transform",
              !isUploading && "group-hover:scale-110 group-hover:border-primary/30",
              isUploading && "border-amber-500/30"
            )}
          >
            {isUploading ? (
              <Loader2 size={16} className="text-amber-500 animate-spin" />
            ) : (
              <Upload size={16} className="text-muted-foreground group-hover:text-primary transition-colors" />
            )}
          </div>

          <p className={cn("text-xs font-semibold", isUploading ? "text-amber-500" : "text-foreground")}>
            {isUploading
              ? "Document is uploading and indexing..."
              : isDragging
              ? "Release files to upload"
              : "Upload Knowledge Source"}
          </p>

          <p className="mt-0.5 text-[10px] text-muted-foreground">
            {isUploading
              ? "Generating vector embeddings for search..."
              : "Drag & drop or click to browse files"}
          </p>

          <p className="mt-1 text-[9px] font-medium text-muted-foreground/70 tracking-wider">
            {isUploading ? "Vector Store Indexing Active" : "PDF, DOCX, TXT, MD, CSV, JSON (Max 25MB)"}
          </p>
        </CardContent>
      </Card>
    </>
  );
}
