<template>
  <div class="product-card">
    <div class="image-wrapper">
      <img
        src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=85/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Den_5.jpg"
        alt=""
        class="product-image"
        @click="handleClick"
      />
      <img
        src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=85/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Den_4.jpg"
        alt=""
        class="product-image hover-image"
        @click="handleClick"
      />
    </div>
    <p class="py-2">Combo 3 Áo ba lỗ 100% Cotton</p>
    <div class="flex items-center gap-2 text-sm">
      <span class="font-bold">100.000đ</span>
      <span class="rounded-lg bg-[#273bcd] p-1 text-white text-sm font-bold" style="font-size: 10px !important">-20%</span>
      <span class="line-through text-[#c4c4c4]">150.000đ</span>
    </div>
    <div class="quick-add p-2">
      <p class="font-bold">Thêm nhanh vào giỏ hàng</p>
      <div class="flex gap-2 my-2">
        <span class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size">M</span>
        <span class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size">L</span>
        <span class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size">XL</span>
        <span class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size">2XL</span>
      </div>
    </div>
    <div class="absolute uppercase text-white text-sm font-bold bg-[#273bcd] px-2 py-1 rounded-full right-[10px] top-[10px]" style="font-size: 10px !important">New</div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";

const router = useRouter();

const handleClick = () => {
  router.push({ name: "product" });
};
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
  background: linear-gradient(0deg,rgba(0,0,0,.1),rgba(0,0,0,.1)),hsla(0,0%,100%,.4);
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
