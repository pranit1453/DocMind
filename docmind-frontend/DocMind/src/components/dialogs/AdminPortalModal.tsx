import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import {
  ShieldAlert,
  Users,
  Search,
  CheckCircle2,
  XCircle,
  RefreshCw,
  UserCheck,
  UserMinus,
  ShieldCheck,
  BookOpen,
  ExternalLink,
  Globe,
  Code2,
  Activity,
} from "lucide-react";
import {
  useAdminUsersQuery,
  useControlUserMutation,
  useAssignRoleMutation,
  useRolesQuery,
} from "@/hooks/useAdmin";
import { useAuth } from "@/context/AuthContext";
import { API_BASE_URL } from "@/api/apiClient";
import type { UserResponse, RoleResponse } from "@/api/types";

interface AdminPortalModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function AdminPortalModal({ open, onOpenChange }: AdminPortalModalProps) {
  const { isAdmin } = useAuth();
  const [activeTab, setActiveTab] = useState<"users" | "swagger_docs">("users");
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(0);
  const pageSize = 10;

  // Role Assignment State
  const [selectedUserForRole, setSelectedUserForRole] = useState<UserResponse | null>(null);
  const [roleIdInput, setRoleIdInput] = useState<number>(2);

  // TanStack Query & Mutations
  const { data: usersData, isLoading: usersLoading, refetch } = useAdminUsersQuery(
    page,
    pageSize,
    search,
    "username",
    "ASC",
    open
  );

  const { data: rolesData } = useRolesQuery(open);

  const controlUserMutation = useControlUserMutation();
  const assignRoleMutation = useAssignRoleMutation();

  const systemRoles: RoleResponse[] = rolesData && rolesData.length > 0
    ? rolesData
    : [
        { roleId: 1, roleName: "ROLE_ADMIN" },
        { roleId: 2, roleName: "ROLE_USER" },
      ];

  const handleToggleAccountStatus = async (userId: string, currentEnabled: boolean) => {
    try {
      await controlUserMutation.mutateAsync({ userId, enabled: !currentEnabled });
    } catch {
      // User control error ignored
    }
  };

  const handleAssignRole = async () => {
    if (!selectedUserForRole) return;
    try {
      await assignRoleMutation.mutateAsync({
        userId: selectedUserForRole.userId,
        roleId: roleIdInput,
      });
      setSelectedUserForRole(null);
      refetch();
    } catch {
      // Assign role error ignored
    }
  };

  const swaggerUrl = `${API_BASE_URL || "http://localhost:8080"}/swagger-ui/index.html#/`;

  const usersList: UserResponse[] = Array.isArray(usersData)
    ? usersData
    : usersData?.content || usersData?.contents || [];

