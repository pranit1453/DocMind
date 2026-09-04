import { useState, useRef, useEffect } from "react";
import { ThemeProvider } from "@/context/ThemeContext";
import { AuthProvider, useAuth } from "@/context/AuthContext";
import { useKnowledgeBase } from "@/hooks/useKnowledgeBase";
import { Header } from "@/components/layout/Header";
import { KnowledgeSidebar } from "@/components/sidebar/KnowledgeSidebar";
import { ChatPanel } from "@/components/chat/ChatPanel";
import { MarkdownDialog } from "@/components/dialogs/MarkdownDialog";
import { SettingsModal } from "@/components/dialogs/SettingsModal";
import { ProfileModal } from "@/components/dialogs/ProfileModal";
import { AccountSettingsModal } from "@/components/dialogs/AccountSettingsModal";
import { AdminPortalModal } from "@/components/dialogs/AdminPortalModal";
import { ChangePasswordModal } from "@/components/dialogs/ChangePasswordModal";
import { AuthPage } from "@/components/auth/AuthPage";
import { DocMindLandingHome } from "@/components/home/DocMindLandingHome";
import { TooltipProvider } from "@/components/ui/tooltip";
import {
  ResizableHandle,
  ResizablePanel,
  ResizablePanelGroup,
} from "@/components/ui/resizable";
import { Sheet, SheetContent } from "@/components/ui/sheet";
import type { PanelImperativeHandle } from "react-resizable-panels";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

type AppView = "home" | "workspace" | "auth";

