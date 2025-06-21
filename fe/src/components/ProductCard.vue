<template>
  <div class="product-card">
    <div class="image-wrapper">
      <img
        src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=85,format=auto/uploads/October2024/Mockup_Ao_tran_ao_tot_bunggang_copy_(7).jpg"
        alt=""
        class="product-image"
        @click="handleClick"
      />
      <img
        src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=85,format=auto/uploads/October2024/Mockup_Ao_tran_ao_tot_bunggang_copy_(6).jpg"
        alt=""
        class="product-image hover-image"
        @click="handleClick"
      />
    </div>
    <p class="py-2">{{ props.product.name }}</p>
    <ColorPicker v-model="size" :colorOptions="colorOptions" :name="props.name" />
    <div class="flex items-center gap-2 text-sm mt-2">
      <span class="font-bold">{{ formatVND(props.product.price) }}</span>
      <span
        class="rounded-lg bg-[#273bcd] p-1 text-white text-sm font-bold"
        style="font-size: 10px !important"
        >-20%</span
      >
      <span class="line-through text-[#c4c4c4]">{{ formatVND(props.product.price) }}</span>
    </div>
    <div class="quick-add p-2">
      
      <p class="font-bold">Thêm nhanh vào giỏ hàng</p>
      <div class="flex gap-2 my-2">
        <span
          class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size"
          >M</span
        >
        <span
          class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size"
          >L</span
        >
        <span
          class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size"
          >XL</span
        >
        <span
          class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size"
          >2XL</span
        >
      </div>
    </div>
    <div
      class="absolute uppercase text-white text-sm font-bold bg-[#273bcd] px-2 py-1 rounded-full right-[10px] top-[10px]"
      style="font-size: 10px !important"
    >
      New
    </div>
    <div class="absolute top-2 left-2 flex gap-1 text-sm">
      <span class="font-bold">{{ props.product.rating }}</span>
      <Rating :stars="1" :modelValue="1"/>
      <span class="text-[#273bcd] font-bold">(83)</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import ColorPicker, { type ColorOption } from "./ColorPicker.vue";
import { ref, watch } from "vue";
import { Rating } from "primevue";
import type { Product } from "@/types";
import { formatVND } from "@/common";

const router = useRouter();

const props = defineProps<{
  product: Product;
}>();

const handleClick = () => {
  console.log(props.product.id);
  router.push({ name: "product" , params: { id: props.product.id } });
};
const size = ref(1);
const colorOptions = ref<ColorOption[]>([
  {
    id: 1,
    type: 'color' as const,
    color: '#3b82f6',
    alt: 'Xanh dương'
  },
  {
    id: 2,
    type: 'image' as const,
    src: 'https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Navy-7.jpg',
    alt: 'Navy'
  },
  {
    id: 3,
    type: 'image' as const,
    src: 'https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Den-7.jpg',
    alt: 'Đen'
  }
]);
watch(size, (newVal) => {
  console.log('Selected color:', newVal); 
});


</script>

<style scoped>
.size:hover {
  background-color: black !important;
  color: white;
}
.product-card {
  position: relative;
  display: flex;
  flex-direction: column;
}

.image-wrapper {
  position: relative;
  width: 100%;
}

.product-image {
  border-radius: 0.5rem;
  cursor: pointer;
  width: 100%;
  height: auto;
}

.hover-image {
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0;
}

.product-card:hover .hover-image {
  opacity: 1;
}

.quick-add {
  position: absolute;
  background:
    linear-gradient(0deg, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.1)),
    hsla(0, 0%, 100%, 0.4);
  backdrop-filter: blur(20px);
  width: 80%;
  left: 50%;
  transform: translateX(-50%) translateY(20px);
  bottom: 25%;
  border-radius: 0.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: column;
  opacity: 0;
  transition: all 0.3s ease;
  visibility: hidden;
}

.product-card:hover .quick-add {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
  visibility: visible;
}
</style>
