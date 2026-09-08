import React, { createContext, useContext, useLayoutEffect, useState } from "react";

type Theme = "dark" | "light" | "system";

interface ThemeContextType {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  isDark: boolean;
  toggleTheme: () => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export function ThemeProvider({ children }: { children: React.ReactNode }) {
  const [theme, setTheme] = useState<Theme>(() => {
    try {
      const saved = localStorage.getItem("docmind-theme") as Theme | null;
      return saved || "dark";
    } catch {
      return "dark";
    }
  });

  const [isDark, setIsDark] = useState<boolean>(() => {
    try {
      const saved = localStorage.getItem("docmind-theme");
      if (saved === "light") return false;
      if (saved === "dark") return true;
      if (saved === "system") {
        return window.matchMedia("(prefers-color-scheme: dark)").matches;
      }
      return document.documentElement.classList.contains("dark");
    } catch {
      return true;
    }
  });

  useLayoutEffect(() => {
    const root = window.document.documentElement;
    try {
      localStorage.setItem("docmind-theme", theme);
    } catch {}

    const applyTheme = (isDarkTheme: boolean) => {
      setIsDark(isDarkTheme);
      if (isDarkTheme) {
        root.classList.add("dark");
      } else {
        root.classList.remove("dark");
      }
    };

    if (theme === "system") {
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");
      applyTheme(mediaQuery.matches);

      const handleChange = (e: MediaQueryListEvent) => {
        applyTheme(e.matches);
      };

      mediaQuery.addEventListener("change", handleChange);
      return () => mediaQuery.removeEventListener("change", handleChange);
    } else {
      applyTheme(theme === "dark");
    }
  }, [theme]);

  const toggleTheme = () => {
    setTheme(isDark ? "light" : "dark");
  };

  return (
    <ThemeContext.Provider value={{ theme, setTheme, isDark, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

export function useTheme() {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
}
