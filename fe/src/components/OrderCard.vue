<template>
  <div class="rounded-lg bg-gray-100 cursor-pointer" @click="router.push(`/account/orders/1`)">
    <div
      class="bg-[#2f5acf] px-4 py-2 flex justify-between items-center rounded-lg"
    >
      <div class="text-white flex flex-col gap-2">
        <span class="font-bold">#12121212</span>
        <span class="text-xs">13.08.2025</span>
      </div>
      <button class="bg-white text-black px-4 py-2 rounded-full text-xs">
        Đã hủy
      </button>
    </div>
    <div class="">
      <div class="border-b border-gray-200 px-4 py-2 flex gap-4">
        <img
          src="https://media3.coolmate.me/cdn-cgi/image/width=160,height=181,quality=80/uploads/May2025/ao-ba-lo-mac-trong-excool-261-trang_89.jpg"
          alt=""
          class="rounded-lg"
        />
        <div class="flex flex-col gap-2 justify-center">
          <span class="font-bold"
            >Áo ba lỗ nam mặc trong thoáng khí nhanh khô</span
          >
          <span>Trắng / L</span>
          <span>x4</span>
          <span class="font-bold">89.000đ</span>
        </div>
      </div>
      <div class="px-4 flex justify-between items-center py-2">
        <div class="flex gap-4">
          <button
            class="bg-white text-black px-4 py-2 rounded-full text-xs border border-black hover:!bg-black hover:!text-white transition-all duration-300"
            @click.stop="router.push(`/message`)"
            >
            Cần hỗ trợ
          </button>
          <button
            class="bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black"
          >
            Mua lại
          </button>
        </div>
        <span class="font-bold text-xs">356.000đ</span>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router';
const router = useRouter();
</script>