function MainAppController() {
  const { isAuthenticated } = useAuth();
  const [mobileSidebarOpen, setMobileSidebarOpen] = useState(false);

  // Persist current view across page refreshes
  const [view, setView] = useState<AppView>(() => {
    try {
      const savedView = localStorage.getItem("docmind-current-view") as AppView | null;
      if (savedView && ["home", "workspace", "auth"].includes(savedView)) {
        const savedUser = localStorage.getItem("docmind-user");
        if (savedUser && savedView === "workspace") return "workspace";
        return savedView;
      }
    } catch {}
    const savedUser = localStorage.getItem("docmind-user");
    return savedUser ? "workspace" : "home";
  });

  useEffect(() => {
    try {
      localStorage.setItem("docmind-current-view", view);
    } catch {}
  }, [view]);

  const {
    documents,
    selectedId,
    setSelectedId,
    selectedDocument,
    filteredDocuments,
    search,
    setSearch,
    input,
    setInput,
    messages,
    isDragging,
    setIsDragging,
    markdownOpen,
    setMarkdownOpen,
    settingsOpen,
    setSettingsOpen,
    copied,
    useStreamApi,
    toggleStreamApi,
    isUploading,
    uploadMessage,
    uploadFiles,
    removeDocument,
    sendMessage,
    copyConversationAsMarkdown,
    exportConversationAsMarkdown,
    clearChat,
  } = useKnowledgeBase(isAuthenticated);

  const leftPanelRef = useRef<PanelImperativeHandle>(null);
  const [profileOpen, setProfileOpen] = useState(false);
  const [accountSettingsOpen, setAccountSettingsOpen] = useState(false);
  const [adminPortalOpen, setAdminPortalOpen] = useState(false);
  const [changePasswordOpen, setChangePasswordOpen] = useState(false);

  // Synchronously initialize sidebar collapsed state from localStorage
  const [isSidebarCollapsed, setIsSidebarCollapsed] = useState<boolean>(() => {
    try {
      return localStorage.getItem("docmind-sidebar-collapsed") === "true";
    } catch {
      return false;
    }
  });

  const toggleSidebar = () => {
    const panel = leftPanelRef.current;
    if (panel) {
      if (panel.isCollapsed()) {
        panel.expand();
        setIsSidebarCollapsed(false);
        try {
          localStorage.setItem("docmind-sidebar-collapsed", "false");
        } catch {}
      } else {
        panel.collapse();
        setIsSidebarCollapsed(true);
        try {
          localStorage.setItem("docmind-sidebar-collapsed", "true");
        } catch {}
      }
    }
  };

  const handleSelectDocumentOnMobile = (id: string) => {
    setSelectedId(id);
    setMobileSidebarOpen(false);
  };

  const handleLaunchWorkspace = () => {
    if (isAuthenticated) {
      setView("workspace");
    } else {
      setView("auth");
    }
  };

  if (view === "home") {
    return (
      <DocMindLandingHome
        onLaunchWorkspace={handleLaunchWorkspace}
        onOpenAuth={() => setView("auth")}
      />
    );
  }

  // Strict Authentication Guard: Without logging in, user cannot enter workspace
  if (view === "auth" || !isAuthenticated) {
    return (
      <AuthPage
        onGoHome={() => setView("home")}
        onSuccess={() => setView("workspace")}
      />
    );
  }

  return (
    <TooltipProvider>
      <div className="flex h-screen w-screen flex-col overflow-hidden bg-background text-foreground selection:bg-primary/20 font-sans">
        {/* Top Navbar */}
        <Header
          onOpenSettings={() => setSettingsOpen(true)}
          onOpenProfile={() => setProfileOpen(true)}
          onOpenAccountSettings={() => setAccountSettingsOpen(true)}
          onOpenAdminPortal={() => setAdminPortalOpen(true)}
          onOpenChangePassword={() => setChangePasswordOpen(true)}
          isSidebarCollapsed={isSidebarCollapsed}
          onToggleSidebar={toggleSidebar}
          onToggleMobileSidebar={() => setMobileSidebarOpen(true)}
          onGoHome={() => setView("home")}
        />

        {/* Mobile Slide-Out Sheet Drawer (< md screens) */}
        <Sheet open={mobileSidebarOpen} onOpenChange={setMobileSidebarOpen}>
          <SheetContent side="left" className="w-[85vw] max-w-sm p-0">
            <KnowledgeSidebar
              documents={documents}
              filteredDocuments={filteredDocuments}
              selectedId={selectedId}
              onSelectDocument={handleSelectDocumentOnMobile}
              onRemoveDocument={removeDocument}
              search={search}
              onSearchChange={setSearch}
              isDragging={isDragging}
              setIsDragging={setIsDragging}
              onUpload={uploadFiles}
              isUploading={isUploading}
            />
          </SheetContent>
        </Sheet>

        {/* Resizable 2-Panel Main Workspace for Desktop & Tablet */}
        <ResizablePanelGroup orientation="horizontal" className="min-h-0 flex-1">
          {/* Left Panel: Knowledge Base Sidebar (Hidden or collapsible) */}
          <ResizablePanel
            panelRef={leftPanelRef}
            defaultSize={isSidebarCollapsed ? "0%" : "26%"}
            minSize="18%"
            maxSize="45%"
            collapsible={true}
            collapsedSize="0%"
            className="hidden md:block"
            onResize={() => {
              if (leftPanelRef.current) {
                const collapsed = leftPanelRef.current.isCollapsed();
                if (collapsed !== isSidebarCollapsed) {
                  setIsSidebarCollapsed(collapsed);
                  try {
                    localStorage.setItem("docmind-sidebar-collapsed", String(collapsed));
                  } catch {}
                }
              }
            }}
          >
            <KnowledgeSidebar
              documents={documents}
              filteredDocuments={filteredDocuments}
              selectedId={selectedId}
              onSelectDocument={setSelectedId}
              onRemoveDocument={removeDocument}
              search={search}
              onSearchChange={setSearch}
              isDragging={isDragging}
              setIsDragging={setIsDragging}
              onUpload={uploadFiles}
              isUploading={isUploading}
            />
          </ResizablePanel>

          {/* Resizable Handle with Drag Grip */}
          <ResizableHandle withHandle className="hidden md:flex" />

          {/* Right Panel: Main AI Assistant RAG Chat */}
          <ResizablePanel defaultSize={isSidebarCollapsed ? "100%" : "74%"} minSize="50%" className="w-full">
            <ChatPanel
              selectedDocument={selectedDocument}
              messages={messages}
              input={input}
              setInput={setInput}
              onSendMessage={sendMessage}
              onCopyMarkdown={copyConversationAsMarkdown}
              onExportMarkdown={exportConversationAsMarkdown}
              onOpenMarkdownModal={() => setMarkdownOpen(true)}
              onClearChat={clearChat}
              copied={copied}
              useStreamApi={useStreamApi}
              onToggleStreamApi={toggleStreamApi}
              isUploading={isUploading}
              uploadMessage={uploadMessage}
            />
          </ResizablePanel>
        </ResizablePanelGroup>

        {/* User Profile Modal */}
        <ProfileModal
          open={profileOpen}
          onOpenChange={setProfileOpen}
        />

        {/* Account Settings Modal */}
        <AccountSettingsModal
          open={accountSettingsOpen}
          onOpenChange={setAccountSettingsOpen}
        />

        {/* Admin Portal Modal */}
        <AdminPortalModal
          open={adminPortalOpen}
          onOpenChange={setAdminPortalOpen}
        />

        {/* Change Password Modal */}
        <ChangePasswordModal
          open={changePasswordOpen}
          onOpenChange={setChangePasswordOpen}
        />

        {/* Markdown Inspector Modal */}
        <MarkdownDialog
          open={markdownOpen}
          onOpenChange={setMarkdownOpen}
          messages={messages}
          copied={copied}
          onCopy={copyConversationAsMarkdown}
        />

        {/* AI Model Preferences & Settings Modal */}
        <SettingsModal
          open={settingsOpen}
          onOpenChange={setSettingsOpen}
        />
      </div>
    </TooltipProvider>
  );
}

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
});

export default function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <ThemeProvider>
        <AuthProvider>
          <MainAppController />
        </AuthProvider>
      </ThemeProvider>
    </QueryClientProvider>
  );
}
