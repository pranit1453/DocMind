export type Source = {
  document: string;
  page?: number;
  chunk?: number;
};

export type MessageRole = "user" | "assistant";

export type Message = {
  id: number;
  role: MessageRole;
  content: string;
  sources?: Source[];
  timestamp?: string;
  executionTime?: string;
};
