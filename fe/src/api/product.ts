import axiosInstance from ".";

// Types
export interface ProductVariant {
  id?: number;
  sku?: string;
  productId?: number;
  size: string;
  colorName: string;
  colorHex: string;
  price: number;
  stockQuantity: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateProductPayload {
  name: string;
  description: string;
  price: number;
  variants: ProductVariant[];
  images?: string[];
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  rating: number;
  status: string | null;
  variants: ProductVariant[];
  images?: string[];
  createdBy?: string;
  // Optional fields for UI display
  image?: string;
  category?: string;
  views?: number;
  sales?: number;
}

export interface ProductResponse {
  products: Product[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ProductQueryParams {
  page?: number;
  size?: number;
  category?: string;
  status?: string;
  vendorId?: string;
}

export const getProducts = async (params: ProductQueryParams = {}): Promise<ProductResponse> => {
  const response = await axiosInstance.get("/api/products", { params });
  return response.data;
};

export const getProduct = async (id: string) => {
  const response = await axiosInstance.get(`/api/products/${id}`);
  return response.data;
};

export const getMyProducts = async () => {
  try {
    const response = await axiosInstance.get("/api/products/me");
    return Promise.resolve(response.data);
  } catch (error) {
    return Promise.reject(error);
  }
};

export const getProductComments = async (productId: number) => {
  try {
    const response = await axiosInstance.get(`/api/products/comments/${productId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching product comments:', error);
    throw error;
  }
};

export async function createProduct(payload: CreateProductPayload) {
  const formData = new FormData();
  formData.append('product', JSON.stringify({
    name: payload.name,
    description: payload.description,
    price: payload.price,
    variants: payload.variants
  }));
  if (payload.images && payload.images.length > 0) {
    payload.images.forEach((img: File | string) => {
      if (img instanceof File) {
        formData.append('files', img);
      }
    });
  }
  return axiosInstance.post('/api/products', formData, {
    headers: {
      Authorization: `Bearer ${localStorage.getItem('token')}`,
    }
  }).then(res => res.data);
}

export const createProductComment = async (payload: {
  productId: number;
  content: string;
  rating: number;
}) => {
  try {
    const response = await axiosInstance.post('/api/products/comments', payload);
    return response.data;
  } catch (error) {
    console.error('Error creating product comment:', error);
    throw error;
  }
};