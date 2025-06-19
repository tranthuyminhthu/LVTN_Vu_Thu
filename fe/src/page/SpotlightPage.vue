<template>
  <div class="min-h-screen py-4">
    <div class="flex gap-4 items-center border-b pb-4 px-24 border-gray-200">
      <p class="font-bold text-2xl">Sản phẩm</p>
      <InputText
        v-model="search"
        placeholder="Tìm kiếm"
        class="w-52"
        @keyup.enter="handleSearch"
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
        <div class="!grid grid-cols-5 gap-4 mt-4">
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
            <ProductCard />
        </div>
        <Button label="Xem thêm" class="!rounded-full mt-4 !px-4 !mx-auto !block !bg-black !text-white !border-black" />
    </div>
  </div>
</template>
<script setup lang="ts">
import ProductCard from "@/components/ProductCard.vue";
import { Button, InputText } from "primevue";
import Select from "primevue/select";
import { ref } from "vue";
const search = ref("");
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
const handleSearch = () => {
  console.log('Searching for:', search.value);
};
</script>
