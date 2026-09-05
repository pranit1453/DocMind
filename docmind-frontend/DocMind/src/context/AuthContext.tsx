import React, { createContext, useContext, useState, useEffect, useRef, useCallback, useMemo } from "react";
import type { UserProfile } from "@/types/user";
import { deleteCookie } from "@/utils/cookies";
import { resetTokenExpiredState } from "@/api/apiClient";
import {
  loginUser as loginUserApi,
  refreshToken as refreshTokenApi,
  logoutUser as logoutUserApi,
  fetchCurrentUserApi,
} from "@/api/authApi";
import { AccessDeniedModal } from "@/components/dialogs/AccessDeniedModal";

interface AuthContextType {
  user: UserProfile | null;
  isAuthenticated: boolean;
  isAdmin: boolean;
  login: (usernameOrEmail: string, pass: string) => Promise<boolean>;
  register: (username: string, email: string, pass: string, fullName?: string) => Promise<boolean>;
  logout: () => void;
  refreshUserProfile: () => Promise<void>;
  accessDeniedOpen: boolean;
  accessDeniedMessage: string;
  confirmAccessDeniedLogout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<UserProfile | null>(() => {
    try {
      const savedUser = localStorage.getItem("docmind-user");
      if (savedUser) {
        const parsed = JSON.parse(savedUser);
        if (parsed && (parsed.username || parsed.email)) {
          if (parsed.email && parsed.email.includes("@workspace.io")) {
            parsed.email = parsed.email.replace("@workspace.io", "@docmind.ai");
          }
          return parsed;
        }
      }
    } catch {}
    return null;
  });

  const isAuthenticated = !!user;
  const lastRefreshTimeRef = useRef<number>(0);

  const isAdmin = useMemo(() => {
    if (!user) return false;
    const username = user.username?.toLowerCase() || "";
    const roleStr = (user.role || user.roleName || "").toUpperCase();
    const rolesArr = (user.roles || []).map((r) => String(r).toUpperCase());
    return (
      username === "admin" ||
      roleStr.includes("ADMIN") ||
      rolesArr.some((r) => r.includes("ADMIN"))
    );
  }, [user]);

  const [accessDeniedOpen, setAccessDeniedOpen] = useState<boolean>(false);
  const [accessDeniedMessage, setAccessDeniedMessage] = useState<string>(
    "Access Denied: Your session has expired or permission was revoked. Please log in again to continue."
  );

  const refreshUserProfile = useCallback(async () => {
    try {
      const meRes = await fetchCurrentUserApi();
      if (meRes) {
        const displayName = meRes.fullName || meRes.username || "";
        const initials = displayName ? displayName.substring(0, 2).toUpperCase() : "US";
        const emailVal = meRes.email || "";
        const roleVal = (meRes as any).roleName || (meRes as any).role || (meRes as any).roles?.[0] || (meRes.username === "admin" ? "ROLE_ADMIN" : "ROLE_USER");

        setUser((prev) => ({
          ...prev,
          userId: String(meRes.userId || prev?.userId || ""),
          username: meRes.username || prev?.username || "",
          fullName: meRes.fullName || meRes.username || prev?.fullName || "",
          email: emailVal || prev?.email || "",
          roleName: roleVal,
          role: roleVal,
          roles: (meRes as any).roles || [roleVal],
          enabled: meRes.enabled ?? true,
          deleted: meRes.deleted ?? false,
          avatarFallback: initials,
        }));
      }
    } catch {
      // User profile fetch failure silently ignored
    }
  }, []);

  const logout = useCallback(() => {
    resetTokenExpiredState();
    logoutUserApi().catch(() => {});
    deleteCookie("access_token");
    deleteCookie("accessToken");
    deleteCookie("token");
    deleteCookie("refresh_token");
    deleteCookie("refreshToken");
    localStorage.removeItem("docmind-user");
    localStorage.removeItem("access_token");
    localStorage.removeItem("accessToken");
    localStorage.removeItem("token");
    localStorage.removeItem("docmind-token");
    localStorage.removeItem("refresh_token");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("docmind-current-view");
    setUser(null);
  }, []);

