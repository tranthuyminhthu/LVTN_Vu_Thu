import axiosInstance from './index';
import type { Product } from '@/types';

export interface UserProfile {
  userId: string;
  email: string;
  username: string;
  fullName: string | null;
  type: string | null;
  image: string | null;
  height: number | null;
  weight: number | null;
  gender: string | null;
  shopName: string | null;
  role: string | null;
  dob: string | null;
  phone?: string | null;
}

export interface UserResponse {
  profiles: UserProfile[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface UserQueryParams {
  page?: number;
  size?: number;
}

export interface ProfileRequestDto {
  email?: string;
  phone?: string;
  fullName?: string;
  image?: string;
  height?: number;
  weight?: number;
  gender?: string;
  dob?: string;
}

export interface FavoriteProductsResponse {
  products: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FavoriteProductsQueryParams {
  page?: number;
  size?: number;
}

export const userApi = {
  // Lấy danh sách users với pagination
  getUsers: async (params: UserQueryParams = {}): Promise<UserResponse> => {
    const response = await axiosInstance.get('/api/profile', { params });
    return response.data;
  },

  // Lấy chi tiết user theo ID
  getUserById: async (id: string): Promise<UserProfile> => {
    const response = await axiosInstance.get(`/api/profile/${id}`);
    return response.data;
  },

  // Cập nhật trạng thái user
  updateUserStatus: async (id: string, status: string): Promise<UserProfile> => {
    const response = await axiosInstance.patch(`/api/profile/${id}/status`, { status });
    return response.data;
  },

  // Xóa user
  deleteUser: async (id: string): Promise<void> => {
    await axiosInstance.delete(`/api/profile/${id}`);
  },

  getCurrentProfile: async (): Promise<UserProfile> => {
    try {
      const response = await axiosInstance.get("/api/profile/current");
      return response.data;
    } catch (error) {
      console.error('Error fetching current profile:', error);
      throw error;
    }
  },

  updateProfile: async (profileData: ProfileRequestDto): Promise<UserProfile> => {
    try {
      const response = await axiosInstance.put("/api/profile", profileData);
      return response.data;
    } catch (error) {
      console.error('Error updating profile:', error);
      throw error;
    }
  },

  // Lấy danh sách sản phẩm yêu thích với pagination
  getFavoriteProducts: async (params: FavoriteProductsQueryParams = {}): Promise<FavoriteProductsResponse> => {
    try {
      const response = await axiosInstance.get('/api/products/favorites', { params });
      return response.data;
    } catch (error) {
      console.error('Error fetching favorite products:', error);
      throw error;
    }
  },

  // Thêm sản phẩm vào danh sách yêu thích
  addToFavorites: async (productId: string): Promise<void> => {
    try {
      await axiosInstance.post('/api/favorites', { productId });
    } catch (error) {
      console.error('Error adding to favorites:', error);
      throw error;
    }
  },

  // Xóa sản phẩm khỏi danh sách yêu thích
  removeFromFavorites: async (productId: string): Promise<void> => {
    try {
      await axiosInstance.delete(`/api/favorites/${productId}`);
    } catch (error) {
      console.error('Error removing from favorites:', error);
      throw error;
    }
  }
};
