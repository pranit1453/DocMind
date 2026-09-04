export interface SignupRequest {
  fullName: string;
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface SignupResponse {
  challengeId?: string;
  message: string;
}

export interface VerifyOtpRequest {
  challengeId: string;
  otp: string;
}

export interface VerificationResponse {
  message: string;
}

export interface ForgotPasswordEmail {
  email: string;
}

export interface PasswordResponse {
  challengeId?: string;
  message: string;
}

export interface PasswordRequest {
  password: string;
  confirmPassword: string;
}

export interface ChangeForgotPasswordRequest {
  email: string;
  passwordRequest: PasswordRequest;
}

export interface ChangePassword {
  oldPassword: string;
  request: PasswordRequest;
}

export interface ChangePasswordResponse {
  message: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  username?: string;
  roles?: string[];
  message?: string;
  tokenType?: string;
  accessToken?: string;
  refreshToken?: string;
}

export interface TokenResponse {
  message: string;
}

export interface ApiResponse<T> {
  status: boolean;
  message: string;
  data: T;
  timestamp?: string;
}

export type FileStatus = "PROCESSING" | "INDEXED" | "FAILED" | "Indexed" | "Processing" | "Failed";

export interface DocumentResponse {
  documentId: string;
  fileName: string;
  fileSize: number;
  status: FileStatus;
  chunksCreated: number;
  createdAt: string;
}

export interface ScrollResponseDocumentResponse {
  hasPrevious?: boolean;
  prevScrollId?: string;
  contents: DocumentResponse[];
  hasNext?: boolean;
  nextScrollId?: string;
  pageSize?: number;
}

export interface PageResponse<T> {
  content?: T[];
  contents?: T[];
  page?: number;
  currentPage?: number;
  size?: number;
  pageSize?: number;
  totalElements?: number;
  totalPages?: number;
  last?: boolean;
  isLastPage?: boolean;
  first?: boolean;
  isFirstPage?: boolean;
}

export type Provider = "NVIDIA" | "OPENAI" | "ANTHROPIC" | "GOOGLE";

export interface RetrievalOptions {
  topK?: number;
  similarityThreshold?: number;
}

export interface ChatQueryRequest {
  query: string;
  provider: Provider;
  options?: RetrievalOptions;
}

export interface QueryResponse {
  content?: string;
  message?: string;
  answer?: string;
  documentId?: string;
  conversationId?: string;
  provider?: Provider;
  executionTime?: string;
  sources?: Array<{
    document: string;
    page?: number;
    chunk?: number;
  }>;
}

export interface UserAccountControlRequest {
  enabled: boolean;
}

export interface AssignUserRoleRequest {
  userId: string;
  roleId: number;
}

export interface RevokeUserRoleRequest {
  userId: string;
  roleId: number;
}

export interface UserResponse {
  userId: string;
  username: string;
  roleName?: string;
  email?: string;
  enabled: boolean;
  deleted?: boolean;
  status?: string;
}

export interface RoleResponses {
  roleId: number;
  roleName: string;
  roleDescription?: string;
  status?: string;
}

export interface UserRoleResponse {
  userRoleId: number;
  userId: string;
  username: string;
  roles?: RoleResponses[];
}

export interface RoleResponse {
  roleId: number;
  roleName: string;
  roleDescription?: string;
  status?: string;
}
