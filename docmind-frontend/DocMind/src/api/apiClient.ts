import { getCookie } from "@/utils/cookies";
import { refreshToken as refreshTokenApi } from "./authApi";

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL !== undefined
    ? import.meta.env.VITE_API_BASE_URL
    : "";

/**
 * Returns common headers for public REST endpoints.
 * Includes X-API-Version: v1 header for API versioning.
 */
export function getPublicHeaders(customHeaders: Record<string, string> = {}): Record<string, string> {
  return {
    "X-API-Version": "v1",
    ...customHeaders,
  };
}

/**
 * Returns authorization and versioning headers for protected REST endpoints.
 * Includes X-API-Version: v1 and sanitized Authorization Bearer token header.
 */
export function getProtectedHeaders(customHeaders: Record<string, string> = {}): Record<string, string> {
  const token =
    getCookie("access_token") ||
    getCookie("accessToken") ||
    getCookie("token") ||
    localStorage.getItem("access_token") ||
    localStorage.getItem("accessToken") ||
    localStorage.getItem("token") ||
    localStorage.getItem("docmind-token");

  const headers: Record<string, string> = {
    "X-API-Version": "v1",
    ...customHeaders,
  };

  if (token && token.trim() !== "" && token !== "undefined" && token !== "null") {
    const cleanToken = token.startsWith("Bearer ") ? token : `Bearer ${token}`;
    headers["Authorization"] = cleanToken;
  }

  return headers;
}

let isRefreshingToken = false;
let isTokenExpiredDispatched = false;

export function resetTokenExpiredState() {
  isRefreshingToken = false;
  isTokenExpiredDispatched = false;
}

/**
 * Enterprise Authenticated Fetch Wrapper:
 * Intercepts 401 Unauthorized responses when access token expires or gets revoked.
 * Automatically attempts token refresh via /api/auth/refresh every 14 minutes or on 401.
 * If token is invalid or refresh fails, triggers automatic session logout safely without re-entrant loops.
 */
export async function fetchWithAuth(input: RequestInfo | URL, init?: RequestInit): Promise<Response> {
  const urlStr = String(input);
  const options: RequestInit = {
    ...init,
    headers: {
      ...getProtectedHeaders(),
      ...(init?.headers || {}),
    },
    credentials: "include",
  };

  let response = await fetch(input, options);

  // Exclude auth control endpoints from recursive refresh/expiration triggering
  const isAuthControlEndpoint =
    urlStr.includes("/api/auth/refresh") ||
    urlStr.includes("/api/auth/logout") ||
    urlStr.includes("/api/auth/login") ||
    urlStr.includes("/api/register");

  if (response.status === 403) {
    if (!isTokenExpiredDispatched) {
      isTokenExpiredDispatched = true;
      window.dispatchEvent(
        new CustomEvent("docmind:access-denied", {
          detail: { message: "Access Denied: You do not have permission to access this resource." },
        })
      );
    }
  }

  if (response.status === 401 && !isAuthControlEndpoint && !isRefreshingToken) {
    isRefreshingToken = true;
    try {
      const refreshRes: any = await refreshTokenApi();
      const newAccessToken =
        refreshRes?.accessToken ||
        refreshRes?.token ||
        refreshRes?.data?.accessToken ||
        refreshRes?.data?.token;

      if (newAccessToken) {
        isTokenExpiredDispatched = false;
        const updatedOptions: RequestInit = {
          ...init,
          headers: {
            ...getProtectedHeaders(),
            ...(init?.headers || {}),
          },
          credentials: "include",
        };
        response = await fetch(input, updatedOptions);
      } else {
        if (!isTokenExpiredDispatched) {
          isTokenExpiredDispatched = true;
          window.dispatchEvent(new CustomEvent("docmind:token-expired"));
        }
      }
    } catch {
      if (!isTokenExpiredDispatched) {
        isTokenExpiredDispatched = true;
        window.dispatchEvent(new CustomEvent("docmind:token-expired"));
      }
    } finally {
      isRefreshingToken = false;
    }
  }

  return response;
}

/**
 * Safely parses response JSON body.
 * Prevents "Unexpected end of JSON input" errors when backend returns an empty (0-byte) response or non-JSON body.
 */
export async function safeJsonResponse<T = any>(response: Response, fallback: any = {}): Promise<T> {
  try {
    const text = await response.text();
    if (!text || !text.trim()) {
      return fallback as T;
    }
    return JSON.parse(text) as T;
  } catch {
    return fallback as T;
  }
}
