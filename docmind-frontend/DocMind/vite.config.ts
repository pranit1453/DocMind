import path from "path"
import tailwindcss from "@tailwindcss/vite"
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(import.meta.dirname, "./src"),
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        ws: true,
        cookieDomainRewrite: "",
        cookiePathRewrite: "/",
        configure: (proxy) => {
          proxy.on("proxyReq", (proxyReq) => {
            proxyReq.setHeader("origin", "http://localhost:8080");
          });
        },
      },
      "/me": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
        cookieDomainRewrite: "",
        cookiePathRewrite: "/",
        configure: (proxy) => {
          proxy.on("proxyReq", (proxyReq) => {
            proxyReq.setHeader("origin", "http://localhost:8080");
          });
        },
      },
    },
  },
})
