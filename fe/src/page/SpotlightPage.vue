<template>
  <div class="min-h-screen py-4">
    <div class="flex gap-4 items-center border-b pb-4 px-24 border-gray-200">
      <p class="font-bold text-2xl">Sản phẩm</p>
      <InputText
        v-model="q"
        placeholder="Tìm kiếm"
        class="w-52"
        @keyup.enter="() => handleSearch(0)"
        :pt="{
          root: () => ({
            class: '!rounded-2xl',
          }),
        }"
      />
      <Select
        v-model="selectedCategory"
        :options="categories"
        optionLabel="name"
        placeholder="Select a City"
        class="w-32"
        :pt="{
          root: () => ({
            class: '!rounded-2xl',
          }),
        }"
      />
    </div>
    <div class="px-24 mt-4">
        <p class="text-2xl font-bold">Kết quả</p>
        <LoadingGlobal v-if="loading" :isLoading="loading" />
        <div v-else-if="products.length > 0" class="!grid grid-cols-5 gap-4 mt-4">
          <ProductCard v-for="product in products" :key="product.id" :product="product" />
        </div>
        <div v-else class="flex flex-col items-center justify-center py-16">
          <img 
            src="https://cdn-icons-png.flaticon.com/512/6134/6134065.png" 
            alt="Không tìm thấy sản phẩm" 
            class="w-32 h-32 opacity-50 mb-4"
          />
          <p class="text-xl text-gray-500 font-medium">Không tìm thấy sản phẩm nào</p>
          <p class="text-gray-400 mt-2">Hãy thử tìm kiếm với từ khóa khác</p>
        </div>
        <Paginator 
          v-if="totalElements > 0"
          v-model:first="first" 
          :rows="size" 
          :totalRecords="totalElements"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          @page="onPageChange"
          class="mt-6"
        />
    </div>
  </div>
</template>
<script setup lang="ts">
import { searchProducts } from "@/api/search";
import ProductCard from "@/components/ProductCard.vue";
import type { Product } from "@/types";
import { Button, InputText, Paginator } from "primevue";
import Select from "primevue/select";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import LoadingGlobal from "@/components/LoadingGlobal.vue";
const route = useRoute();

const selectedCategory = ref({ name: "Áo polo" });
const categories = ref<{ name: string }[]>([
  { name: "Áo polo" },
  { name: "Áo sơ mi" },
  { name: "Áo khoác" },
  { name: "Quần" },
  { name: "Đồ bóng" },
  { name: "Phụ kiện" },
  { name: "Giày" },
  { name: "Bóng" },
  { name: "Bóng bàn" },
]);
const q = ref(route.query.q as string);

// State cho kết quả tìm kiếm
const products = ref<Product[]>([]);
const page = ref(0);
const size = ref(10);
const totalElements = ref(0);
const totalPages = ref(0);
const loading = ref(false);

const first = computed({
  get: () => page.value * size.value,
  set: (value: number) => {
    page.value = Math.floor(value / size.value);
  }
});

const handleSearch = async (pageNum = 0) => {
  loading.value = true;
  const response = await searchProducts(q.value, pageNum, size.value);
  products.value = response.products;
  page.value = response.page;
  size.value = response.size;
  totalElements.value = response.totalElements;
  totalPages.value = response.totalPages;
  window.scrollTo({ top: 0, behavior: "smooth" });
  loading.value = false;
};

const onPageChange = (event: { first: number; rows: number }) => {
  size.value = event.rows;
  handleSearch(event.first / event.rows);
};

watch(() => route.query.q, (newQ) => {
  q.value = newQ as string;
  handleSearch();
}, { immediate: true });

// onMounted(() => {
//   handleSearch();
// });
</script>
