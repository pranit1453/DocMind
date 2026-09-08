export type Source = {
  document: string;
  page?: number;
  chunk?: number;
};

export type MessageRole = "user" | "assistant";
export type MessageStatus = "streaming" | "complete" | "error";

export type Message = {
  id: number | string;
  role: MessageRole;
  content: string;
  status?: MessageStatus;
  error?: string;
  sources?: Source[];
  timestamp?: string;
  executionTime?: string;
};
