import axiosInstance from './index';

// Vendor Management Types
export interface Vendor {
  id: string;
  shopId: number;
  name: string;
  description: string;
  address: string;
  phone: string;
  email: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED';
  image: string;
  wardName: string;
  districtName: string;
  provinceName: string;
  districtId: number;
  wardId: string;
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

// Revenue Stats Types
export interface DailyRevenue {
  date: string;
  revenue: number;
  orderCount: number;
}

export interface TopProduct {
  productSku: string;
  productName: string;
  quantitySold: number;
  totalRevenue: number;
}

export interface MonthlyRevenue {
  year: number;
  month: number;
  revenue: number;
  orderCount: number;
}

export interface OrderStatusStat {
  status: string;
  count: number;
  revenue: number;
}

export interface RevenueStats {
  vendorId: string;
  vendorName: string;
  totalRevenue: number;
  totalOrders: number;
  averageOrderValue: number;
  startDate?: string;
  endDate?: string;
  dailyRevenue?: DailyRevenue[];
  monthlyRevenue?: MonthlyRevenue[];
  topProducts?: TopProduct[];
  orderStatusStats?: OrderStatusStat[];
}

// API functions
export const getVendorRevenueStats = async (
  startDate: string,
  endDate: string
): Promise<RevenueStats> => {
  const response = await axiosInstance.get('/api/profile/vendors/me/revenue-stats', {
    params: { startDate, endDate }
  });
  return response.data;
};

export const getVendorMonthlyRevenueStats = async (
  year?: number
): Promise<RevenueStats> => {
  const params = year ? { year } : {};
  const response = await axiosInstance.get('/api/profile/vendors/me/revenue-stats/monthly', {
    params
  });
  return response.data;
};

export const getVendorTotalRevenueStats = async (): Promise<RevenueStats> => {
  const response = await axiosInstance.get('/api/profile/vendors/me/revenue-stats/total');
  return response.data;
};

// Admin functions (for specific vendor)
export const getVendorRevenueStatsById = async (
  vendorId: string,
  startDate: string,
  endDate: string
): Promise<RevenueStats> => {
  const response = await axiosInstance.get(`/api/profile/vendors/${vendorId}/revenue-stats`, {
    params: { startDate, endDate }
  });
  return response.data;
};

export const getVendorMonthlyRevenueStatsById = async (
  vendorId: string,
  year?: number
): Promise<RevenueStats> => {
  const params = year ? { year } : {};
  const response = await axiosInstance.get(`/api/profile/vendors/${vendorId}/revenue-stats/monthly`, {
    params
  });
  return response.data;
};

export const getVendorTotalRevenueStatsById = async (
  vendorId: string
): Promise<RevenueStats> => {
  const response = await axiosInstance.get(`/api/profile/vendors/${vendorId}/revenue-stats/total`);
  return response.data;
};

// Get current vendor info
export const getCurrentVendor = async (): Promise<Vendor> => {
  const response = await axiosInstance.get('/api/profile/vendors/me');
  return response.data;
};

// Vendor Management API
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
  },

  // Accept multiple vendors
  acceptVendors: async (vendorIds: string[]): Promise<void> => {
    await axiosInstance.post('/api/profile/vendors/accepted', vendorIds);
  }
}; 