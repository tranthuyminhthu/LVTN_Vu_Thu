<template>
  <div class="p-6">
    <h1 class="text-2xl font-bold mb-6">Sản phẩm yêu thích</h1>
    
    <!-- Empty state -->
    <div v-if="favoriteProducts.length === 0" class="text-gray-500 text-center py-8">
      Bạn chưa có sản phẩm yêu thích nào.
    </div>
    
    <!-- Products grid -->
    <div v-else>
      <div class="!grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
        <ProductCard
          v-for="product in favoriteProducts"
          :key="product.id"
          :product="product"
          @remove="removeFromFavorites(product.id)"
        />
      </div>
      
      <!-- Pagination -->
      <div class="flex justify-center">
        <Paginator
          v-model:first="first"
          :rows="pageSize"
          :totalRecords="totalElements"
          @page="onPageChange"
          :template="{
            '640px': 'PrevPageLink PageLinks NextPageLink',
            '960px': 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink',
            '1300px': 'FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink'
          }"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import ProductCard from '@/components/ProductCard.vue';
import Paginator from 'primevue/paginator';
import { userApi } from '@/api/user';
import { useLoading } from '@/composables/useLoading';
import type { Product } from '@/types/index';

// Global loading
const { show: showLoading, hide: hideLoading } = useLoading();

// Pagination state
const favoriteProducts = ref<Product[]>([]);
const first = ref(0);
const pageSize = ref(9); // 3 columns x 3 rows
const totalElements = ref(0);

// Load favorite products
const loadFavoriteProducts = async (page: number = 0) => {
  try {
    showLoading('Đang tải sản phẩm yêu thích...');
    const response = await userApi.getFavoriteProducts({
      page,
      size: pageSize.value
    });
    
    favoriteProducts.value = response.products;
    totalElements.value = response.totalElements;
    first.value = page * pageSize.value;
  } catch (error) {
    console.error('Error loading favorite products:', error);
    // Fallback to empty array on error
    favoriteProducts.value = [];
    totalElements.value = 0;
  } finally {
    hideLoading();
  }
};

// Handle page change
const onPageChange = (event: { first: number; rows: number }) => {
  const page = Math.floor(event.first / event.rows);
  loadFavoriteProducts(page);
};

// Remove from favorites
const removeFromFavorites = async (id: string) => {
  try {
    showLoading('Đang xóa sản phẩm khỏi danh sách yêu thích...');
    await userApi.removeFromFavorites(id);
    // Reload current page after removal
    const currentPage = Math.floor(first.value / pageSize.value);
    await loadFavoriteProducts(currentPage);
  } catch (error) {
    console.error('Error removing from favorites:', error);
  } finally {
    hideLoading();
  }
};

// Load data on mount
onMounted(() => {
  loadFavoriteProducts();
});
</script> 