import type { OrderAcceptedRequestDto, Order } from "@/types";
import axiosInstance from ".";
import type { LocationQuery } from "vue-router";

export const getOrders = async (page = 0, size = 10, status?: string) => {
  let url = `/api/order/vendors?page=${page}&size=${size}`;
  if (status) {
    url += `&status=${status}`;
  }
  const res = await axiosInstance.get(url);
  return res.data;
}

export const getAdminOrders = async (page = 0, size = 10, status?: string) => {
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
  fee: number;
  vendorId: string;
  receiverDistrictId: number;
  receiverWardCode: string;
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

export const getOrderDetail = async (orderId: string) : Promise<Order> => {
  const res = await axiosInstance.get(`/api/order/${orderId}`);
  return res.data;
};

export const changeOrderStatusToDelivering= async (data: OrderAcceptedRequestDto) => {
  const res = await axiosInstance.put(`/api/order/delivering`, data);
  return res.data;
};

export const changeOrderStatusToAccepted = async (orderId: string) => {
  const res = await axiosInstance.put(`/api/order/accepted` , {
    orderId: orderId
  });
  return res.data;
};

export const changeOrderStatusToReceived = async (orderId: string) => {
  const res = await axiosInstance.put(`/api/order/received` , {
    orderId: orderId
  });
  return res.data;
};

export const changeOrderStatusToCancelled = async (orderId: string, note: string) => {
  const res = await axiosInstance.put(`/api/order/cancelled` , {
    orderId: orderId,
    note: note
  });
  return res.data;
};

export const vnpayReturn = async (params: LocationQuery) => {
  const res = await axiosInstance.post(`/api/order/payment`, params);
  return res.data;
};