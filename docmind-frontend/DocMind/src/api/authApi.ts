import type {
  SignupRequest,
  SignupResponse,
  VerifyOtpRequest,
  VerificationResponse,
  ForgotPasswordEmail,
  PasswordResponse,
  PasswordRequest,
  ChangePassword,
  ChangePasswordResponse,
  LoginRequest,
  LoginResponse,
  TokenResponse,
} from "./types";
import { API_BASE_URL, getPublicHeaders, getProtectedHeaders, fetchWithAuth, safeJsonResponse } from "./apiClient";
import { setCookie, deleteCookie, getCookie } from "@/utils/cookies";

/**
 * PUBLIC ENDPOINT: POST /api/register
 * Header: X-API-Version: v1
 * Initiates user registration and sends OTP code to user email.
 */
export async function registerUser(request: SignupRequest): Promise<SignupResponse> {
  const response = await fetch(`${API_BASE_URL}/api/register`, {
    method: "POST",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Registration failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PROTECTED ENDPOINT: POST /api/register/admin
 * Header: X-API-Version: v1
 * Initiates administrator registration.
 */
export async function registerAdminUser(request: SignupRequest): Promise<SignupResponse> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/register/admin`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Admin registration failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PUBLIC ENDPOINT: POST /api/verify
 * Header: X-API-Version: v1
 * Validates registration OTP code and activates the user account.
 */
export async function verifyRegistrationOtp(request: VerifyOtpRequest): Promise<VerificationResponse> {
  const response = await fetch(`${API_BASE_URL}/api/verify`, {
    method: "POST",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `OTP verification failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PUBLIC ENDPOINT: POST /api/verify/reset
 * Header: X-API-Version: v1
 * Validates password reset OTP code.
 */
export async function verifyPasswordResetOtp(request: VerifyOtpRequest): Promise<VerificationResponse> {
  const response = await fetch(`${API_BASE_URL}/api/verify/reset`, {
    method: "POST",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Password reset OTP verification failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PUBLIC ENDPOINT: POST /api/password/reset/request
 * Header: X-API-Version: v1
 * Requests password reset OTP code sent to user email.
 */
export async function requestPasswordReset(request: ForgotPasswordEmail): Promise<PasswordResponse> {
  const response = await fetch(`${API_BASE_URL}/api/password/reset/request`, {
    method: "POST",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Password reset request failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PUBLIC ENDPOINT: PATCH /api/password/reset
 * Header: X-API-Version: v1
 * Resets forgotten password with challengeId/OTP verification.
 */
export async function resetPassword(email: string, passwordRequest: PasswordRequest): Promise<VerificationResponse> {
  const response = await fetch(`${API_BASE_URL}/api/password/reset`, {
    method: "PATCH",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify({ email, passwordRequest }),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Password reset failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PROTECTED ENDPOINT: PATCH /api/password/change
 * Header: X-API-Version: v1
 * Updates password for an authenticated user.
 */
export async function changePassword(request: ChangePassword): Promise<ChangePasswordResponse> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/password/change`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Password change failed (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PUBLIC ENDPOINT: POST /api/auth/login
 * Header: X-API-Version: v1
 * Authenticates user credentials and sets access and refresh tokens in HTTP-only cookies & local storage.
 */
export async function loginUser(request: LoginRequest): Promise<LoginResponse> {
  const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
    method: "POST",
    headers: getPublicHeaders({ "Content-Type": "application/json" }),
    credentials: "include",
    body: JSON.stringify(request),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Invalid username or password (${response.status})`);
  }

  const resData: any = await safeJsonResponse(response, {});
  const token =
    resData?.accessToken ||
    resData?.token ||
    resData?.jwt ||
    resData?.data?.accessToken ||
    resData?.data?.token ||
    resData?.data?.jwt;

  const refreshTokenVal =
    resData?.refreshToken ||
    resData?.data?.refreshToken;

  if (token) {
    setCookie("access_token", token, 7);
    setCookie("accessToken", token, 7);
    setCookie("token", token, 7);
    localStorage.setItem("access_token", token);
    localStorage.setItem("accessToken", token);
    localStorage.setItem("token", token);
    localStorage.setItem("docmind-token", token);
  }

  if (refreshTokenVal) {
    setCookie("refresh_token", refreshTokenVal, 14);
    setCookie("refreshToken", refreshTokenVal, 14);
    localStorage.setItem("refresh_token", refreshTokenVal);
    localStorage.setItem("refreshToken", refreshTokenVal);
  }

  return resData;
}

/**
 * PROTECTED ENDPOINT: POST /api/auth/refresh
 * Header: X-API-Version: v1
 * Refreshes access token in HTTP-only cookies every 14 minutes to keep session active.
 */
export async function refreshToken(): Promise<TokenResponse> {
  const rfToken =
    getCookie("refresh_token") ||
    getCookie("refreshToken") ||
    localStorage.getItem("refresh_token") ||
    localStorage.getItem("refreshToken");

  const headers = getPublicHeaders({ "Content-Type": "application/json" });
  if (rfToken) {
    headers["X-Refresh-Token"] = rfToken;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: "POST",
      headers,
      credentials: "include",
      body: rfToken ? JSON.stringify({ refreshToken: rfToken, token: rfToken }) : undefined,
    });

    if (!response.ok) {
      throw new Error(`Token refresh failed (${response.status})`);
    }

    const resData: any = await safeJsonResponse(response, {});
    const newToken =
      resData?.accessToken ||
      resData?.token ||
      resData?.jwt ||
      resData?.data?.accessToken ||
      resData?.data?.token;

    const newRefreshToken =
      resData?.refreshToken ||
      resData?.data?.refreshToken;

    if (newToken) {
      setCookie("access_token", newToken, 7);
      setCookie("accessToken", newToken, 7);
      localStorage.setItem("access_token", newToken);
      localStorage.setItem("accessToken", newToken);
      localStorage.setItem("docmind-token", newToken);
    }

    if (newRefreshToken) {
      setCookie("refresh_token", newRefreshToken, 14);
      setCookie("refreshToken", newRefreshToken, 14);
      localStorage.setItem("refresh_token", newRefreshToken);
      localStorage.setItem("refreshToken", newRefreshToken);
    }

    return resData;
  } catch (err: any) {
    throw new Error(err.message || "Token refresh exception");
  }
}

/**
 * PROTECTED ENDPOINT: POST /api/auth/logout
 * Header: X-API-Version: v1
 * Logs out user and clears cookies & storage.
 */
export async function logoutUser(): Promise<string> {
  const rfToken =
    getCookie("refresh_token") ||
    getCookie("refreshToken") ||
    localStorage.getItem("refresh_token") ||
    localStorage.getItem("refreshToken") ||
    "";

  const customHeaders: Record<string, string> = {};
  if (rfToken) {
    customHeaders["X-Refresh-Token"] = rfToken;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: "POST",
      headers: getProtectedHeaders(customHeaders),
      credentials: "include",
    });

    deleteCookie("access_token");
    deleteCookie("accessToken");
    deleteCookie("token");
    deleteCookie("refresh_token");
    deleteCookie("refreshToken");
    localStorage.removeItem("docmind-token");
    localStorage.removeItem("access_token");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("token");
    localStorage.removeItem("refresh_token");
    localStorage.removeItem("refreshToken");

    if (response.ok) {
      const data = await safeJsonResponse(response, {});
      return data.message || "Logged out successfully";
    }
  } catch {
    deleteCookie("access_token");
    deleteCookie("accessToken");
    deleteCookie("token");
    deleteCookie("refresh_token");
    deleteCookie("refreshToken");
  }
  return "Logged out";
}

/**
 * PROTECTED ENDPOINT: POST /api/account/deactivate
 * Header: X-API-Version: v1
 * Deactivates user account.
 */
export async function deactivateAccount(): Promise<VerificationResponse> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/account/deactivate`, {
    method: "POST",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Account deactivation failed (${response.status})`);
  }

  deleteCookie("access_token");
  deleteCookie("accessToken");
  deleteCookie("token");
  deleteCookie("refresh_token");
  deleteCookie("refreshToken");
  localStorage.removeItem("docmind-user");
  localStorage.removeItem("access_token");

  const resData = await safeJsonResponse(response, {});
  return {
    status: "SUCCESS",
    message: resData.message || "Account deactivated successfully.",
    ...resData,
  };
}

/**
 * PROTECTED ENDPOINT: GET /api/auth/me
 * Header: X-API-Version: v1
 * Fetches current authenticated UserResponse record from Spring Boot backend.
 * Response format: { userId, username, fullName, email, enabled, deleted }
 */
export async function fetchCurrentUserApi(): Promise<{
  userId: string;
  username: string;
  fullName?: string;
  email: string;
  enabled: boolean;
  deleted?: boolean;
}> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/auth/me`, {
    method: "GET",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to fetch user profile (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}
