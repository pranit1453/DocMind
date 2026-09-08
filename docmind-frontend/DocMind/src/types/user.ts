export type UserProfile = {
  userId: string;
  username: string;
  fullName?: string;
  email: string;
  avatarFallback: string;
  enabled?: boolean;
  deleted?: boolean;
  role?: string;
  roleName?: string;
  roles?: string[];
  createdAt?: string;
};
