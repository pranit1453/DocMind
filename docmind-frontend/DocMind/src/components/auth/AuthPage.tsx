import { useState } from "react";
import { useAuth } from "@/context/AuthContext";
import { useTheme } from "@/context/ThemeContext";
import {
  useLoginMutation,
  useRegisterMutation,
  useVerifyOtpMutation,
  usePasswordResetRequestMutation,
  useVerifyResetOtpMutation,
  useResetPasswordMutation,
} from "@/hooks/useAuthMutations";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Sparkles,
  Sun,
  Moon,
  Lock,
  Mail,
  User as UserIcon,
  ArrowRight,
  ShieldCheck,
  Check,
  X,
  AlertCircle,
  IdCard,
  KeyRound,
  RefreshCw,
} from "lucide-react";
import { cn } from "@/lib/utils";

import { AnimatedVectorBackground } from "@/components/home/AnimatedVectorBackground";

interface AuthPageProps {
  onGoHome?: () => void;
  onSuccess?: () => void;
}

type AuthMode = "login" | "register" | "verify_otp" | "forgot_password" | "reset_password";

export function AuthPage({ onGoHome, onSuccess }: AuthPageProps) {
  const { login, register } = useAuth();
  const { isDark, toggleTheme } = useTheme();

  // TanStack Query Mutations
  const loginMutation = useLoginMutation();
  const registerMutation = useRegisterMutation();
  const verifyOtpMutation = useVerifyOtpMutation();
  const passwordResetReqMutation = usePasswordResetRequestMutation();
  const verifyResetOtpMutation = useVerifyResetOtpMutation();
  const resetPasswordMutation = useResetPasswordMutation();

  const [mode, setMode] = useState<AuthMode>("login");
  const [fullName, setFullName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [challengeId, setChallengeId] = useState<string>("");

  const [submitted, setSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [showRequirements, setShowRequirements] = useState(false);
  const [isLoggingIn, setIsLoggingIn] = useState(false);

  const isPending =
    isLoggingIn ||
    loginMutation.isPending ||
    registerMutation.isPending ||
    verifyOtpMutation.isPending ||
    passwordResetReqMutation.isPending ||
    verifyResetOtpMutation.isPending ||
    resetPasswordMutation.isPending;

  // Password validation rules
  const hasMinLength = password.length >= 8;
  const hasUpper = /[A-Z]/.test(password);
  const hasLower = /[a-z]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasSpecial = /[@#$%^&+=!]/.test(password);
  const isPasswordValid = hasMinLength && hasUpper && hasLower && hasNumber && hasSpecial;
  const isConfirmValid = password.length > 0 && password === confirmPassword;
  const isUsernameValid = username.trim().length >= 3 && username.trim().length <= 50;
  const isEmailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSubmitted(true);
    setErrorMessage(null);
    setSuccessMessage(null);

    // MODE 1: REGISTER USER
    if (mode === "register") {
      if (!fullName.trim()) {
        setErrorMessage("Please enter your full name.");
        return;
      }
      if (!username.trim() || !isUsernameValid) {
        setErrorMessage("Please enter a valid username (between 3 and 50 characters).");
        return;
      }
      if (!email.trim() || !isEmailValid) {
        setErrorMessage("Please enter a valid email address.");
        return;
      }
      if (!isPasswordValid) {
        setShowRequirements(true);
        setErrorMessage("Password does not meet required security criteria.");
        return;
      }
      if (!isConfirmValid) {
        setErrorMessage("Passwords do not match.");
        return;
      }

      try {
        const res = await registerMutation.mutateAsync({
          fullName,
          username,
          email,
          password,
          confirmPassword,
        });

        if (res.challengeId) {
          setChallengeId(res.challengeId);
          setMode("verify_otp");
          setSuccessMessage(res.message || "Registration initiated! Please enter the OTP sent to your email.");
        } else {
          setSuccessMessage("Registration successful! Signing in...");
          setTimeout(async () => {
            await register(username, email, password, fullName);
            if (onSuccess) onSuccess();
          }, 600);
        }
      } catch (err: any) {
        setErrorMessage(err.message || "Registration failed.");
      }
    }

    // MODE 2: VERIFY REGISTRATION OTP
    else if (mode === "verify_otp") {
      if (!otp.trim()) {
        setErrorMessage("Please enter the verification OTP code.");
        return;
      }

      try {
        const verifyRes = await verifyOtpMutation.mutateAsync({
          challengeId,
          otp,
        });

        setSuccessMessage(verifyRes.message || "Account verified successfully!");
        setTimeout(() => {
          setMode("login");
          setSubmitted(false);
          setErrorMessage(null);
          setSuccessMessage("Account verified successfully! Please sign in with your credentials.");
        }, 1000);
      } catch (err: any) {
        setErrorMessage(err.message || "Invalid OTP verification code.");
      }
    }

    // MODE 3: LOGIN USER
    else if (mode === "login") {
      if (!username.trim()) {
        setErrorMessage("Please enter your username.");
        return;
      }
      if (!password.trim()) {
        setErrorMessage("Please enter your password.");
        return;
      }

      try {
        setIsLoggingIn(true);
        await login(username || email, password);
        if (onSuccess) onSuccess();
      } catch (err: any) {
        setErrorMessage(err.message || "Invalid username or password.");
      } finally {
        setIsLoggingIn(false);
      }
    }

    // MODE 4: FORGOT PASSWORD REQUEST
    else if (mode === "forgot_password") {
      if (!email.trim() || !isEmailValid) {
        setErrorMessage("Please enter a valid email address.");
        return;
      }

      try {
        const resetReq = await passwordResetReqMutation.mutateAsync({ email });
        if (resetReq.challengeId) {
          setChallengeId(resetReq.challengeId);
          setMode("reset_password");
          setSuccessMessage(resetReq.message || "Password reset OTP sent to your email!");
        } else {
          setSuccessMessage("Password reset request submitted.");
        }
      } catch (err: any) {
        setErrorMessage(err.message || "Failed to request password reset.");
      }
    }

    // MODE 5: RESET PASSWORD WITH OTP
    else if (mode === "reset_password") {
      if (!otp.trim()) {
        setErrorMessage("Please enter the OTP verification code.");
        return;
      }
      if (!isPasswordValid) {
        setShowRequirements(true);
        setErrorMessage("New password does not meet security criteria.");
        return;
      }
      if (!isConfirmValid) {
        setErrorMessage("Passwords do not match.");
        return;
      }

      try {
        await verifyResetOtpMutation.mutateAsync({ challengeId, otp });
        await resetPasswordMutation.mutateAsync({
          email,
          passwordRequest: { password, confirmPassword },
        });

        setSuccessMessage("Password reset successful! Please sign in with your new password.");
        setTimeout(() => {
          setMode("login");
        }, 1200);
      } catch (err: any) {
        setErrorMessage(err.message || "Failed to reset password.");
      }
    }
  };

  const fullNameError = submitted && mode === "register" && !fullName.trim() ? "Full name is required" : null;
  const usernameError =
    submitted && (mode === "register" || mode === "login")
      ? mode === "register" && !isUsernameValid
        ? "Username required (3 to 50 chars)"
        : !username.trim()
        ? "Username is required"
        : null
      : null;
  const emailError =
    submitted && (mode === "register" || mode === "forgot_password") && !isEmailValid
      ? "Valid email address is required"
      : null;
  const passwordError = submitted && (mode === "login" || mode === "register" || mode === "reset_password") && !password.trim() ? "Password is required" : null;
  const confirmError = submitted && (mode === "register" || mode === "reset_password") && !isConfirmValid ? "Passwords do not match" : null;

  return (
    <div className="relative flex min-h-screen w-screen items-center justify-center bg-background px-4 py-12 select-none overflow-hidden font-sans">
      <AnimatedVectorBackground />
      {/* Background Glow */}
      <div className="absolute top-1/4 left-1/2 -translate-x-1/2 -translate-y-1/2 h-96 w-96 rounded-full bg-primary/10 blur-3xl pointer-events-none" />

      {/* Top Corner Theme Switcher */}
      <div className="absolute top-5 right-5 z-20">
        <Button
          variant="outline"
          size="icon"
          className="h-9 w-9 rounded-xl border-border bg-card cursor-pointer"
          onClick={toggleTheme}
          title="Toggle Theme"
        >
          {isDark ? <Moon size={16} className="text-blue-400" /> : <Sun size={16} className="text-amber-500" />}
        </Button>
      </div>

      {/* Main Auth Card */}
      <Card className="relative z-10 w-full max-w-md rounded-2xl border-border/80 bg-card/90 backdrop-blur-xl shadow-2xl">
        <CardHeader className="space-y-3 p-6 text-center">
          <div
            onClick={onGoHome}
            className="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10 text-primary border border-primary/20 shadow-md cursor-pointer hover:scale-105 transition-transform"
            title="Go to DocMind Home"
          >
            <Sparkles size={24} className="animate-pulse" />
          </div>

          <div>
            <div
              onClick={onGoHome}
              className="flex items-center justify-center gap-2 cursor-pointer group"
              title="Go to DocMind Home"
            >
              <CardTitle className="text-xl font-bold tracking-tight text-foreground group-hover:text-primary transition-colors">
                DocMind Workspace
              </CardTitle>
              <Badge variant="secondary" className="text-[9px] bg-primary/10 text-primary">
                AI RAG
              </Badge>
            </div>
            <CardDescription className="mt-1 text-xs text-muted-foreground">
              {mode === "login"
                ? "Sign in to access your knowledge workspace"
                : mode === "register"
                ? "Create your account to start uploading documents"
                : mode === "verify_otp"
                ? "Enter OTP sent to your registered email"
                : "Reset your account password"}
            </CardDescription>
          </div>

          {/* Mode Switcher Tabs */}
          {(mode === "login" || mode === "register") && (
            <div className="grid grid-cols-2 gap-1 rounded-xl bg-muted/60 p-1 text-xs font-semibold">
              <button
                type="button"
                onClick={() => {
                  setMode("login");
                  setSubmitted(false);
                  setErrorMessage(null);
                  setSuccessMessage(null);
                  setShowRequirements(false);
                }}
                className={`rounded-lg py-1.5 transition-all cursor-pointer ${
                  mode === "login"
                    ? "bg-background text-foreground shadow-xs"
                    : "text-muted-foreground hover:text-foreground"
                }`}
              >
                Sign In
              </button>
              <button
                type="button"
                onClick={() => {
                  setMode("register");
                  setSubmitted(false);
                  setErrorMessage(null);
                  setSuccessMessage(null);
                  setShowRequirements(false);
                }}
                className={`rounded-lg py-1.5 transition-all cursor-pointer ${
                  mode === "register"
                    ? "bg-background text-foreground shadow-xs"
                    : "text-muted-foreground hover:text-foreground"
                }`}
              >
                Register
              </button>
            </div>
          )}
        </CardHeader>

        <CardContent className="p-6 pt-0">
          {errorMessage && (
            <div className="mb-4 flex items-center gap-2 rounded-xl border border-destructive/30 bg-destructive/10 p-3 text-xs text-destructive">
              <AlertCircle size={15} className="shrink-0" />
              <span>{errorMessage}</span>
            </div>
          )}

          {successMessage && (
            <div className="mb-4 flex items-center gap-2 rounded-xl border border-emerald-500/30 bg-emerald-500/10 p-3 text-xs text-emerald-500">
              <Check size={15} className="shrink-0" />
              <span>{successMessage}</span>
            </div>
          )}

          <form onSubmit={handleSubmit} noValidate className="space-y-3.5">
            {/* REGISTER: Full Name Input */}
            {mode === "register" && (
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                  <IdCard size={12} className="text-primary" /> Full Name
                </label>
                <Input
                  type="text"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  placeholder="Enter your full name"
                  className={cn("h-9 text-xs transition-colors", fullNameError && "border-destructive ring-1 ring-destructive/20")}
                />
                {fullNameError && (
                  <p className="text-[10px] font-medium text-destructive flex items-center gap-1 mt-1">
                    <AlertCircle size={11} /> {fullNameError}
                  </p>
                )}
              </div>
            )}

            {/* LOGIN & REGISTER: Username Input */}
            {(mode === "login" || mode === "register") && (
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                  <UserIcon size={12} className="text-primary" /> Username
                </label>
                <Input
                  type="text"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  placeholder="Enter your username"
                  className={cn("h-9 text-xs transition-colors", usernameError && "border-destructive ring-1 ring-destructive/20")}
                />
                {usernameError && (
                  <p className="text-[10px] font-medium text-destructive flex items-center gap-1 mt-1">
                    <AlertCircle size={11} /> {usernameError}
                  </p>
                )}
              </div>
            )}

            {/* REGISTER & FORGOT PASSWORD: Email Input */}
            {(mode === "register" || mode === "forgot_password") && (
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                  <Mail size={12} className="text-primary" /> Email Address
                </label>
                <Input
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@company.com"
                  className={cn("h-9 text-xs transition-colors", emailError && "border-destructive ring-1 ring-destructive/20")}
                />
                {emailError && (
                  <p className="text-[10px] font-medium text-destructive flex items-center gap-1 mt-1">
                    <AlertCircle size={11} /> {emailError}
                  </p>
                )}
              </div>
            )}

            {/* OTP VERIFICATION INPUT */}
            {(mode === "verify_otp" || mode === "reset_password") && (
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                  <KeyRound size={12} className="text-primary" /> Enter 6-Digit OTP Code
                </label>
                <Input
                  type="text"
                  value={otp}
                  onChange={(e) => setOtp(e.target.value)}
                  placeholder="123456"
                  maxLength={10}
                  className="h-9 text-xs tracking-widest font-mono text-center"
                />
              </div>
            )}

            {/* LOGIN & REGISTER & RESET PASSWORD: Password Input */}
            {(mode === "login" || mode === "register" || mode === "reset_password") && (
              <div className="space-y-1">
                <div className="flex items-center justify-between">
                  <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                    <Lock size={12} className="text-primary" /> {mode === "reset_password" ? "New Password" : "Password"}
                  </label>
                  {mode === "login" && (
                    <button
                      type="button"
                      onClick={() => {
                        setMode("forgot_password");
                        setSubmitted(false);
                        setErrorMessage(null);
                        setSuccessMessage(null);
                      }}
                      className="text-[10px] text-primary hover:underline font-medium cursor-pointer"
                    >
                      Forgot password?
                    </button>
                  )}
                </div>
                <Input
                  type="password"
                  value={password}
                  onChange={(e) => {
                    setPassword(e.target.value);
                    if (errorMessage) setErrorMessage(null);
                  }}
                  placeholder={mode === "reset_password" ? "Enter new password" : "Enter your password"}
                  className={cn("h-9 text-xs transition-colors", passwordError && "border-destructive ring-1 ring-destructive/20")}
                />
                {passwordError && (
                  <p className="text-[10px] font-medium text-destructive flex items-center gap-1 mt-1">
                    <AlertCircle size={11} /> {passwordError}
                  </p>
                )}
              </div>
            )}

            {/* Password Requirements Checklist */}
            {(mode === "register" || mode === "reset_password") && showRequirements && (
              <div className="space-y-2 rounded-xl border border-destructive/30 bg-destructive/5 p-3 text-[10px] animate-in fade-in-0 duration-200">
                <p className="font-bold text-destructive uppercase tracking-wider text-[9px] flex items-center gap-1">
                  <AlertCircle size={11} /> Security Password Requirements
                </p>
                <div className="grid grid-cols-2 gap-1.5">
                  <RuleCheck label="8+ characters" valid={hasMinLength} />
                  <RuleCheck label="1 Uppercase (A-Z)" valid={hasUpper} />
                  <RuleCheck label="1 Lowercase (a-z)" valid={hasLower} />
                  <RuleCheck label="1 Number (0-9)" valid={hasNumber} />
                  <RuleCheck label="1 Special (@#$%^&+=!)" valid={hasSpecial} />
                  <RuleCheck label="Passwords Match" valid={isConfirmValid} />
                </div>
              </div>
            )}

            {/* Confirm Password Input for Register / Reset */}
            {(mode === "register" || mode === "reset_password") && (
              <div className="space-y-1">
                <label className="text-[11px] font-semibold text-foreground flex items-center gap-1.5">
                  <Lock size={12} className="text-primary" /> Confirm Password
                </label>
                <Input
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  placeholder="Confirm password"
                  className={cn("h-9 text-xs transition-colors", confirmError && "border-destructive ring-1 ring-destructive/20")}
                />
                {confirmError && (
                  <p className="text-[10px] font-medium text-destructive flex items-center gap-1 mt-1">
                    <AlertCircle size={11} /> {confirmError}
                  </p>
                )}
              </div>
            )}

            <Button
              type="submit"
              disabled={isPending}
              className="w-full h-9 rounded-xl text-xs font-bold gap-2 shadow-md shadow-primary/10 cursor-pointer mt-2"
            >
              {isPending ? (
                <span className="flex items-center gap-1.5">
                  <RefreshCw size={13} className="animate-spin" /> Processing...
                </span>
              ) : (
                <>
                  <span>
                    {mode === "login"
                      ? "Sign In"
                      : mode === "register"
                      ? "Create Account"
                      : mode === "verify_otp"
                      ? "Verify OTP Code"
                      : mode === "forgot_password"
                      ? "Request Reset Link"
                      : "Reset Password"}
                  </span>
                  <ArrowRight size={14} />
                </>
              )}
            </Button>

            {mode !== "login" && (
              <div className="text-center mt-2">
                <button
                  type="button"
                  onClick={() => {
                    setMode("login");
                    setSubmitted(false);
                    setErrorMessage(null);
                    setSuccessMessage(null);
                  }}
                  className="text-[11px] text-muted-foreground hover:text-foreground font-medium cursor-pointer"
                >
                  ← Back to Sign In
                </button>
              </div>
            )}
          </form>

          <div className="mt-6 flex items-center justify-center text-emerald-500">
            <ShieldCheck size={16} />
          </div>
        </CardContent>
      </Card>
    </div>
  );
}

function RuleCheck({ label, valid }: { label: string; valid: boolean }) {
  return (
    <div className="flex items-center gap-1">
      {valid ? (
        <Check size={11} className="text-emerald-500 shrink-0" />
      ) : (
        <X size={11} className="text-destructive shrink-0" />
      )}
      <span className={valid ? "text-emerald-500 font-semibold" : "text-destructive font-medium"}>
        {label}
      </span>
    </div>
  );
}
