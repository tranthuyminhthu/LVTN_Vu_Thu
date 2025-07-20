<template>
  <div class="rounded-lg bg-gray-100 cursor-pointer" @click="router.push(`/account/orders/${order.orderId}`)">
    <div class="bg-[#2f5acf] px-4 py-2 flex justify-between items-center rounded-lg">
      <div class="text-white flex flex-col gap-2">
        <span class="font-bold">#{{ order.orderId }}</span>
        <span class="text-xs">{{ formatDate(order.createdAt) }}</span>
      </div>
      <button class="bg-white text-black px-4 py-2 rounded-full text-xs">
        {{ getStatusLabel(order.status) }}
      </button>
    </div>
    <div>
      <div class="border-b border-gray-200 px-4 py-2 flex gap-4">
        <img
          :src="order.image"
          alt=""
          class="rounded-lg !w-24 !h-24 object-cover"
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
        <span class="font-bold text-xs">{{ formatPrice(order.totalAmount) }}</span>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { Order } from '@/api/order';
import { computed } from 'vue';

const props = defineProps<{ order: Order }>();
const router = useRouter();

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('vi-VN');
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PENDING': return 'Chờ xử lý';
    case 'PROCESSING': return 'Đang xử lý';
    case 'SHIPPED': return 'Đang giao hàng';
    case 'DELIVERED': return 'Đã giao hàng';
    case 'CANCELLED': return 'Đã hủy';
    case 'REFUNDED': return 'Đã hoàn trả';
    default: return status;
  }
};
</script>