  const confirmAccessDeniedLogout = useCallback(() => {
    setAccessDeniedOpen(false);
    logout();
  }, [logout]);

  useEffect(() => {
    if (user) {
      localStorage.setItem("docmind-user", JSON.stringify(user));
    } else {
      localStorage.removeItem("docmind-user");
    }
  }, [user]);

  // Sync user profile from GET /api/auth/me whenever authenticated
  useEffect(() => {
    if (isAuthenticated) {
      refreshUserProfile();
    }
  }, [isAuthenticated, refreshUserProfile]);

  // Access Denied / Token Expiration Popup Listener: Shows popup and triggers logout when acknowledged
  useEffect(() => {
    const handleTokenExpired = (e: Event) => {
      const customMsg = (e as CustomEvent)?.detail?.message;
      setAccessDeniedMessage(
        customMsg || "Access Denied: Your session has expired or permission was revoked. Please log in again to continue."
      );
      setAccessDeniedOpen(true);
    };

    window.addEventListener("docmind:token-expired", handleTokenExpired);
    window.addEventListener("docmind:access-denied", handleTokenExpired);
    return () => {
      window.removeEventListener("docmind:token-expired", handleTokenExpired);
      window.removeEventListener("docmind:access-denied", handleTokenExpired);
    };
  }, []);

  // Automated Token Refresh: Hits /api/auth/refresh every 14 minutes to keep session active
  useEffect(() => {
    if (!isAuthenticated) return;

    const FOURTEEN_MINUTES_MS = 14 * 60 * 1000;

    const triggerSilentRefresh = async () => {
      try {
        await refreshTokenApi();
        lastRefreshTimeRef.current = Date.now();
      } catch {
        setAccessDeniedMessage("Session expired during token refresh. Please log in again.");
        setAccessDeniedOpen(true);
      }
    };

    const interval = setInterval(triggerSilentRefresh, FOURTEEN_MINUTES_MS);

    return () => clearInterval(interval);
  }, [isAuthenticated]);

  const login = useCallback(async (usernameOrEmail: string, pass: string): Promise<boolean> => {
    try {
      const res = await loginUserApi({ username: usernameOrEmail, password: pass });

      const finalUsername = res?.username || (usernameOrEmail.includes("@") ? usernameOrEmail.split("@")[0] : usernameOrEmail);
      const finalEmail =
        (res as any)?.email ||
        (usernameOrEmail.includes("@") ? usernameOrEmail : "");
      const finalFullName = (res as any)?.fullName || finalUsername;
      const finalUserId = String((res as any)?.userId || finalUsername);
      const initials = (finalFullName || finalUsername).substring(0, 2).toUpperCase();

      const loggedUser: UserProfile = {
        userId: finalUserId,
        username: finalUsername,
        fullName: finalFullName,
        email: finalEmail,
        enabled: true,
        deleted: false,
        avatarFallback: initials || "US",
      };

      resetTokenExpiredState();
      setAccessDeniedOpen(false);
      setUser(loggedUser);
      lastRefreshTimeRef.current = Date.now();
      
      // Proactively fetch updated user profile from GET /api/auth/me
      refreshUserProfile().catch(() => {});

      return true;
    } catch (err: any) {
      throw new Error(err.message || "Failed to log in.");
    }
  }, [refreshUserProfile]);

  const register = useCallback(async (_username: string, _email: string): Promise<boolean> => {
    return true;
  }, []);

  const contextValue = useMemo(
    () => ({
      user,
      isAuthenticated,
      isAdmin,
      login,
      register,
      logout,
      refreshUserProfile,
      accessDeniedOpen,
      accessDeniedMessage,
      confirmAccessDeniedLogout,
    }),
    [
      user,
      isAuthenticated,
      isAdmin,
      login,
      register,
      logout,
      refreshUserProfile,
      accessDeniedOpen,
      accessDeniedMessage,
      confirmAccessDeniedLogout,
    ]
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
      <AccessDeniedModal
        open={accessDeniedOpen}
        message={accessDeniedMessage}
        onConfirmLogout={confirmAccessDeniedLogout}
      />
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
}
