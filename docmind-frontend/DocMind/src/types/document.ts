export type DocumentStatus = "Indexed" | "Processing" | "Failed";

export type DocumentType =
  | "pdf"
  | "doc"
  | "docx"
  | "xls"
  | "xlsx"
  | "ppt"
  | "pptx"
  | "html"
  | "htm"
  | "xml"
  | "txt"
  | "md"
  | "csv"
  | "json"
  | string;

export type DocumentItem = {
  id: string; // Valid UUID String matching Spring Boot java.util.UUID
  name: string;
  size: string;
  chunks: number;
  status: DocumentStatus;
  type: DocumentType;
  progress?: number;
};
