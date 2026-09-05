import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { cn } from "@/lib/utils";

interface AIResponseRendererProps {
  content: string;
  status?: "streaming" | "complete" | "error";
  className?: string;
}

/**
 * Shared AI Response Renderer Component:
 * Renders both blocking and streaming Markdown AI responses identically.
 * Handles headings, lists, bold/italic, code blocks, blockquotes, links, and tables.
 * Renders a visual streaming indicator when status is 'streaming'.
 */
export function AIResponseRenderer({
  content,
  status,
  className,
}: AIResponseRendererProps) {
  const isStreaming = status === "streaming";

  return (
    <div
      className={cn(
        "markdown-content text-xs leading-relaxed space-y-2 select-text",
        className
      )}
    >
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          // Links: open in new tab securely with subtle styling
          a({ href, children, ...props }) {
            return (
              <a
                href={href}
                target="_blank"
                rel="noopener noreferrer"
                className="text-primary underline underline-offset-2 hover:text-primary/80 transition-colors font-medium"
                {...props}
              >
                {children}
              </a>
            );
          },
          // Code & Fenced Code Blocks
          code({ className: codeClassName, children, ...props }) {
            const isInline = !codeClassName && !String(children).includes("\n");
            if (isInline) {
              return (
                <code
                  className="font-mono text-[11px] bg-muted/80 text-foreground px-1.5 py-0.5 rounded border border-border/50 break-words"
                  {...props}
                >
                  {children}
                </code>
              );
            }
            return (
              <code
                className={cn(
                  "font-mono text-[11px] text-foreground block",
                  codeClassName
                )}
                {...props}
              >
                {children}
              </code>
            );
          },
          pre({ children, ...props }) {
            return (
              <pre
                className="bg-muted/90 border border-border/80 rounded-xl p-3.5 my-2.5 overflow-x-auto text-[11px] font-mono text-foreground leading-normal shadow-xs"
                {...props}
              >
                {children}
              </pre>
            );
          },
          // Markdown Tables with Overflow Wrapper
          table({ children, ...props }) {
            return (
              <div className="my-3 overflow-x-auto rounded-xl border border-border/70 shadow-xs">
                <table
                  className="w-full text-left border-collapse text-xs"
                  {...props}
                >
                  {children}
                </table>
              </div>
            );
          },
          // Blockquotes
          blockquote({ children, ...props }) {
            return (
              <blockquote
                className="border-l-3 border-primary/70 pl-3.5 italic my-2.5 text-muted-foreground bg-muted/20 py-1.5 rounded-r-lg"
                {...props}
              >
                {children}
              </blockquote>
            );
          },
        }}
      >
        {content}
      </ReactMarkdown>

      {/* Streaming Indicator Cursor */}
      {isStreaming && (
        <span
          className="inline-block h-3.5 w-1.5 ml-0.5 bg-primary animate-pulse align-middle rounded-xs"
          title="Streaming response..."
        />
      )}
    </div>
  );
}
