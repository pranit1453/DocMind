import { useMutation } from "@tanstack/react-query";
import {
  loginUser,
  registerUser,
  verifyRegistrationOtp,
  verifyPasswordResetOtp,
  requestPasswordReset,
  resetPassword,
  changePassword,
  deactivateAccount,
} from "@/api/authApi";
import type {
  LoginRequest,
  LoginResponse,
  SignupRequest,
  SignupResponse,
  VerifyOtpRequest,
  VerificationResponse,
  ForgotPasswordEmail,
  PasswordResponse,
  ChangeForgotPasswordRequest,
  ChangePassword,
  ChangePasswordResponse,
} from "@/api/types";

export function useLoginMutation() {
  return useMutation<LoginResponse, Error, LoginRequest>({
    mutationFn: (request) => loginUser(request),
  });
}

export function useRegisterMutation() {
  return useMutation<SignupResponse, Error, SignupRequest>({
    mutationFn: (request) => registerUser(request),
  });
}

export function useVerifyOtpMutation() {
  return useMutation<VerificationResponse, Error, VerifyOtpRequest>({
    mutationFn: (request) => verifyRegistrationOtp(request),
  });
}

export function usePasswordResetRequestMutation() {
  return useMutation<PasswordResponse, Error, ForgotPasswordEmail>({
    mutationFn: (request) => requestPasswordReset(request),
  });
}

export function useVerifyResetOtpMutation() {
  return useMutation<VerificationResponse, Error, VerifyOtpRequest>({
    mutationFn: (request) => verifyPasswordResetOtp(request),
  });
}

export function useResetPasswordMutation() {
  return useMutation<VerificationResponse, Error, ChangeForgotPasswordRequest>({
    mutationFn: (request) => resetPassword(request.email, request.passwordRequest),
  });
}

export function useChangePasswordMutation() {
  return useMutation<ChangePasswordResponse, Error, ChangePassword>({
    mutationFn: (request) => changePassword(request),
  });
}

export function useDeactivateAccountMutation() {
  return useMutation<VerificationResponse, Error, void>({
    mutationFn: () => deactivateAccount(),
  });
}
