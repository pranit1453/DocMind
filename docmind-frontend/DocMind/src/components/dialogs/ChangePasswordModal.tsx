import { useState } from "react";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { KeyRound, Lock, Check, AlertCircle, RefreshCw } from "lucide-react";
import { useChangePasswordMutation } from "@/hooks/useAuthMutations";

interface ChangePasswordModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function ChangePasswordModal({ open, onOpenChange }: ChangePasswordModalProps) {
  const changePasswordMutation = useChangePasswordMutation();

  const [oldPassword, setOldPassword] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(null);

    if (!oldPassword.trim()) {
      setError("Please enter your current password.");
      return;
    }
    if (!password.trim()) {
      setError("Please enter a new password.");
      return;
    }
    if (password.length < 8) {
      setError("New password must be at least 8 characters long.");
      return;
    }
    if (password !== confirmPassword) {
      setError("New passwords do not match.");
      return;
    }

    try {
      const res = await changePasswordMutation.mutateAsync({
        oldPassword,
        request: { password, confirmPassword },
      });

      setSuccess(res.message || "Account password updated successfully!");
      setOldPassword("");
      setPassword("");
      setConfirmPassword("");

      setTimeout(() => {
        setSuccess(null);
        onOpenChange(false);
      }, 1500);
    } catch (err: any) {
      setError(err.message || "Failed to change account password.");
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-md rounded-2xl p-6">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-2 text-base font-bold text-foreground">
            <KeyRound size={18} className="text-primary" />
            Change Account Password
          </DialogTitle>
        </DialogHeader>

        {error && (
          <div className="flex items-center gap-2 rounded-xl border border-destructive/30 bg-destructive/10 p-3 text-xs text-destructive">
            <AlertCircle size={15} className="shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="flex items-center gap-2 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-3 text-xs text-emerald-500">
            <Check size={15} className="shrink-0" />
            <span>{success}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5 mt-2">
          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
              <Lock size={12} className="text-primary" /> Current Password
            </label>
            <Input
              type="password"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
              placeholder="Enter current password"
              className="h-9 text-xs"
            />
          </div>

          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
              <Lock size={12} className="text-primary" /> New Password
            </label>
            <Input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Enter new password (min 8 chars)"
              className="h-9 text-xs"
            />
          </div>

          <div className="space-y-1">
            <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
              <Lock size={12} className="text-primary" /> Confirm New Password
            </label>
            <Input
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="Confirm new password"
              className="h-9 text-xs"
            />
          </div>

          <DialogFooter className="mt-4 flex justify-between gap-2">
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() => onOpenChange(false)}
              className="rounded-xl text-xs font-semibold"
            >
              Cancel
            </Button>

            <Button
              type="submit"
              size="sm"
              disabled={changePasswordMutation.isPending}
              className="rounded-xl text-xs font-bold gap-1 cursor-pointer"
            >
              {changePasswordMutation.isPending ? (
                <span className="flex items-center gap-1.5">
                  <RefreshCw size={13} className="animate-spin" /> Updating...
                </span>
              ) : (
                "Update Password"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
