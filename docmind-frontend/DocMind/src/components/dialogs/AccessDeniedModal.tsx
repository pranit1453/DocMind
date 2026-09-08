import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogDescription,
  DialogFooter,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { ShieldAlert, LogOut } from "lucide-react";

interface AccessDeniedModalProps {
  open: boolean;
  message?: string;
  onConfirmLogout: () => void;
}

export function AccessDeniedModal({
  open,
  message = "Access Denied: Your session has expired or permission was revoked. Please log in again to continue.",
  onConfirmLogout,
}: AccessDeniedModalProps) {
  const handleClose = () => {
    onConfirmLogout();
  };

  return (
    <Dialog open={open} onOpenChange={(isOpen) => { if (!isOpen) handleClose(); }}>
      <DialogContent className="max-w-md rounded-2xl p-6 border-destructive/20 shadow-2xl">
        <DialogHeader>
          <div className="mx-auto mb-3 flex h-14 w-14 items-center justify-center rounded-2xl bg-destructive/10 text-destructive ring-8 ring-destructive/5">
            <ShieldAlert size={28} />
          </div>
          <DialogTitle className="text-center text-lg font-bold text-foreground">
            Access Denied / Session Expired
          </DialogTitle>
          <DialogDescription className="text-center text-xs text-muted-foreground mt-2 leading-relaxed">
            {message}
          </DialogDescription>
        </DialogHeader>

        <DialogFooter className="mt-6 sm:justify-center">
          <Button
            onClick={handleClose}
            variant="destructive"
            className="w-full sm:w-auto px-6 h-10 text-xs font-semibold rounded-xl gap-2 shadow-md hover:shadow-lg transition-all"
          >
            <LogOut size={14} />
            Acknowledge & Log Out
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
