import { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { useAuth } from "@/context/AuthContext";
import { fetchCurrentUserApi, deactivateAccount } from "@/api/authApi";
import { User, Mail, Hash, IdCard, Loader2, UserX, AlertTriangle, Check } from "lucide-react";
import type { UserProfile } from "@/types/user";

interface ProfileModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ProfileModal({ open, onOpenChange }: ProfileModalProps) {
  const { user: contextUser, logout, refreshUserProfile } = useAuth();
  const [liveUser, setLiveUser] = useState<UserProfile | null>(contextUser);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [isDeactivating, setIsDeactivating] = useState<boolean>(false);
  const [deactivateMsg, setDeactivateMsg] = useState<string | null>(null);
  const [deactivateErr, setDeactivateErr] = useState<string | null>(null);
  const [showDeactivateConfirm, setShowDeactivateConfirm] = useState<boolean>(false);

  useEffect(() => {
    if (!open) return;

    let isMounted = true;

    refreshUserProfile();

    fetchCurrentUserApi()
      .then((res) => {
        if (!isMounted || !res) return;
        const displayName = res.fullName || res.username || "";
        const initials = displayName ? displayName.substring(0, 2).toUpperCase() : "US";
        const fetchedProfile: UserProfile = {
          userId: String(res.userId || ""),
          username: res.username || "",
          fullName: res.fullName || res.username,
          email: res.email || "",
          enabled: res.enabled ?? true,
          deleted: res.deleted ?? false,
          avatarFallback: initials,
        };
        setLiveUser(fetchedProfile);
      })
      .catch(() => {
        // Ignored catch block
      })
      .finally(() => {
        if (isMounted) {
          setIsLoading(false);
        }
      });

    return () => {
      isMounted = false;
    };
  }, [open, refreshUserProfile]);

  const handleOpenChange = (newOpen: boolean) => {
    if (!newOpen) {
      setShowDeactivateConfirm(false);
      setDeactivateMsg(null);
      setDeactivateErr(null);
    }
    onOpenChange(newOpen);
  };

  const handleDeactivateAccount = async () => {
    setIsDeactivating(true);
    setDeactivateErr(null);
    setDeactivateMsg(null);

    try {
      const res = await deactivateAccount();
      setDeactivateMsg(res.message || "Account deactivated successfully. Logging out...");
      setTimeout(() => {
        logout();
        onOpenChange(false);
      }, 1500);
    } catch (err: any) {
      setDeactivateErr(err.message || "Account deactivation failed.");
    } finally {
      setIsDeactivating(false);
    }
  };

  // Fall back to context user if live user is not yet set
  const activeUser = liveUser || contextUser;

  if (!activeUser) return null;

  const isUserActive = activeUser.enabled !== false;
  const displayName = activeUser.fullName || activeUser.username;
  const avatarInitials = activeUser.avatarFallback || displayName.substring(0, 2).toUpperCase();

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-w-md rounded-2xl p-0 overflow-hidden">
        <DialogHeader className="border-b px-6 py-4">
          <DialogTitle className="flex items-center justify-between text-base font-bold">
            <div className="flex items-center gap-2">
              <User size={18} className="text-primary" />
              <span>User Profile Information</span>
            </div>
            {isLoading && <Loader2 size={16} className="animate-spin text-primary ml-2" />}
          </DialogTitle>
        </DialogHeader>

        <div className="p-6 space-y-5 text-xs">
          {/* Avatar & Main Badge */}
          <div className="flex items-center gap-4 p-4 rounded-xl border bg-muted/30">
            <Avatar className="h-14 w-14 border-2 border-primary/20 shadow-md">
              <AvatarFallback className="text-base font-bold bg-primary/10 text-primary">
                {avatarInitials}
              </AvatarFallback>
            </Avatar>

            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base font-bold text-foreground">
                  {activeUser.username}
                </h3>
                <Badge
                  variant={isUserActive ? "secondary" : "outline"}
                  className={
                    isUserActive
                      ? "text-[9px] bg-primary/10 text-primary border-primary/20"
                      : "text-[9px] bg-muted text-muted-foreground"
                  }
                >
                  {isUserActive ? "Active" : "Disabled"}
                </Badge>
              </div>
              <p className="text-xs text-muted-foreground mt-0.5">{activeUser.email}</p>
            </div>
          </div>

          <Separator />

          {/* User Details Grid */}
          <div className="space-y-3">
            <h4 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Account Metadata
            </h4>

            <div className="space-y-2.5">
              {/* Full Name */}
              <div className="flex items-center justify-between p-2.5 rounded-xl border bg-card">
                <div className="flex items-center gap-2 text-muted-foreground">
                  <IdCard size={14} className="text-primary" />
                  <span className="font-semibold text-foreground">Full Name</span>
                </div>
                <span className="text-xs font-bold text-foreground">
                  {displayName}
                </span>
              </div>

              {/* Username */}
              <div className="flex items-center justify-between p-2.5 rounded-xl border bg-card">
                <div className="flex items-center gap-2 text-muted-foreground">
                  <User size={14} className="text-primary" />
                  <span className="font-semibold text-foreground">Username</span>
                </div>
                <span className="font-mono text-xs font-bold text-foreground">
                  {activeUser.username}
                </span>
              </div>

              {/* Email */}
              <div className="flex items-center justify-between p-2.5 rounded-xl border bg-card">
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Mail size={14} className="text-primary" />
                  <span className="font-semibold text-foreground">Email Address</span>
                </div>
                <span className="font-mono text-xs font-bold text-foreground">
                  {activeUser.email}
                </span>
              </div>

              {/* User ID */}
              <div className="flex items-center justify-between p-2.5 rounded-xl border bg-card">
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Hash size={14} className="text-primary" />
                  <span className="font-semibold text-foreground">User ID</span>
                </div>
                <Badge variant="secondary" className="font-mono text-[10px] font-bold">
                  {activeUser.userId}
                </Badge>
              </div>
            </div>
          </div>

          <Separator />

          {/* Account Deactivation & Deletion Section */}
          <div className="space-y-3 pt-1">
            <h4 className="text-xs font-bold uppercase tracking-wider text-muted-foreground">
              Account Security & Status
            </h4>

            {deactivateErr && (
              <div className="p-2.5 rounded-xl border border-destructive/30 bg-destructive/10 text-destructive text-[11px] flex items-center gap-2">
                <AlertTriangle size={14} className="shrink-0" />
                <span>{deactivateErr}</span>
              </div>
            )}

            {deactivateMsg && (
              <div className="p-2.5 rounded-xl border border-emerald-500/30 bg-emerald-500/10 text-emerald-500 text-[11px] flex items-center gap-2">
                <Check size={14} className="shrink-0" />
                <span>{deactivateMsg}</span>
              </div>
            )}

            {!showDeactivateConfirm ? (
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowDeactivateConfirm(true)}
                className="w-full text-xs font-semibold text-destructive border-destructive/30 hover:bg-destructive/10 gap-2 rounded-xl cursor-pointer"
              >
                <UserX size={14} />
                <span>Deactivate / Delete Account</span>
              </Button>
            ) : (
              <div className="p-3 rounded-xl border border-destructive/30 bg-destructive/5 space-y-2 text-center">
                <p className="text-[11px] font-bold text-destructive flex items-center justify-center gap-1.5">
                  <AlertTriangle size={14} /> Confirm Account Deactivation
                </p>
                <p className="text-[10px] text-muted-foreground">
                  Deactivating will disable access and schedule deletion after 15 days.
                </p>
                <div className="flex items-center justify-center gap-2 pt-1">
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={() => setShowDeactivateConfirm(false)}
                    className="h-7 text-xs rounded-lg cursor-pointer"
                  >
                    Cancel
                  </Button>
                  <Button
                    size="sm"
                    variant="destructive"
                    disabled={isDeactivating}
                    onClick={handleDeactivateAccount}
                    className="h-7 text-xs font-bold rounded-lg gap-1 cursor-pointer"
                  >
                    {isDeactivating ? "Deactivating..." : "Confirm Deactivate"}
                  </Button>
                </div>
              </div>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  );
}
