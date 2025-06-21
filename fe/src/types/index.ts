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
}