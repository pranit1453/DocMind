import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Separator } from "@/components/ui/separator";
import { useTheme } from "@/context/ThemeContext";
import {
  Settings,
  Sun,
  Moon,
  Laptop,
  Cpu,
  Check,
  Sparkles,
  Sliders,
} from "lucide-react";

interface SettingsModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

const availableModels = [
  {
    id: "NVIDIA",
    name: "NVIDIA NIM RAG Assistant",
    provider: "NVIDIA AI Foundation Pipeline",
    badge: "Recommended",
  },
];

export function SettingsModal({ open, onOpenChange }: SettingsModalProps) {
  const { theme, setTheme } = useTheme();
  const [selectedModel, setSelectedModel] = useState("NVIDIA");
  const [saved, setSaved] = useState(false);

  const [topK, setTopK] = useState<number>(() => {
    try {
      const val = localStorage.getItem("docmind-topk");
      return val ? Number(val) : 4;
    } catch {
      return 4;
    }
  });

  const [similarityThreshold, setSimilarityThreshold] = useState<number>(() => {
    try {
      const val = localStorage.getItem("docmind-similarity-threshold");
      return val ? Number(val) : 0.7;
    } catch {
      return 0.7;
    }
  });

  const handleSave = () => {
    try {
      localStorage.setItem("docmind-topk", String(topK));
      localStorage.setItem("docmind-similarity-threshold", String(similarityThreshold));
    } catch {}
    setSaved(true);
    setTimeout(() => {
      setSaved(false);
      onOpenChange(false);
    }, 600);
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-lg rounded-2xl p-0 overflow-hidden">
        <DialogHeader className="border-b px-6 py-4">
          <DialogTitle className="flex items-center gap-2 text-base font-bold">
            <Settings size={18} className="text-primary" />
            Workspace & AI Preferences
          </DialogTitle>
        </DialogHeader>

        <div className="p-6 space-y-5 text-xs">
          {/* Theme Section */}
          <div className="space-y-2">
            <label className="font-semibold text-foreground flex items-center gap-2">
              <Sun size={14} className="text-primary" />
              Interface Theme Mode
            </label>
            <div className="grid grid-cols-3 gap-2 pt-1">
              <button
                type="button"
                onClick={() => setTheme("light")}
                className={`flex items-center justify-center gap-2 p-2.5 rounded-xl border transition-all text-xs font-medium cursor-pointer ${
                  theme === "light"
                    ? "border-primary bg-primary/10 text-primary font-semibold ring-1 ring-primary/30"
                    : "border-border bg-card hover:bg-accent text-muted-foreground"
                }`}
              >
                <Sun size={14} className="text-amber-500" />
                <span>Light</span>
              </button>

              <button
                type="button"
                onClick={() => setTheme("dark")}
                className={`flex items-center justify-center gap-2 p-2.5 rounded-xl border transition-all text-xs font-medium cursor-pointer ${
                  theme === "dark"
                    ? "border-primary bg-primary/10 text-primary font-semibold ring-1 ring-primary/30"
                    : "border-border bg-card hover:bg-accent text-muted-foreground"
                }`}
              >
                <Moon size={14} className="text-blue-400" />
                <span>Dark</span>
              </button>

              <button
                type="button"
                onClick={() => setTheme("system")}
                className={`flex items-center justify-center gap-2 p-2.5 rounded-xl border transition-all text-xs font-medium cursor-pointer ${
                  theme === "system"
                    ? "border-primary bg-primary/10 text-primary font-semibold ring-1 ring-primary/30"
                    : "border-border bg-card hover:bg-accent text-muted-foreground"
                }`}
              >
                <Laptop size={14} className="text-muted-foreground" />
                <span>System</span>
              </button>
            </div>
          </div>

          <Separator />

          {/* Model Selection Section */}
          <div className="space-y-3">
            <label className="font-semibold text-foreground flex items-center gap-2">
              <Cpu size={14} className="text-primary" />
              AI Foundation Model Provider
            </label>

            <div className="space-y-2">
              {availableModels.map((model) => {
                const isSelected = selectedModel === model.id;
                return (
                  <div
                    key={model.id}
                    onClick={() => setSelectedModel(model.id)}
                    className={`group cursor-pointer rounded-xl border p-2.5 transition-all flex items-center justify-between ${
                      isSelected
                        ? "border-primary bg-primary/10 shadow-xs ring-1 ring-primary/20"
                        : "border-border bg-card hover:bg-accent/40"
                    }`}
                  >
                    <div className="flex items-center gap-2.5">
                      <div
                        className={`flex h-7 w-7 items-center justify-center rounded-lg ${
                          isSelected
                            ? "bg-primary text-primary-foreground"
                            : "bg-muted text-muted-foreground"
                        }`}
                      >
                        <Sparkles size={13} />
                      </div>
                      <div>
                        <p className="font-semibold text-xs text-foreground group-hover:text-primary transition-colors">
                          {model.name}
                        </p>
                        <p className="text-[10px] text-muted-foreground">
                          {model.provider}
                        </p>
                      </div>
                    </div>

                    <div className="flex items-center gap-2">
                      <Badge
                        variant={isSelected ? "default" : "outline"}
                        className="text-[9px]"
                      >
                        {model.badge}
                      </Badge>
                      {isSelected && <Check size={14} className="text-primary" />}
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <Separator />

          {/* Vector Retrieval Parameters (Top-K and Similarity Threshold 0.0 - 1.0) */}
          <div className="space-y-4">
            <label className="font-semibold text-foreground flex items-center gap-2">
              <Sliders size={14} className="text-primary" />
              Vector Retrieval Parameters
            </label>

            {/* Top-K Chunks */}
            <div className="space-y-1.5 rounded-xl border border-border bg-card p-3">
              <div className="flex items-center justify-between">
                <span className="font-semibold text-xs text-foreground">Top-K Chunks</span>
                <span className="font-mono text-xs text-primary font-bold bg-primary/10 px-2 py-0.5 rounded-md border border-primary/20">
                  {topK}
                </span>
              </div>
              <p className="text-[10px] text-muted-foreground">
                Number of top vector document chunks retrieved for context (1 to 10).
              </p>
              <input
                type="range"
                min="1"
                max="10"
                step="1"
                value={topK}
                onChange={(e) => setTopK(Number(e.target.value))}
                className="w-full h-1.5 bg-muted rounded-lg appearance-none cursor-pointer accent-primary mt-1"
              />
            </div>

            {/* Similarity Threshold (0.0 to 1.0) */}
            <div className="space-y-1.5 rounded-xl border border-border bg-card p-3">
              <div className="flex items-center justify-between">
                <span className="font-semibold text-xs text-foreground">Similarity Threshold</span>
                <span className="font-mono text-xs text-primary font-bold bg-primary/10 px-2 py-0.5 rounded-md border border-primary/20">
                  {similarityThreshold.toFixed(2)}
                </span>
              </div>
              <p className="text-[10px] text-muted-foreground">
                Cosine similarity search score threshold filter from 0.0 to 1.0.
              </p>
              <input
                type="range"
                min="0.0"
                max="1.0"
                step="0.05"
                value={similarityThreshold}
                onChange={(e) => setSimilarityThreshold(Number(e.target.value))}
                className="w-full h-1.5 bg-muted rounded-lg appearance-none cursor-pointer accent-primary mt-1"
              />
            </div>
          </div>
        </div>

        <DialogFooter className="border-t px-6 py-3 bg-muted/10">
          <Button
            variant="outline"
            size="sm"
            onClick={() => onOpenChange(false)}
          >
            Cancel
          </Button>
          <Button size="sm" onClick={handleSave} className="gap-1.5">
            {saved ? (
              <>
                <Check size={14} className="text-emerald-400" />
                <span>Saved</span>
              </>
            ) : (
              <span>Save Preferences</span>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
