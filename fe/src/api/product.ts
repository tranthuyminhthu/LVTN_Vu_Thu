import axiosInstance from ".";

export const getProducts = async () => {
  const response = await axiosInstance.get("/products");
  return response.data;
};

export const getProduct = async (id: string) => {
  const response = await axiosInstance.get(`/products/${id}`);
  return response.data;
};