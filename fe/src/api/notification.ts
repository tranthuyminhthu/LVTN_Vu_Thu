import axiosInstance from './index';

// Notification Types
export interface Notification {
  id: number;
  userId: string;
  type: 'PUSH' | 'EMAIL' | 'SMS';
  category: string;
  title: string;
  content: string;
  templateId: string | null;
  status: 'DRAFT' | 'SCHEDULED' | 'SENT' | 'FAILED' | 'READ';
  recipient: string;
  data: string | null;
  scheduledAt: string | null;
  sentAt: string | null;
  readAt: string | null;
  errorMessage: string | null;
  retryCount: number;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationResponse {
  notifications: Notification[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

export interface NotificationQueryParams {
  page?: number;
  size?: number;
  status?: string;
  type?: string;
  category?: string;
}

// API functions
export const getNotifications = async (
  page = 0,
  size = 20,
  status?: string,
  type?: string,
  category?: string
): Promise<NotificationResponse> => {
  const params: NotificationQueryParams = { page, size };
  
  if (status) params.status = status;
  if (type) params.type = type;
  if (category) params.category = category;
  
  const response = await axiosInstance.get('/api/notifications/list-paginated', {
    params
  });
  return response.data;
};

export const markNotificationAsRead = async (notificationId: number): Promise<void> => {
  await axiosInstance.put(`/api/notifications/${notificationId}/read`);
};

export const deleteNotification = async (notificationId: number): Promise<void> => {
  await axiosInstance.delete(`/api/notifications/${notificationId}`);
};

export const markAllNotificationsAsRead = async (): Promise<void> => {
  await axiosInstance.put('/api/notifications/mark-all-read');
}; 