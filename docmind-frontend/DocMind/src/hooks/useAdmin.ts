import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  fetchAllUsersApi,
  controlUserAccountApi,
  assignUserRoleApi,
  revokeUserRoleApi,
  reassignUserRoleApi,
  fetchRolesApi,
} from "@/api/adminApi";
import type { PageResponse, UserResponse, RoleResponse } from "@/api/types";

export const ADMIN_QUERY_KEYS = {
  all: ["admin"] as const,
  users: (page?: number, size?: number, keyword?: string, sortBy?: string, sortDirection?: string) =>
    [...ADMIN_QUERY_KEYS.all, "users", { page, size, keyword, sortBy, sortDirection }] as const,
  roles: () => [...ADMIN_QUERY_KEYS.all, "roles"] as const,
};

export function useAdminUsersQuery(
  page = 0,
  size = 10,
  keyword?: string,
  sortBy = "username",
  sortDirection = "ASC",
  enabled = true
) {
  return useQuery<PageResponse<UserResponse>, Error>({
    queryKey: ADMIN_QUERY_KEYS.users(page, size, keyword, sortBy, sortDirection),
    queryFn: () => fetchAllUsersApi(page, size, keyword, sortBy, sortDirection),
    enabled,
    staleTime: 1000 * 15,
    refetchOnWindowFocus: false,
  });
}

export function useRolesQuery(enabled = true) {
  return useQuery<RoleResponse[], Error>({
    queryKey: ADMIN_QUERY_KEYS.roles(),
    queryFn: fetchRolesApi,
    enabled,
    staleTime: 1000 * 60 * 5,
    refetchOnWindowFocus: false,
  });
}

export function useControlUserMutation() {
  const queryClient = useQueryClient();

  return useMutation<any, Error, { userId: string; enabled: boolean }>({
    mutationFn: ({ userId, enabled }) => controlUserAccountApi(userId, enabled),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADMIN_QUERY_KEYS.all });
    },
  });
}

export function useAssignRoleMutation() {
  const queryClient = useQueryClient();

  return useMutation<any, Error, { userId: string; roleId: number }>({
    mutationFn: ({ userId, roleId }) => assignUserRoleApi(userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADMIN_QUERY_KEYS.all });
    },
  });
}

export function useRevokeRoleMutation() {
  const queryClient = useQueryClient();

  return useMutation<any, Error, { userRoleId: number; userId: string; roleId: number }>({
    mutationFn: ({ userRoleId, userId, roleId }) => revokeUserRoleApi(userRoleId, userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADMIN_QUERY_KEYS.all });
    },
  });
}

export function useReassignRoleMutation() {
  const queryClient = useQueryClient();

  return useMutation<any, Error, { userRoleId: number; userId: string; roleId: number }>({
    mutationFn: ({ userRoleId, userId, roleId }) => reassignUserRoleApi(userRoleId, userId, roleId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ADMIN_QUERY_KEYS.all });
    },
  });
}