  const totalPages = usersData?.totalPages || 1;
  if (!isAdmin) {
    return (
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent className="max-w-md rounded-2xl p-6 text-center">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-destructive/10 text-destructive border border-destructive/20 mb-3">
            <ShieldAlert size={24} />
          </div>
          <h3 className="text-base font-bold text-foreground">Access Restricted</h3>
          <p className="text-xs text-muted-foreground mt-1">
            The Administrator Management Portal is strictly restricted to administrator accounts.
          </p>
        </DialogContent>
      </Dialog>
    );
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="w-[95vw] max-w-5xl rounded-2xl p-0 overflow-hidden max-h-[90vh] flex flex-col bg-card">
        {/* Header */}
        <DialogHeader className="border-b px-6 py-4 flex flex-row items-center justify-between bg-muted/20">
          <DialogTitle className="flex items-center gap-2 text-lg font-bold text-foreground">
            <ShieldAlert size={20} className="text-primary" />
            DocMind Administrator Management Portal
          </DialogTitle>

          <div className="flex gap-1.5 bg-muted/60 p-1 rounded-xl text-xs font-semibold">
            <button
              type="button"
              onClick={() => setActiveTab("users")}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                activeTab === "users"
                  ? "bg-background text-foreground shadow-xs"
                  : "text-muted-foreground hover:text-foreground"
              }`}
            >
              <Users size={14} />
              User Directory
            </button>
            <button
              type="button"
              onClick={() => setActiveTab("swagger_docs")}
              className={`flex items-center gap-1.5 px-3 py-1.5 rounded-lg transition-all cursor-pointer ${
                activeTab === "swagger_docs"
                  ? "bg-background text-foreground shadow-xs text-primary"
                  : "text-muted-foreground hover:text-foreground"
              }`}
            >
              <BookOpen size={14} />
              Swagger API Docs
            </button>
          </div>
        </DialogHeader>

        {/* Content Body */}
        <div className="flex-1 overflow-hidden p-6 flex flex-col">
          {activeTab === "users" ? (
            <div className="space-y-4 flex flex-col h-full">
              {/* Search Bar & Stats */}
              <div className="flex items-center justify-between gap-4">
                <div className="relative flex-1 max-w-sm">
                  <Search size={14} className="absolute left-3 top-1/2 -translate-y-1/2 text-muted-foreground" />
                  <Input
                    type="text"
                    value={search}
                    onChange={(e) => {
                      setSearch(e.target.value);
                      setPage(0);
                    }}
                    placeholder="Search users by name, email..."
                    className="pl-9 h-9 text-xs"
                  />
                </div>

                <Badge variant="outline" className="text-xs py-1 px-3 bg-primary/5 text-primary border-primary/20">
                  Total Users: {usersData?.totalElements || usersList.length}
                </Badge>
              </div>

              {/* Role Assignment Bar (if user selected) */}
              {selectedUserForRole && (
                <div className="flex items-center justify-between p-3 rounded-xl border border-purple-500/30 bg-purple-500/10 text-xs">
                  <span className="font-semibold text-purple-500 flex items-center gap-1.5">
                    <ShieldCheck size={14} /> Assign Role for {selectedUserForRole.username}
                  </span>
                  <div className="flex items-center gap-2">
                    <select
                      value={roleIdInput}
                      onChange={(e) => setRoleIdInput(Number(e.target.value))}
                      className="h-8 rounded-lg border bg-background px-2 text-xs font-semibold"
                    >
                      {systemRoles.map((r) => (
                        <option key={r.roleId} value={r.roleId}>
                          {r.roleName} (ID: {r.roleId})
                        </option>
                      ))}
                    </select>

                    <Button
                      size="sm"
                      disabled={assignRoleMutation.isPending}
                      onClick={handleAssignRole}
                      className="h-8 text-xs font-bold rounded-lg cursor-pointer"
                    >
                      {assignRoleMutation.isPending ? "Assigning..." : "Confirm Assign"}
                    </Button>

                    <Button
                      size="sm"
                      variant="ghost"
                      onClick={() => setSelectedUserForRole(null)}
                      className="h-8 text-xs rounded-lg"
                    >
                      Cancel
                    </Button>
                  </div>
                </div>
              )}

              {/* Users Table */}
              <ScrollArea className="flex-1 border rounded-xl bg-background/50">
                {usersLoading ? (
                  <div className="p-8 text-center text-xs text-muted-foreground flex items-center justify-center gap-2">
                    <RefreshCw size={16} className="animate-spin text-primary" />
                    <span>Loading User Directory...</span>
                  </div>
                ) : usersList.length === 0 ? (
                  <div className="p-8 text-center text-xs text-muted-foreground">
                    No users found matching your search.
                  </div>
                ) : (
                  <table className="w-full text-xs text-left">
                    <thead className="border-b bg-muted/40 text-[11px] font-semibold text-muted-foreground uppercase">
                      <tr>
                        <th className="p-3 pl-4">User</th>
                        <th className="p-3">Email</th>
                        <th className="p-3">Role</th>
                        <th className="p-3">Account Status</th>
                        <th className="p-3 text-right pr-4">Actions</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-border/60">
                      {usersList.map((usr: UserResponse) => (
                        <tr key={usr.userId} className="hover:bg-muted/20 transition-colors">
                          <td className="p-3 pl-4 font-semibold text-foreground flex items-center gap-2">
                            <div className="h-7 w-7 rounded-full bg-primary/10 text-primary flex items-center justify-center font-bold text-[10px]">
                              {usr.username.substring(0, 2).toUpperCase()}
                            </div>
                            <span>{usr.username}</span>
                          </td>
                          <td className="p-3 text-muted-foreground">{usr.email || "N/A"}</td>
                          <td className="p-3">
                            <Badge
                              variant="secondary"
                              className={
                                String(usr.roleName).toUpperCase().includes("ADMIN")
                                  ? "bg-purple-500/10 text-purple-500 border-purple-500/20"
                                  : "bg-blue-500/10 text-blue-500 border-blue-500/20"
                              }
                            >
                              {usr.roleName || "USER"}
                            </Badge>
                          </td>
                          <td className="p-3">
                            {usr.enabled ? (
                              <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-500">
                                <CheckCircle2 size={13} /> Active
                              </span>
                            ) : (
                              <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-destructive">
                                <XCircle size={13} /> Disabled
                              </span>
                            )}
                          </td>
                          <td className="p-3 text-right pr-4 space-x-1.5">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => setSelectedUserForRole(usr)}
                              className="h-7 text-[11px] rounded-lg cursor-pointer"
                            >
                              Manage Role
                            </Button>

                            <Button
                              size="sm"
                              variant={usr.enabled ? "outline" : "default"}
                              disabled={controlUserMutation.isPending}
                              onClick={() => handleToggleAccountStatus(usr.userId, usr.enabled)}
                              className="h-7 text-[11px] rounded-lg cursor-pointer gap-1"
                            >
                              {usr.enabled ? (
                                <>
                                  <UserMinus size={12} className="text-destructive" />
                                  <span>Disable</span>
                                </>
                              ) : (
                                <>
                                  <UserCheck size={12} className="text-emerald-500" />
                                  <span>Enable</span>
                                </>
                              )}
                            </Button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
              </ScrollArea>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex items-center justify-between text-xs pt-2">
                  <span className="text-muted-foreground">
                    Page {page + 1} of {totalPages}
                  </span>
                  <div className="flex gap-1">
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={page === 0}
                      onClick={() => setPage((p) => Math.max(0, p - 1))}
                      className="h-7 text-xs rounded-lg"
                    >
                      Previous
                    </Button>
                    <Button
                      size="sm"
                      variant="outline"
                      disabled={page >= totalPages - 1}
                      onClick={() => setPage((p) => p + 1)}
                      className="h-7 text-xs rounded-lg"
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </div>
          ) : (
            /* Swagger API Documentation Tab (Vertical Column Stack inside fixed max-w-5xl dialog) */
            <div className="flex-1 flex flex-col items-center justify-center py-12 px-4 border rounded-xl bg-background/50">
              <div className="flex flex-col items-center gap-3.5 w-full max-w-sm mx-auto">
                <a
                  href={swaggerUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="w-full flex items-center justify-between gap-3 px-5 py-2.5 rounded-full border border-border bg-card hover:bg-accent text-xs font-semibold text-foreground transition-all shadow-xs cursor-pointer hover:border-primary/50"
                >
                  <div className="flex items-center gap-2.5">
                    <Globe size={15} className="text-foreground shrink-0" />
                    <span>Swagger UI Console</span>
                  </div>
                  <ExternalLink size={13} className="text-muted-foreground shrink-0" />
                </a>

                <a
                  href={`${API_BASE_URL || "http://localhost:8080"}/v3/api-docs`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="w-full flex items-center justify-between gap-3 px-5 py-2.5 rounded-full border border-border bg-card hover:bg-accent text-xs font-semibold text-foreground transition-all shadow-xs cursor-pointer hover:border-purple-500/50"
                >
                  <div className="flex items-center gap-2.5">
                    <Code2 size={15} className="text-purple-400 shrink-0" />
                    <span>OpenAPI JSON Spec</span>
                  </div>
                  <ExternalLink size={13} className="text-muted-foreground shrink-0" />
                </a>

                <a
                  href={`${API_BASE_URL || "http://localhost:8080"}/actuator/health`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="w-full flex items-center justify-between gap-3 px-5 py-2.5 rounded-full border border-border bg-card hover:bg-accent text-xs font-semibold text-foreground transition-all shadow-xs cursor-pointer hover:border-emerald-500/50"
                >
                  <div className="flex items-center gap-2.5">
                    <Activity size={15} className="text-emerald-400 shrink-0" />
                    <span>Actuator Health Check</span>
                  </div>
                  <ExternalLink size={13} className="text-muted-foreground shrink-0" />
                </a>
              </div>
            </div>
          )}
        </div>
      </DialogContent>
    </Dialog>
  );
}


