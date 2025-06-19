<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">Sản phẩm yêu thích</h1>
    <div v-if="favoriteProducts.length === 0" class="text-gray-500">
      Bạn chưa có sản phẩm yêu thích nào.
    </div>
    <div v-else class="!grid grid-cols-1 md:grid-cols-3 gap-6">
      <ProductCard
        v-for="product in favoriteProducts"
        :key="product.id"
        :product="product"
        @remove="removeFromFavorites(product.id)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import ProductCard from '@/components/ProductCard.vue';

interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
  // ... các trường khác nếu cần
}

// Dữ liệu mẫu, sau này sẽ lấy từ API hoặc localStorage
const favoriteProducts = ref<Product[]>([
  {
    id: 1,
    name: 'Áo thun basic',
    price: 199000,
    image: 'https://via.placeholder.com/300',
  },
  {
    id: 2,
    name: 'Quần jean slim fit',
    price: 499000,
    image: 'https://via.placeholder.com/300',
  },
]);

const removeFromFavorites = (id: number) => {
  favoriteProducts.value = favoriteProducts.value.filter(p => p.id !== id);
};
</script> 