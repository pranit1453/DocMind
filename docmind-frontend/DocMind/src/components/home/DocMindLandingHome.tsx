import { motion } from "framer-motion";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { useTheme } from "@/context/ThemeContext";
import { useAuth } from "@/context/AuthContext";
import {
  Sparkles,
  Database,
  FileText,
  ShieldCheck,
  Download,
  Lock,
  Sun,
  Moon,
  ArrowRight,
  Zap,
  CheckCircle2,
  Layers,
  FileCode2,
  Workflow,
} from "lucide-react";

import { AnimatedVectorBackground } from "./AnimatedVectorBackground";

interface DocMindLandingHomeProps {
  onLaunchWorkspace: () => void;
  onOpenAuth: () => void;
}

export function DocMindLandingHome({
  onLaunchWorkspace,
  onOpenAuth,
}: DocMindLandingHomeProps) {
  const { isDark, toggleTheme } = useTheme();
  const { isAuthenticated, logout } = useAuth();

  return (
    <div className="relative min-h-screen w-screen bg-background text-foreground transition-colors overflow-x-hidden select-none font-sans">
      <AnimatedVectorBackground />
      {/* Top Navbar */}
      <header className="sticky top-0 z-50 flex h-16 w-full items-center justify-between border-b border-border/60 bg-background/80 backdrop-blur-xl px-6 md:px-12">
        <div className="flex items-center gap-3">
          <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-tr from-primary to-primary/80 text-primary-foreground shadow-md shadow-primary/20">
            <Sparkles size={18} className="animate-pulse" />
          </div>
          <div className="flex items-center gap-2">
            <span className="text-base font-bold tracking-tight bg-gradient-to-r from-foreground to-foreground/80 bg-clip-text text-transparent">
              DocMind
            </span>
            <Badge variant="secondary" className="text-[9px] font-semibold bg-primary/10 text-primary border-primary/20">
              RAG workflow
            </Badge>
          </div>
        </div>

        {/* Center Nav Links */}
        <nav className="hidden md:flex items-center gap-8 text-xs font-semibold text-muted-foreground">
          <a href="#features" className="hover:text-foreground transition-colors">
            Features
          </a>
          <a href="#why-choose-us" className="hover:text-foreground transition-colors">
            Why DocMind
          </a>
          <a href="#security" className="hover:text-foreground transition-colors">
            Security
          </a>
        </nav>

        {/* Right Action Toolbar */}
        <div className="flex items-center gap-3">
          <Button
            variant="ghost"
            size="icon"
            className="h-8 w-8 text-muted-foreground hover:text-foreground hover:bg-accent rounded-lg"
            onClick={toggleTheme}
            title="Toggle theme"
          >
            {isDark ? <Moon size={16} className="text-blue-400" /> : <Sun size={16} className="text-amber-500" />}
          </Button>

          {isAuthenticated ? (
            <div className="flex items-center gap-2">
              <Button
                onClick={onLaunchWorkspace}
                size="sm"
                className="rounded-xl font-bold text-xs gap-1.5 shadow-md shadow-primary/10"
              >
                <span>Workspace</span>
                <ArrowRight size={14} />
              </Button>
              <Button
                variant="outline"
                size="sm"
                onClick={logout}
                className="rounded-xl text-xs"
              >
                Sign out
              </Button>
            </div>
          ) : (
            <div className="flex items-center gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={onOpenAuth}
                className="rounded-xl text-xs font-semibold"
              >
                Sign In
              </Button>
              <Button
                onClick={onLaunchWorkspace}
                size="sm"
                className="rounded-xl font-bold text-xs gap-1.5 shadow-md shadow-primary/10"
              >
                <span>Get Started</span>
                <ArrowRight size={14} />
              </Button>
            </div>
          )}
        </div>
      </header>

      {/* Hero Section */}
      <section className="relative py-24 px-6 text-center flex flex-col items-center justify-center max-w-5xl mx-auto">
        {/* Background Soft Glow */}
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 h-80 w-80 rounded-full bg-primary/10 blur-3xl pointer-events-none" />

        <motion.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.6 }}
        >
          <Badge variant="outline" className="mb-6 px-3 py-1 text-xs gap-1.5 border-primary/30 bg-primary/5 text-primary">
            <Zap size={13} />
            Next-Gen RAG Vector Engine
          </Badge>
        </motion.div>

        <motion.h1
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8, delay: 0.1 }}
          className="text-4xl sm:text-6xl md:text-7xl font-extrabold tracking-tight leading-tight text-foreground"
        >
          Smart. <span className="bg-gradient-to-r from-primary via-primary/80 to-blue-500 bg-clip-text text-transparent">Grounded.</span> Futuristic.
        </motion.h1>

        <motion.p
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.8 }}
          className="mt-6 max-w-2xl text-base sm:text-lg md:text-xl text-muted-foreground leading-relaxed"
        >
          The next‑generation AI RAG knowledge workspace built for modern apps, developers, and teams. Transform PDFs, Markdown, and documents into instant grounded intelligence.
        </motion.p>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4, duration: 0.8 }}
          className="mt-10 flex flex-wrap items-center justify-center gap-4"
        >
          <Button
            size="lg"
            onClick={onLaunchWorkspace}
            className="rounded-2xl text-sm font-bold px-8 h-12 gap-2 shadow-lg shadow-primary/20 hover:scale-105 transition-transform"
          >
            <span>Launch Workspace</span>
            <ArrowRight size={16} />
          </Button>

          <Button
            size="lg"
            variant="outline"
            onClick={() => {
              document.getElementById("features")?.scrollIntoView({ behavior: "smooth" });
            }}
            className="rounded-2xl text-sm font-semibold px-8 h-12 border-border/80 hover:bg-accent"
          >
            Explore Features
          </Button>
        </motion.div>

        {/* Feature Pills */}
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ delay: 0.6, duration: 0.8 }}
          className="mt-12 flex flex-wrap justify-center items-center gap-6 text-xs font-semibold text-muted-foreground/80"
        >
          <span className="flex items-center gap-1.5">
            <CheckCircle2 size={14} className="text-emerald-500" /> 100% Vector Grounded
          </span>
          <span className="flex items-center gap-1.5">
            <CheckCircle2 size={14} className="text-emerald-500" /> Zero Hallucinations
          </span>
          <span className="flex items-center gap-1.5">
            <CheckCircle2 size={14} className="text-emerald-500" /> Token Based Security
          </span>
        </motion.div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-24 px-6 max-w-6xl mx-auto">
        <div className="text-center max-w-2xl mx-auto mb-16 space-y-3">
          <Badge variant="secondary" className="text-[10px] font-semibold uppercase tracking-wider">
            Capabilities
          </Badge>
          <h2 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-foreground">
            Powerful RAG Features
          </h2>
          <p className="text-xs sm:text-sm text-muted-foreground">
            Designed for high-precision retrieval, security, and document context streaming.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            {
              title: "Vector RAG Intelligence",
              desc: "Next-level cosine similarity search with exact chunk vector retrieval.",
              icon: <Database className="w-8 h-8" />,
              tag: "Cosine Vector",
            },
            {
              title: "Multi-Format Indexing",
              desc: "Seamless indexing for PDF, Markdown, DOCX, TXT, and CSV files.",
              icon: <FileText className="w-8 h-8" />,
              tag: "PDF / MD / CSV",
            },
            {
              title: "Source Citation & Attribution",
              desc: "Every answer is strictly cited with exact page number and vector chunk ID.",
              icon: <ShieldCheck className="w-8 h-8" />,
              tag: "Zero Hallucination",
            },
            {
              title: "Enterprise Cookie Security",
              desc: "HTTP-only cookie access and refresh tokens for protected auth sessions.",
              icon: <Lock className="w-8 h-8" />,
              tag: "JWT Cookies",
            },
            {
              title: "Modular RAG Pipeline",
              desc: "Complete 4-stage engine: Pre-Retrieval indexing, similarity Retrieval, Post-Retrieval reranking, and grounded Generation.",
              icon: <Workflow className="w-8 h-8" />,
              tag: "Pre/Post Retrieval",
            },
            {
              title: "Markdown Export & Sync",
              desc: "Copy or export full grounded conversation threads in GitHub Flavored Markdown.",
              icon: <Download className="w-8 h-8" />,
              tag: "GFM Markdown",
            },
          ].map((feature, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 20 }}
              whileInView={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: i * 0.1 }}
              viewport={{ once: true }}
            >
              <Card className="group relative border-border/80 bg-card/70 backdrop-blur-xl rounded-2xl shadow-sm hover:shadow-md hover:border-primary/40 transition-all duration-300 h-full">
                <CardContent className="p-6 flex flex-col justify-between h-full">
                  <div>
                    <div className="flex items-center justify-between mb-4">
                      <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary border border-primary/20 group-hover:scale-110 transition-transform">
                        {feature.icon}
                      </div>
                      <Badge variant="outline" className="text-[9px] font-mono">
                        {feature.tag}
                      </Badge>
                    </div>
                    <h3 className="text-base font-bold text-foreground mb-2 group-hover:text-primary transition-colors">
                      {feature.title}
                    </h3>
                    <p className="text-xs text-muted-foreground leading-relaxed">
                      {feature.desc}
                    </p>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      </section>

      {/* Why Choose Us Section */}
      <section id="why-choose-us" className="py-24 px-6 bg-card/40 backdrop-blur-lg border-y border-border">
        <div className="max-w-4xl mx-auto">
          <div className="text-center mb-16 space-y-2">
            <h2 className="text-3xl font-extrabold text-foreground">
              Why Choose DocMind RAG Platform?
            </h2>
            <p className="text-xs text-muted-foreground">
              Built from the ground up for strict knowledge accuracy and developer flexibility.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8 text-xs">
            <div className="p-6 rounded-2xl border border-border bg-card">
              <h3 className="text-sm font-bold text-foreground mb-2 flex items-center gap-2">
                <Sparkles className="w-5 h-5 text-primary" /> AI-Driven Vector Grounding
              </h3>
              <p className="text-muted-foreground leading-relaxed">
                Real-time chunk vector indexing ensures models respond strictly using your documents without making up answers.
              </p>
            </div>

            <div className="p-6 rounded-2xl border border-border bg-card">
              <h3 className="text-sm font-bold text-foreground mb-2 flex items-center gap-2">
                <Zap className="w-5 h-5 text-primary" /> Lightning-Fast Performance
              </h3>
              <p className="text-muted-foreground leading-relaxed">
                Optimized state management and instant client-side chunk previews with zero lag.
              </p>
            </div>

            <div className="p-6 rounded-2xl border border-border bg-card">
              <h3 className="text-sm font-bold text-foreground mb-2 flex items-center gap-2">
                <Layers className="w-5 h-5 text-primary" /> Resizable Dual Panel Layout
              </h3>
              <p className="text-muted-foreground leading-relaxed">
                Maximize and minimize document knowledge base sidebars seamlessly with persisted layout state.
              </p>
            </div>

            <div className="p-6 rounded-2xl border border-border bg-card">
              <h3 className="text-sm font-bold text-foreground mb-2 flex items-center gap-2">
                <FileCode2 className="w-5 h-5 text-primary" /> Developer-Friendly Markdown
              </h3>
              <p className="text-muted-foreground leading-relaxed">
                Integrated ReactMarkdown with GFM plugins, code block formatting, and clipboard export.
              </p>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section id="security" className="py-24 px-6 text-center max-w-4xl mx-auto">
        <Card className="rounded-3xl border-primary/30 bg-gradient-to-tr from-primary/10 via-card to-card p-8 sm:p-12 shadow-2xl relative overflow-hidden">
          <div className="relative z-10 space-y-4">
            <h2 className="text-3xl sm:text-4xl font-extrabold text-foreground tracking-tight">
              Start Exploring Your Knowledge Base Today
            </h2>
            <p className="max-w-xl mx-auto text-xs sm:text-sm text-muted-foreground leading-relaxed">
              Join thousands of developers, teams, and researchers relying on DocMind for accurate AI RAG intelligence.
            </p>

            <div className="pt-4">
              <Button
                size="lg"
                onClick={onLaunchWorkspace}
                className="px-8 h-12 text-sm font-bold rounded-2xl gap-2 shadow-lg shadow-primary/20 hover:scale-105 transition-transform"
              >
                <span>Open Workspace Now</span>
                <ArrowRight size={16} />
              </Button>
            </div>
          </div>
        </Card>
      </section>

      {/* Footer */}
      <footer className="py-8 text-center text-xs text-muted-foreground border-t border-border">
        <p>© {new Date().getFullYear()} DocMind AI RAG Platform. All rights reserved.</p>
      </footer>
    </div>
  );
}
