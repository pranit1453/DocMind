import {
  Sparkles,
  Settings,
  User as UserIcon,
  Bot,
  ChevronDown,
  Sun,
  Moon,
  Laptop,
  LogOut,
  PanelLeftClose,
  PanelLeftOpen,
  ShieldCheck,
  KeyRound,
} from "lucide-react";
import { useTheme } from "@/context/ThemeContext";
import { useAuth } from "@/context/AuthContext";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Separator } from "@/components/ui/separator";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

interface HeaderProps {
  onOpenSettings: () => void;
  onOpenProfile: () => void;
  onOpenAccountSettings: () => void;
  onOpenAdminPortal?: () => void;
  onOpenChangePassword?: () => void;
  isSidebarCollapsed: boolean;
  onToggleSidebar: () => void;
  onToggleMobileSidebar?: () => void;
  onGoHome?: () => void;
}

export function Header({
  onOpenSettings,
  onOpenProfile,
  onOpenAccountSettings,
  onOpenAdminPortal,
  onOpenChangePassword,
  isSidebarCollapsed,
  onToggleSidebar,
  onToggleMobileSidebar,
  onGoHome,
}: HeaderProps) {
  const { theme, setTheme, isDark } = useTheme();
  const { user, logout, isAdmin } = useAuth();

  const handleToggle = () => {
    if (typeof window !== "undefined" && window.innerWidth < 768 && onToggleMobileSidebar) {
      onToggleMobileSidebar();
    } else {
      onToggleSidebar();
    }
  };

  return (
    <header className="flex h-14 shrink-0 items-center justify-between border-b bg-background/95 backdrop-blur-md px-3 sm:px-4 select-none z-10 transition-colors">
      {/* Brand & Logo + Sidebar Toggle */}
      <div className="flex items-center gap-1.5 sm:gap-2">
        <Button
          variant="ghost"
          size="icon"
          className="h-8 w-8 text-muted-foreground hover:text-foreground hover:bg-accent rounded-lg"
          onClick={handleToggle}
          title={isSidebarCollapsed ? "Expand Sidebar" : "Collapse Sidebar"}
        >
          {isSidebarCollapsed ? (
            <PanelLeftOpen size={17} className="text-primary" />
          ) : (
            <PanelLeftClose size={17} />
          )}
        </Button>

        <Separator orientation="vertical" className="h-4 mx-0.5" />

        {/* DocMind Logo - Navigates to Home Landing Page */}
        <div
          onClick={onGoHome}
          className="flex items-center gap-2 cursor-pointer group"
          title="Go to DocMind Home Landing Page"
        >
          <div className="flex h-8 w-8 items-center justify-center rounded-xl bg-gradient-to-tr from-primary to-primary/80 text-primary-foreground shadow-sm shadow-primary/20 transition-transform group-hover:scale-105">
            <Sparkles size={17} className="animate-pulse" />
          </div>

          <div className="flex items-center gap-2">
            <span className="text-sm font-bold tracking-tight bg-gradient-to-r from-foreground to-foreground/80 bg-clip-text text-transparent group-hover:text-primary transition-colors">
              DocMind
            </span>

            <Badge
              variant="secondary"
              className="hidden text-[9px] font-semibold tracking-wider sm:inline-flex bg-primary/10 text-primary border-primary/20"
            >
              RAG workflow
            </Badge>
          </div>
        </div>

        <Separator orientation="vertical" className="mx-1 h-4 hidden md:block" />

        <span className="hidden text-xs text-muted-foreground/80 font-medium md:block">
          Knowledge Workspace
        </span>
      </div>

      {/* Action Toolbar */}
      <div className="flex items-center gap-1.5">
        {/* Admin Portal Button - Strictly visible ONLY for Administrator Users */}
        {isAdmin && onOpenAdminPortal && (
          <Button
            variant="outline"
            size="sm"
            onClick={onOpenAdminPortal}
            className="h-8 text-xs font-semibold gap-1.5 rounded-xl border-purple-500/30 text-purple-500 bg-purple-500/10 hover:bg-purple-500/20 cursor-pointer"
            title="Open Admin Portal"
          >
            <ShieldCheck size={14} />
            <span className="hidden sm:inline">Admin Portal</span>
          </Button>
        )}

        {/* Theme Toggle Dropdown */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              size="icon"
              className="h-8 w-8 text-muted-foreground hover:text-foreground hover:bg-accent rounded-lg"
              title="Toggle theme"
            >
              {isDark ? (
                <Moon size={16} className="text-blue-400 transition-transform rotate-0 hover:rotate-12" />
              ) : (
                <Sun size={16} className="text-amber-500 transition-transform rotate-0 hover:rotate-45" />
              )}
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-36">
            <DropdownMenuLabel className="text-[10px] text-muted-foreground uppercase font-semibold">
              Theme Mode
            </DropdownMenuLabel>
            <DropdownMenuItem onClick={() => setTheme("light")} className="gap-2 text-xs">
              <Sun size={14} className="text-amber-500" />
              <span>Light</span>
              {theme === "light" && <span className="ml-auto text-[10px] font-bold">✓</span>}
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => setTheme("dark")} className="gap-2 text-xs">
              <Moon size={14} className="text-blue-400" />
              <span>Dark</span>
              {theme === "dark" && <span className="ml-auto text-[10px] font-bold">✓</span>}
            </DropdownMenuItem>
            <DropdownMenuItem onClick={() => setTheme("system")} className="gap-2 text-xs">
              <Laptop size={14} className="text-muted-foreground" />
              <span>System</span>
              {theme === "system" && <span className="ml-auto text-[10px] font-bold">✓</span>}
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>

        {/* Settings Button */}
        <Button
          variant="ghost"
          size="icon"
          className="h-8 w-8 text-muted-foreground hover:text-foreground hover:bg-accent rounded-lg"
          onClick={onOpenSettings}
          title="Workspace Settings"
        >
          <Settings size={16} />
        </Button>

        <Separator orientation="vertical" className="mx-1 h-4" />

        {/* User Profile Dropdown */}
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button
              variant="ghost"
              className="h-8 gap-2 px-1.5 rounded-full hover:bg-accent"
            >
              <Avatar className="h-7 w-7 border border-border">
                <AvatarFallback className="text-[11px] font-bold bg-primary/10 text-primary">
                  {user?.avatarFallback || (user?.fullName || user?.username || "US").substring(0, 2).toUpperCase()}
                </AvatarFallback>
              </Avatar>

              <span className="hidden text-xs font-medium sm:inline">
                {user?.username || "Account"}
              </span>

              <ChevronDown size={13} className="text-muted-foreground" />
            </Button>
          </DropdownMenuTrigger>

          <DropdownMenuContent align="end" className="w-56">
            <div className="px-3 py-2">
              <p className="text-sm font-semibold">{user?.username || "Workspace User"}</p>
              <p className="text-xs text-muted-foreground truncate">
                {user?.email || (user?.username ? `${user.username.toLowerCase()}@docmind.ai` : "")}
              </p>
            </div>

            <DropdownMenuSeparator />

            {/* Profile -> Shows User Data */}
            <DropdownMenuItem onClick={onOpenProfile} className="gap-2 text-xs">
              <UserIcon size={14} />
              <span>Profile</span>
            </DropdownMenuItem>

            {/* Change Password */}
            {onOpenChangePassword && (
              <DropdownMenuItem onClick={onOpenChangePassword} className="gap-2 text-xs">
                <KeyRound size={14} />
                <span>Change Password</span>
              </DropdownMenuItem>
            )}

            {/* Account Settings */}
            <DropdownMenuItem onClick={onOpenAccountSettings} className="gap-2 text-xs">
              <Settings size={14} />
              <span>Account Settings</span>
            </DropdownMenuItem>

            {/* AI Model Preferences -> Settings Modal */}
            <DropdownMenuItem onClick={onOpenSettings} className="gap-2 text-xs">
              <Bot size={14} />
              <span>AI Model Preferences</span>
            </DropdownMenuItem>

            <DropdownMenuSeparator />

            {/* Sign Out -> Clears Cookies & Returns to Auth Page */}
            <DropdownMenuItem
              onClick={logout}
              className="text-destructive focus:text-destructive focus:bg-destructive/10 gap-2 text-xs cursor-pointer"
            >
              <LogOut size={14} />
              <span>Sign out</span>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}
