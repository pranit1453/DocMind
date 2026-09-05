import { useEffect, useRef } from "react";
import { useTheme } from "@/context/ThemeContext";

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  radius: number;
  baseAlpha: number;
  pulse: number;
  pulseSpeed: number;
}

interface ConnectionPulse {
  from: Particle;
  to: Particle;
  progress: number;
  speed: number;
}

export function AnimatedVectorBackground() {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const { isDark } = useTheme();

  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas) return;
    const ctx = canvas.getContext("2d");
    if (!ctx) return;

    let animationFrameId: number;
    let width = (canvas.width = window.innerWidth);
    let height = (canvas.height = window.innerHeight);

    const handleResize = () => {
      if (!canvas) return;
      width = canvas.width = window.innerWidth;
      height = canvas.height = window.innerHeight;
    };

    window.addEventListener("resize", handleResize);

    // Mouse tracking for interactive connection
    const mouse = {
      x: -1000,
      y: -1000,
      radius: 190,
    };

    const handleMouseMove = (e: MouseEvent) => {
      const rect = canvas.getBoundingClientRect();
      mouse.x = e.clientX - rect.left;
      mouse.y = e.clientY - rect.top;
    };

    const handleMouseLeave = () => {
      mouse.x = -1000;
      mouse.y = -1000;
    };

    window.addEventListener("mousemove", handleMouseMove);
    window.addEventListener("mouseleave", handleMouseLeave);

    // Generate RAG Vector Node Particles
    const count = Math.min(Math.floor((width * height) / 16000), 80);
    const particles: Particle[] = [];

    for (let i = 0; i < count; i++) {
      particles.push({
        x: Math.random() * width,
        y: Math.random() * height,
        vx: (Math.random() - 0.5) * 0.45,
        vy: (Math.random() - 0.5) * 0.45,
        radius: Math.random() * 2 + 1.2,
        baseAlpha: Math.random() * 0.5 + 0.25,
        pulse: Math.random() * Math.PI * 2,
        pulseSpeed: 0.015 + Math.random() * 0.025,
      });
    }

    const pulses: ConnectionPulse[] = [];

    const draw = () => {
      ctx.clearRect(0, 0, width, height);

      const primaryColor = isDark ? "99, 102, 241" : "59, 130, 246"; // Indigo / Primary blue
      const cyanColor = isDark ? "56, 189, 248" : "14, 165, 233"; // Vibrant Sky Cyan

      // Update and draw particles
      particles.forEach((p) => {
        p.x += p.vx;
        p.y += p.vy;

        // Bounce smoothly on edges
        if (p.x < 0 || p.x > width) p.vx *= -1;
        if (p.y < 0 || p.y > height) p.vy *= -1;

        p.pulse += p.pulseSpeed;
        const currentAlpha = p.baseAlpha + Math.sin(p.pulse) * 0.2;

        // Draw particle node
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(${cyanColor}, ${Math.max(0.1, currentAlpha)})`;
        ctx.fill();

        // Subtle glowing aura around node
        ctx.beginPath();
        ctx.arc(p.x, p.y, p.radius * 2.8, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(${primaryColor}, ${Math.max(0.02, currentAlpha * 0.15)})`;
        ctx.fill();
      });

      // Draw vector connection lines between nearby particles
      const maxDistance = 145;
      for (let i = 0; i < particles.length; i++) {
        for (let j = i + 1; j < particles.length; j++) {
          const p1 = particles[i];
          const p2 = particles[j];
          const dx = p1.x - p2.x;
          const dy = p1.y - p2.y;
          const dist = Math.sqrt(dx * dx + dy * dy);

          if (dist < maxDistance) {
            const alpha = (1 - dist / maxDistance) * (isDark ? 0.3 : 0.18);
            ctx.beginPath();
            ctx.moveTo(p1.x, p1.y);
            ctx.lineTo(p2.x, p2.y);
            ctx.strokeStyle = `rgba(${primaryColor}, ${alpha})`;
            ctx.lineWidth = 0.9;
            ctx.stroke();

            // Randomly spawn traveling vector signal pulses along connected nodes
            if (Math.random() < 0.0006 && pulses.length < 18) {
              pulses.push({
                from: p1,
                to: p2,
                progress: 0,
                speed: 0.012 + Math.random() * 0.02,
              });
            }
          }
        }

        // Draw interactive connection line to user mouse cursor
        const p1 = particles[i];
        const mdx = p1.x - mouse.x;
        const mdy = p1.y - mouse.y;
        const mdist = Math.sqrt(mdx * mdx + mdy * mdy);
        if (mdist < mouse.radius) {
          const alpha = (1 - mdist / mouse.radius) * (isDark ? 0.5 : 0.35);
          ctx.beginPath();
          ctx.moveTo(p1.x, p1.y);
          ctx.lineTo(mouse.x, mouse.y);
          ctx.strokeStyle = `rgba(${cyanColor}, ${alpha})`;
          ctx.lineWidth = 1.3;
          ctx.stroke();
        }
      }

      // Draw active vector signal data pulses
      for (let i = pulses.length - 1; i >= 0; i--) {
        const pulse = pulses[i];
        pulse.progress += pulse.speed;

        if (pulse.progress >= 1) {
          pulses.splice(i, 1);
          continue;
        }

        const px = pulse.from.x + (pulse.to.x - pulse.from.x) * pulse.progress;
        const py = pulse.from.y + (pulse.to.y - pulse.from.y) * pulse.progress;

        ctx.beginPath();
        ctx.arc(px, py, 2.5, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(${cyanColor}, ${isDark ? 0.95 : 0.8})`;
        ctx.fill();
      }

      animationFrameId = requestAnimationFrame(draw);
    };

    draw();

    return () => {
      cancelAnimationFrame(animationFrameId);
      window.removeEventListener("resize", handleResize);
      window.removeEventListener("mousemove", handleMouseMove);
      window.removeEventListener("mouseleave", handleMouseLeave);
    };
  }, [isDark]);

  return (
    <div className="fixed inset-0 pointer-events-none z-0 overflow-hidden">
      {/* Soft Ambient Glowing Orbs */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[550px] h-[550px] bg-primary/10 rounded-full blur-[150px] animate-pulse pointer-events-none" />
      <div className="absolute bottom-12 left-12 w-[380px] h-[380px] bg-sky-500/10 rounded-full blur-[130px] pointer-events-none" />
      <div className="absolute top-1/3 right-12 w-[400px] h-[400px] bg-indigo-500/10 rounded-full blur-[140px] pointer-events-none" />

      {/* Subtle Vector Dot Matrix Overlay */}
      <div
        className="absolute inset-0 opacity-[0.04] dark:opacity-[0.08] pointer-events-none"
        style={{
          backgroundImage: `radial-gradient(circle at 1px 1px, currentColor 1px, transparent 0)`,
          backgroundSize: "32px 32px",
        }}
      />

      {/* Interactive Canvas Mesh */}
      <canvas ref={canvasRef} className="absolute inset-0 block w-full h-full" />
    </div>
  );
}
