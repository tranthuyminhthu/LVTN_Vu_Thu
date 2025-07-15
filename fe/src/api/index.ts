import axios from "axios";
import { useAuth } from "../composables/useAuth";
import { refreshToken } from "./auth";

const axiosInstance = axios.create({
  baseURL: "http://localhost:8888",
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
    
    // Không thêm Authorization header cho refresh token requests
    if (accessToken.value && !config.url?.includes('/auth/token')) {
      config.headers.Authorization = `Bearer ${accessToken.value}`;
      console.log('Added Authorization header for request:', config.url);
    }
    
    return config;
  },
  (error) => {
    console.log('Request interceptor error:', error);
    return Promise.reject(error);
  }
);

// Response interceptor - xử lý refresh token khi access token hết hạn
axiosInstance.interceptors.response.use(
  (response) => {
    console.log('Response success:', {
      url: response.config.url,
      status: response.status
    });
    return response;
  },
  async (error) => {
    const originalRequest = error.config;
    const { refreshToken: storedRefreshToken, updateAccessToken, logoutWithoutRedirect } = useAuth();

    // Debug logging
    console.log('Response interceptor error:', {
      status: error.response?.status,
      url: error.config?.url,
      message: error.response?.data?.message,
      isRetry: originalRequest._retry,
      isRefreshing: isRefreshing
    });

    // Xử lý lỗi 401 (access token hết hạn) và chưa retry
    if (error.response?.status === 401 && !originalRequest._retry) {
      console.log('Handling 401 error - access token expired');
      
      // Nếu đang refresh, thêm request vào queue
      if (isRefreshing) {
        console.log('Already refreshing, adding request to queue');
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject });
        }).then(token => {
          console.log('Request from queue resolved with token:', !!token);
          originalRequest.headers.Authorization = `Bearer ${token}`;
          return axiosInstance(originalRequest);
        }).catch(err => {
          console.log('Request from queue rejected:', err);
          return Promise.reject(err);
        });
      }

      console.log('Starting token refresh process');
      originalRequest._retry = true;
      isRefreshing = true;

      try {
        console.log('Calling refresh token API with stored refresh token:', !!storedRefreshToken.value);
        // Gọi API refresh token
        const response = await refreshToken(storedRefreshToken.value!);
        
        console.log('Refresh token API response received:', {
          hasAccessToken: !!response.accessToken
        });
        
        // Cập nhật access token mới
        updateAccessToken(response.accessToken);
        console.log('Access token updated successfully');
        
        // Process queue và retry tất cả failed requests
        processQueue(null, response.accessToken);
        
        // Retry request ban đầu với token mới
        originalRequest.headers.Authorization = `Bearer ${response.accessToken}`;
        console.log('Retrying original request with new token');
        return axiosInstance(originalRequest);
      } catch (refreshError) {
        // Nếu refresh token cũng hết hạn, logout và reject tất cả
        const error = refreshError as { response?: { status?: number; data?: { message?: string } } };
        console.log('Refresh token failed:', {
          error: refreshError,
          status: error.response?.status,
          message: error.response?.data?.message
        });
        console.log('Refresh token expired (400 error), logging out user and redirecting to login');
        processQueue(refreshError, null);
        logoutWithoutRedirect();
        // Redirect về trang login
        window.location.href = '/login';
        return Promise.reject(refreshError);
      } finally {
        console.log('Refresh process completed, setting isRefreshing to false');
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

    console.log('Error not handled by interceptors, rejecting:', error);
    return Promise.reject(error);
  }
);

export default axiosInstance;