import type { DocumentItem } from "@/types/document";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Search, Database, FileText } from "lucide-react";
import { UploadZone } from "./UploadZone";
import { DocumentCard } from "./DocumentCard";

interface KnowledgeSidebarProps {
  documents: DocumentItem[];
  filteredDocuments: DocumentItem[];
  selectedId: string | null;
  onSelectDocument: (id: string) => void;
  onRemoveDocument: (id: string, e: React.MouseEvent) => void;
  search: string;
  onSearchChange: (value: string) => void;
  isDragging: boolean;
  setIsDragging: (dragging: boolean) => void;
  onUpload: (files: FileList | null) => void;
  isUploading?: boolean;
}

export function KnowledgeSidebar({
  documents,
  filteredDocuments,
  selectedId,
  onSelectDocument,
  onRemoveDocument,
  search,
  onSearchChange,
  isDragging,
  setIsDragging,
  onUpload,
  isUploading = false,
}: KnowledgeSidebarProps) {
  return (
    <section className="flex h-full min-h-0 flex-col bg-background/50">
      {/* Panel Header */}
      <div className="flex h-14 shrink-0 items-center justify-between border-b px-5 select-none">
        <div className="flex items-center gap-2">
          <Database size={16} className="text-primary" />
          <div>
            <h2 className="text-xs font-bold tracking-tight text-foreground">
              Knowledge Base
            </h2>
            <p className="text-[10px] text-muted-foreground font-medium">
              Vector Stores & Documents
            </p>
          </div>
        </div>

        <Badge variant="secondary" className="text-[10px] font-semibold">
          {documents.length} {documents.length === 1 ? "doc" : "docs"}
        </Badge>
      </div>

      {/* Main Scrollable Content */}
      <ScrollArea className="min-h-0 flex-1">
        <div className="space-y-4 p-4">
          {/* File Upload Drag & Drop Zone */}
          <UploadZone
            isDragging={isDragging}
            setIsDragging={setIsDragging}
            onUpload={onUpload}
            isUploading={isUploading}
          />

          {/* Filter & Search Bar */}
          <div className="space-y-2">
            <div className="relative">
              <Search
                size={14}
                className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground pointer-events-none"
              />
              <Input
                value={search}
                onChange={(e) => onSearchChange(e.target.value)}
                placeholder="Search documents..."
                className="pl-9 h-9 text-xs bg-card/60"
              />
            </div>
          </div>

          {/* File Heading */}
          <div className="flex items-center justify-between px-1">
            <p className="text-[10px] font-bold uppercase tracking-wider text-muted-foreground">
              Available Files
            </p>
            <span className="text-[10px] text-muted-foreground font-medium">
              {filteredDocuments.length} shown
            </span>
          </div>

          {/* Document Cards List */}
          <div className="space-y-2">
            {filteredDocuments.length === 0 ? (
              <div className="py-8 text-center border border-dashed rounded-xl bg-muted/10">
                <FileText size={24} className="mx-auto text-muted-foreground/40 mb-2" />
                <p className="text-xs font-medium text-muted-foreground">
                  No documents match your search filter
                </p>
              </div>
            ) : (
              filteredDocuments.map((doc) => (
                <DocumentCard
                  key={doc.id}
                  document={doc}
                  selected={doc.id === selectedId}
                  onClick={() => onSelectDocument(doc.id)}
                  onRemove={onRemoveDocument}
                />
              ))
            )}
          </div>
        </div>
      </ScrollArea>
    </section>
  );
}
