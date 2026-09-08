import path from "path"
import tailwindcss from "@tailwindcss/vite"
import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), "");
  const backendTarget = env.VITE_API_BASE_URL || "";
  const frontendPort = Number(env.VITE_FRONTEND_PORT || 5173);

  return {
    plugins: [react(), tailwindcss()],
    resolve: {
      alias: {
        "@": path.resolve(import.meta.dirname, "./src"),
      },
    },
    server: {
      port: frontendPort,
      proxy: {
        "/api": {
          target: backendTarget,
          changeOrigin: true,
          secure: false,
          ws: true,
          cookieDomainRewrite: "",
          cookiePathRewrite: "/",
          configure: (proxy) => {
            proxy.on("proxyReq", (proxyReq) => {
              proxyReq.setHeader("origin", backendTarget);
            });
          },
        },
        "/me": {
          target: backendTarget,
          changeOrigin: true,
          secure: false,
          cookieDomainRewrite: "",
          cookiePathRewrite: "/",
          configure: (proxy) => {
            proxy.on("proxyReq", (proxyReq) => {
              proxyReq.setHeader("origin", backendTarget);
            });
          },
        },
      },
    },
  };
});
