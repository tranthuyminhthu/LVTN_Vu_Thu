import axiosInstance from './index';

export interface Vendor {
  id: string;
  name: string | null;
  description: string | null;
  address: string | null;
  phone: string | null;
  email: string | null;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';
  image: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface VendorResponse {
  vendors: Vendor[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface VendorQueryParams {
  status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';
  page?: number;
  size?: number;
}

export const vendorApi = {
  // Lấy danh sách vendors với pagination và filter
  getVendors: async (params: VendorQueryParams = {}): Promise<VendorResponse> => {
    const response = await axiosInstance.get('/api/profile/vendors', { params });
    return response.data;
  },

  // Lấy chi tiết vendor theo ID
  getVendorById: async (id: string): Promise<Vendor> => {
    const response = await axiosInstance.get(`/api/profile/vendors/${id}`);
    return response.data;
  },

  // Cập nhật trạng thái vendor
  updateVendorStatus: async (id: string, status: Vendor['status']): Promise<Vendor> => {
    const response = await axiosInstance.patch(`/api/profile/vendors/${id}/status`, { status });
    return response.data;
  },

  // Xóa vendor
  deleteVendor: async (id: string): Promise<void> => {
    await axiosInstance.delete(`/api/profile/vendors/${id}`);
  }
}; 