import type { CartItem } from "@/types";
import axiosInstance from ".";

export const getCart = async () => {
    try {
        const response = await axiosInstance.get(`/api/carts`);
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const addItemToCart = async (payload: CartItem) => {
    try {
        const response = await axiosInstance.post(`/api/carts/items`, payload);
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const removeCartItem = async (itemId: string) => {
    try {
        const response = await axiosInstance.delete(`/api/carts/items/${itemId}`);
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const updateCartItemQuantity = async (itemId: string, quantity: number) => {
    try {
        const response = await axiosInstance.put(`/api/carts/items/${itemId}`, { quantity });
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};

export const removeCartItems = async (itemIds: string[]) => {
    try {
        const response = await axiosInstance.delete(`/api/carts/items`, { data: itemIds });
        return Promise.resolve(response.data);
    } catch (error) {
        return Promise.reject(error);
    }
};
