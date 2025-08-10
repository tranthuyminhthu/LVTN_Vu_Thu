import axios from "axios";
import { useAuth } from "../composables/useAuth";
import { refreshToken } from "./auth";

const axiosInstance = axios.create({
  baseURL: "https://9be3dc87ddb5.ngrok-free.app/",
  headers: {
    "ngrok-skip-browser-warning": "true",
  },
});

// Biến để track refresh token đang được xử lý
let isRefreshing = false;
let failedQueue: Array<{
  resolve: (value?: unknown) => void;
  reject: (reason?: unknown) => void;
}> = [];

const processQueue = (error: unknown, token: string | null = null) => {
  console.log('Processing queue:', { error: !!error, token: !!token, queueLength: failedQueue.length });
  failedQueue.forEach(({ resolve, reject }) => {
    if (error) {
      reject(error);
    } else {
      resolve(token);
    }
  });
  
  failedQueue = [];
};

// Request interceptor - tự động thêm access token vào headers
axiosInstance.interceptors.request.use(
  (config) => {
    const { accessToken } = useAuth();
    
    // Không thêm Authorization header cho refresh token requests và public APIs
    if (accessToken.value && 
        !config.url?.includes('/auth/token') && 
        !config.url?.includes('/api/products/latest') &&
        !config.url?.includes('/api/products/most-viewed')) {
      config.headers.Authorization = `Bearer ${accessToken.value}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor - xử lý refresh token khi access token hết hạn
axiosInstance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const { refreshToken: storedRefreshToken, updateAccessToken, logoutWithoutRedirect } = useAuth();

    // Xử lý lỗi 401 (access token hết hạn) và chưa retry
    if (error.response?.status === 401 && !originalRequest._retry) {
      
      // Nếu đang refresh, thêm request vào queue
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosInstance(originalRequest);
        }).catch(err => {
          return Promise.reject(err);
        });
      }

      originalRequest._retry = true;
      isRefreshing = true;

      try {
        // Gọi API refresh token
        const response = await refreshToken(storedRefreshToken.value!);
      
        
        // Cập nhật access token mới
        updateAccessToken(response.accessToken);
        
        // Process queue và retry tất cả failed requests
        processQueue(null, response.accessToken);
        
        // Retry request ban đầu với token mới
        originalRequest.headers.Authorization = `Bearer ${response.accessToken}`;
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Nếu refresh token cũng hết hạn, logout và reject tất cả
        const error = refreshError as { response?: { status?: number; data?: { message?: string } } };
        processQueue(refreshError, null);
        logoutWithoutRedirect();
        // Redirect về trang login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }

    // Xử lý lỗi 400 từ refresh token API (refresh token hết hạn)
    if (error.response?.status === 400 && error.config?.url?.includes('/api/auth/token')) {
      // Clear failed queue trước khi logout
      processQueue(error, null);
      logoutWithoutRedirect();
      // Redirect về trang login
      window.location.href = '/login';
      return Promise.reject(error);
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;