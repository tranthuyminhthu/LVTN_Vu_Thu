import axiosInstance from ".";

export interface OrderItem {
  productSku: string;
  productName: string;
  quantity: number;
  price: number;
}

export interface Order {
  orderId: string;
  userId: string;
  status: string;
  totalAmount: number;
  paymentStatus: string;
  paymentMethod: string;
  paymentTransactionId: string | null;
  createdAt: string;
  items: OrderItem[] | null;
  receiverName: string | null;
  receiverPhone: string | null;
  receiverEmail: string | null;
  note: string | null;
  timeline: unknown;
  image: string;
}

export const getOrders = async (page = 0, size = 10, status?: string) => {
  let url = `/api/order?page=${page}&size=${size}`;
  if (status) {
    url += `&status=${status}`;
  }
  const res = await axiosInstance.get(url);
  return res.data;
}

export interface CreateOrderPayload {
  items: Array<{
    cartItemId: string;
    productSku: string;
    productName: string;
    quantity: number;
    price: number;
    image?: string;
  }>;
  shippingAddress: string;
  paymentMethod: string;
  receiverName: string;
  receiverPhone: string;
  receiverEmail: string;
  note?: string;
}

export const createOrder = async (orderData: CreateOrderPayload) => {
  const res = await axiosInstance.post('/api/order', orderData);
  return res.data;
}

export const getMyOrders = async (page = 0, size = 10) => {
  try {
    const res = await axiosInstance.get(`/api/order/me?page=${page}&size=${size}`);
    return res.data;
  } catch (error) {
    console.error("Error fetching my orders:", error);
    throw error;
  }
}