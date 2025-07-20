export interface ProductVariant {
    sku: string;
    productId: string;
    size: string;
    colorName: string;
    colorHex: string;
    price: number;
    stockQuantity: number;
}

export interface Product {
    id: string;
    name: string;
    description: string;
    price: number;
    rating: number;
    status: string;
    variants: ProductVariant[];
    images?: (string | File)[];
}

export interface CreateProductPayload {
    name: string;
    description: string;
    price: number;
    rating: number;
    status: string;
    variants: ProductVariant[];
    images?: (string | File)[];
}

export interface RegisterData {
    username: string;
    email: string;
    password: string;
    firstName?: string;
    lastName?: string;
    isVendor?: boolean; // Thêm trường isVendor để phân biệt user/vendor
}

export interface LoginData {
    username: string;
    password: string;
}

export interface CartItem {
    id: string;
    cartId: string;
    sku: string;
    quantity: number;
    price: number;
    productDetails: Product;
}

export interface Cart {
    userId: string;
    totalPrice: number;
    items: CartItem[];
}

export interface CartItemWithProduct extends CartItem {
    product?: Product;
    variant?: ProductVariant;
}

export interface Comment {
  id: number;
  userId: string;
  userName: string;
  productId: number;
  content: string;
  rating: number;
  createdAt: string;
  updatedAt: string;
}

export interface Conversation {
  id: string;
  conversationAvatar: string | null;
  conversationName: string;
  updatedAt: string;
  participants: Array<{
    userId: string;
    username: string;
    avatar: string | null;
  }>;
}

export interface Message {
  id: number;
  text: string;
  sender: 'user' | 'other';
  timestamp: Date;
}

export interface ApiMessageResponse {
  id: number;
  message: string;
  isMe: boolean;
  createdDate: string;
}

export interface UserInfo {
  userId: string;
  email: string;
  username: string;
  fullName: string | null;
  type: string | null;
  image: string | null;
  height: number | null;
  weight: number | null;
  gender: string | null;
  shopName: string | null;
  role: string[];
  dob: string | null;
  phone?: string | null;
}