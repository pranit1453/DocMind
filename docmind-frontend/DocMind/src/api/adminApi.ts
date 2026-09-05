import type {
  PageResponse,
  UserResponse,
  ApiResponse,
  UserRoleResponse,
  RoleResponse,
} from "./types";
import { API_BASE_URL, fetchWithAuth, safeJsonResponse } from "./apiClient";

/**
 * PROTECTED ADMIN ENDPOINT: GET /api/admin/portal/all/users
 * Header: X-API-Version: v1
 * Fetches paginated directory of users.
 */
export async function fetchAllUsersApi(
  page = 0,
  size = 10,
  keyword?: string,
  sortBy = "username",
  sortDirection = "ASC"
): Promise<PageResponse<UserResponse>> {
  const params = new URLSearchParams({
    page: String(page),
    size: String(size),
    sortBy,
    sortDirection,
  });
  if (keyword) params.append("keyword", keyword);

  const response = await fetchWithAuth(
    `${API_BASE_URL}/api/admin/portal/all/users?${params.toString()}`,
    { method: "GET" }
  );

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to fetch users (${response.status})`);
  }

  return await safeJsonResponse(response, { content: [], totalPages: 1 });
}

/**
 * PROTECTED ADMIN ENDPOINT: POST /api/admin/portal/user/{userId}/control
 * Header: X-API-Version: v1
 * Enables or disables user account access.
 */
export async function controlUserAccountApi(
  userId: string,
  enabled: boolean
): Promise<ApiResponse<void>> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/admin/portal/user/${userId}/control`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ enabled }),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to update user status (${response.status})`);
  }

  return await safeJsonResponse(response, { status: "SUCCESS" });
}

/**
 * PROTECTED ADMIN ENDPOINT: POST /api/admin/portal/assign
 * Header: X-API-Version: v1
 * Assigns a role to a user.
 */
export async function assignUserRoleApi(
  userId: string,
  roleId: number
): Promise<{ status?: boolean; message?: string }> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/admin/portal/assign`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, roleId }),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to assign role (${response.status})`);
  }

  return await safeJsonResponse(response, { status: true });
}

/**
 * PROTECTED ADMIN ENDPOINT: PATCH /api/admin/portal/{userRoleId}/revoke
 * Header: X-API-Version: v1
 * Revokes a role from a user.
 */
export async function revokeUserRoleApi(
  userRoleId: number,
  userId: string,
  roleId: number
): Promise<{ status?: boolean; message?: string }> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/admin/portal/${userRoleId}/revoke`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, roleId }),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to revoke role (${response.status})`);
  }

  return await safeJsonResponse(response, { status: true });
}

/**
 * PROTECTED ADMIN ENDPOINT: PATCH /api/admin/portal/{userRoleId}/reassign
 * Header: X-API-Version: v1
 * Reassigns a user role.
 */
export async function reassignUserRoleApi(
  userRoleId: number,
  userId: string,
  roleId: number
): Promise<{ status?: boolean; message?: string }> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/admin/portal/${userRoleId}/reassign`, {
    method: "PATCH",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ userId, roleId }),
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to reassign role (${response.status})`);
  }

  return await safeJsonResponse(response, { status: true });
}

/**
 * PROTECTED ADMIN ENDPOINT: GET /api/admin/portal/{userRoleId}
 * Header: X-API-Version: v1
 * Fetches role assignment details by ID.
 */
export async function fetchUserRoleByIdApi(userRoleId?: number): Promise<UserRoleResponse> {
  const id = userRoleId || 1;
  const response = await fetchWithAuth(`${API_BASE_URL}/api/admin/portal/${id}`, {
    method: "GET",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, {});
    throw new Error(errorData.message || `Failed to fetch role details (${response.status})`);
  }

  return await safeJsonResponse(response, {});
}

/**
 * PROTECTED ENDPOINT: GET /api/roles
 * Header: X-API-Version: v1
 * Fetches list of system roles from backend database.
 */
export async function fetchRolesApi(): Promise<RoleResponse[]> {
  const response = await fetchWithAuth(`${API_BASE_URL}/api/roles`, {
    method: "GET",
  });

  if (!response.ok) {
    const errorData = await safeJsonResponse(response, []);
    throw new Error(errorData.message || `Failed to fetch roles (${response.status})`);
  }

  const resData = await safeJsonResponse(response, []);
  if (Array.isArray(resData)) return resData;
  if (Array.isArray(resData?.data)) return resData.data;
  if (Array.isArray(resData?.content)) return resData.content;
  if (Array.isArray(resData?.roles)) return resData.roles;
  return [];
}
