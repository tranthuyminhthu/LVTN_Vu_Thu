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
    status: string | null;
    isFavorite: boolean;
    variants: ProductVariant[];
    images?: (string | File)[];
    vendorInfo: VendorInfo;
}

interface VendorInfo {
  id: string;
  name: string;
  image: string;
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
  id: string;
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

export interface CalculateFeeItem {
  name: string;
  quantity: number;
  height: number;
  weight: number;
  length: number;
  width: number;
}

export interface CalculateFeePayload {
  from_district_id: number;
  from_ward_code: string;
  service_id: number;
  service_type_id?: number | null;
  to_district_id: number;
  to_ward_code: string;
  height?: number;
  length?: number;
  weight: number;
  width?: number;
  insurance_value?: number;
  cod_failed_amount?: number;
  coupon?: string | null;
  items?: CalculateFeeItem[];
}

export interface GetServicePayload {
  from_district: number;
  to_district: number;
  shop_id: number;
}

export interface OrderAcceptedRequestDto {
  orderId: string;
  serviceId: number;
  note: string;
  requiredNote: string;
  content: string;
}

export interface SocketChatMessageResponse {
  id: string;
  conversationId: string;
  isMe: boolean;
  message: string;
  sender: {
    userId: string;
    username: string;
    avatar: string | null;
  };
  createdDate: string;
}

export interface OrderItem {
  productSku: string;
  productName: string;
  quantity: number;
  price: number;
  image: string;
}

export interface OrderTimeline {
  status: string;
  note: string;
  changedAt: string;
}

export interface Order {
  orderId: string;
  userId: string;
  shopId: number;
  status: string;
  totalAmount: number;
  paymentStatus: string;
  paymentMethod: string;
  paymentTransactionId: string | null;
  createdAt: string;
  items: OrderItem[];
  receiverName: string;
  receiverPhone: string;
  receiverEmail: string;
  senderDistrictId: number;
  senderWardCode: string;
  receiverAddress: string;
  receiverWardCode: string;
  receiverDistrictId: number;
  note: string;
  timeline: OrderTimeline[];
  image: string | null;
}

export interface VnpayReturnParams {
  vnp_Amount: string;
  vnp_BankCode: string;
  vnp_CardType: string;
  vnp_OrderInfo: string;
  vnp_PayDate: string;
  vnp_ResponseCode: string;
  vnp_SecureHash: string;
  vnp_TmnCode: string;
  vnp_TransactionNo: string;
  vnp_TransactionStatus: string;
  vnp_TxnRef: string;
}