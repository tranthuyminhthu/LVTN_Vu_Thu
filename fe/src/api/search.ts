import axiosInstance from "./index";

export const searchProducts = async (query: string, page = 0, size = 10) => {
    const response = await axiosInstance.get("/api/products/search", {
        params: { query, page, size }
    });
    return response.data;
};