import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { AlertTriangle, UserX, Check, AlertCircle } from "lucide-react";
import { useAuth } from "@/context/AuthContext";
import { useDeactivateAccountMutation } from "@/hooks/useAuthMutations";

interface AccountSettingsModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function AccountSettingsModal({
  open,
  onOpenChange,
}: AccountSettingsModalProps) {
  const { logout } = useAuth();
  const deactivateMutation = useDeactivateAccountMutation();
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleDeactivate = async () => {
    setError(null);
    setMessage(null);
    try {
      await deactivateMutation.mutateAsync();
      setMessage("Account successfully deactivated. Logging out...");
      setTimeout(() => {
        logout();
        onOpenChange(false);
      }, 1500);
    } catch (err: any) {
      setError(err.message || "Failed to deactivate account.");
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md rounded-2xl p-0 overflow-hidden text-center">
        <DialogHeader className="border-b px-6 py-4">
          <DialogTitle className="flex items-center justify-center gap-2 text-base font-bold text-foreground">
            <UserX size={18} className="text-destructive" />
            Account Management & Settings
          </DialogTitle>
        </DialogHeader>

        <div className="p-6 space-y-4">
          <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-destructive/10 text-destructive border border-destructive/20 shadow-md">
            <AlertTriangle size={28} />
          </div>

          <div className="space-y-1.5">
            <h3 className="text-base font-bold text-foreground">
              Deactivate User Account
            </h3>
            <p className="text-xs text-muted-foreground max-w-xs mx-auto leading-relaxed">
              Deactivating your account will disable your access to DocMind and schedule your permanent account deletion after 15 days.
            </p>
          </div>

          {error && (
            <div className="flex items-center justify-center gap-1.5 text-xs text-destructive bg-destructive/10 p-2.5 rounded-xl border border-destructive/20">
              <AlertCircle size={14} />
              <span>{error}</span>
            </div>
          )}

          {message && (
            <div className="flex items-center justify-center gap-1.5 text-xs text-emerald-500 bg-emerald-500/10 p-2.5 rounded-xl border border-emerald-500/20">
              <Check size={14} />
              <span>{message}</span>
            </div>
          )}

          <Badge variant="outline" className="text-[10px] text-amber-500 border-amber-500/30 bg-amber-500/10 px-3 py-1">
            Status: Active Member
          </Badge>
        </div>

        <DialogFooter className="border-t px-6 py-3 bg-muted/10 flex justify-between gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={() => onOpenChange(false)}
            className="rounded-xl text-xs font-semibold cursor-pointer"
          >
            Cancel
          </Button>

          <Button
            variant="destructive"
            size="sm"
            disabled={deactivateMutation.isPending}
            onClick={handleDeactivate}
            className="rounded-xl text-xs font-bold gap-1 cursor-pointer"
          >
            {deactivateMutation.isPending ? "Deactivating..." : "Deactivate Account"}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
