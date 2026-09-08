import type { Message } from "@/types/chat";

export function buildMarkdown(messages: Message[]): string {
  return messages
    .map((message) => {
      const title = message.role === "user" ? "## User" : "## Assistant";

      const sources =
        message.sources && message.sources.length > 0
          ? `\n\n### Sources\n\n${message.sources
              .map(
                (source) =>
                  `- **${source.document}**${
                    source.page ? ` — Page ${source.page}` : ""
                  }${source.chunk ? ` — Chunk ${source.chunk}` : ""}`
              )
              .join("\n")}`
          : "";

      return `${title}\n\n${message.content}${sources}`;
    })
    .join("\n\n---\n\n");
}
