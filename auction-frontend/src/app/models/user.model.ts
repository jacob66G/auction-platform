export enum UserRole {
  USER = 'USER',
  MODERATOR = 'MODERATOR',
  ADMIN = 'ADMIN'
}

export interface User {
  id?: number;
  username: string;
  email: string;
  role?: UserRole;
  balance?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface UserRegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export interface ChangeEmailRequest {
  email: string;
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  role: UserRole;
  balance: number;
  createdAt: string;
  updatedAt: string;
}

export interface DepositRequest {
  amount: number;
}

export interface UserSearchCriteria {
  role?: UserRole;
  searchTerm?: string;
} 