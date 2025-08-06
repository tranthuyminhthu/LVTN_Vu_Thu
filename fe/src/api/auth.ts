import axiosInstance from ".";
import type { RegisterData, LoginData } from "../types";
import axios from "axios";

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  userProfile: {
    userId: string;
    email: string;
    username: string;
    firstName: string | null;
    lastName: string | null;
    dob: string | null;
  };
}

export interface UserInfo {
  id: string;
  username: string;
  email: string;
  role: string;
}

export const register = async (data: RegisterData): Promise<void> => {
    try {
        const response = await axiosInstance.post("/api/auth/register", data);
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const login = async (data: LoginData): Promise<AuthResponse> => {
    try {
        const response = await axiosInstance.post("/api/auth/login", data);
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const getUserInfo = async (): Promise<UserInfo> => {
    try {
        const response = await axiosInstance.get("/api/auth/me");
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const refreshToken = async (refreshToken: string): Promise<{ accessToken: string }> => {
    try {
        console.log('Calling refresh token API');
        const response = await axiosInstance.post("/api/auth/token", { refreshToken });
        console.log('Refresh token API response:', { hasAccessToken: !!response.data.accessToken });
        return Promise.resolve(response.data);
    } catch (error) {
        console.log('Refresh token API error:', error);
        return Promise.reject(error);
    }
};

export const logout = async (): Promise<void> => {
    try {
        console.log('Calling logout API');
        await axiosInstance.post("/api/auth/logout");
        console.log('Logout API success');
        return Promise.resolve();
    } catch (error) {
        console.log('Logout API error:', error);
        return Promise.reject(error);
    }
};

export const forgotPassword = async (email: string): Promise<void> => {
    try {
        await axiosInstance.post("/api/auth/forgot-password", { email });
        return Promise.resolve();
    } catch (error) {
        return Promise.reject(error);
    }
};

export interface ResetPasswordData {
    token: string;
    newPassword: string;
    confirmPassword: string;
}

export const resetPassword = async (data: ResetPasswordData): Promise<void> => {
    try {
        await axiosInstance.post("/api/auth/reset-password", data);
        return Promise.resolve();
    } catch (error) {
        return Promise.reject(error);
    }
};