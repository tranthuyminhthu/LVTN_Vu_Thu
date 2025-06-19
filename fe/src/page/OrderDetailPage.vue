<template>
  <div class="flex gap-4">
    <h2 class="text-2xl font-bold">Thông tin đơn hàng #45326960942</h2>
    <button
      class="bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black block"
    >
      Đã hủy
    </button>
  </div>
  <div class="flex gap-4 my-4 text-sm">
    <div class="flex flex-col gap-1">
      <span>Ngày đặt hàng:</span>
      <span>Tên người nhận:</span>
      <span>Địa chỉ email:</span>
      <span>Số điện thoại:</span>
      <span>Phương thức thanh toán:</span>
      <span>Địa chỉ giao hàng:</span>
      <span>Ghi chú:</span>
    </div>
    <div class="flex flex-col gap-1">
      <span>13.08.2025</span>
      <span>Nguyễn Duy Vụ</span>
      <span>nguyenduyvu@gmail.com</span>
      <span>0909090909</span>
      <span>Thanh toán khi nhận hàng</span>
      <span>123 Đường ABC, Quận XYZ, TP. HCM</span>
      <span></span>
    </div>
  </div>
  <div class="flex gap-4">
    <button
      class="bg-white text-black px-4 py-2 rounded-full text-xs hover:!bg-black hover:!text-white transition-all duration-300 border border-black"
    >
      Cần hỗ trợ
    </button>
    <button
      class="bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black"
    >
      Mua lại
    </button>
  </div>
  <div class="my-4">
    <p class="text-xl font-bold">Tình trạng đơn hàng</p>
    <Timeline
      :value="events"
      class="my-8"
      :pt="{
        eventOpposite: () => ({
          class: '!flex-0 whitespace-nowrap pl-0',
        }),
      }"
    >
      <template #marker="slotProps">
        <span
          class="flex !w-3 !h-3 items-center justify-center text-white rounded-full z-10 shadow-sm"
          :style="{ backgroundColor: slotProps.item.color }"
        >
        </span>
      </template>
      <template #opposite="slotProps">
        <div class="text-surface-500 dark:text-surface-400 text-xs">
          {{ slotProps.item.time }}
        </div>
        <div class="text-surface-500 dark:text-surface-400 text-xs">
          {{ slotProps.item.date }}
        </div>
      </template>
      <template #content="slotProps">
        <div class="text-sm font-bold">
          {{ slotProps.item.status }}
        </div>
        <div class="text-xs">
          {{ slotProps.item.description }}
        </div>
      </template>
    </Timeline>
  </div>
  <div class="rounded-lg">
    <div class="">
      <div
        class="bg-[#2f5acf] !grid grid-cols-12 text-white text-center py-4 rounded-lg"
      >
        <span class="col-span-4">Tên sản phẩm</span>
        <span class="col-span-2">Số lượng</span>
        <span class="col-span-2">Giá niêm yết</span>
        <span class="col-span-2">Biến thể</span>
        <span class="col-span-2">Thành tiền</span>
      </div>
      <div class="bg-gray-100 rounded-b-lg">
        <div
          class="!grid grid-cols-12 text-center py-4 px-2 items-center border-b border-gray-200"
        >
          <div class="col-span-4 flex gap-2 items-center">
            <img
              src="https://media3.coolmate.me/cdn-cgi/image/width=160,height=181,quality=80/uploads/May2025/ao-ba-lo-mac-trong-excool-261-trang_89.jpg"
              alt=""
              class="rounded-lg !w-18 object-cover border border-[#2f5acf]"
            />
            <span class="text-xs"
              >Áo ba lỗ nam mặc trong thoáng khí nhanh khô</span
            >
          </div>
          <span class="col-span-2">4</span>
          <span class="col-span-2">89.000đ</span>
          <span class="col-span-2">L</span>
          <span class="col-span-2">356.000đ</span>
        </div>
        <div
          class="!grid grid-cols-12 text-center py-4 px-2 items-center border-b border-gray-200"
        >
          <div class="col-span-4 flex gap-2 items-center">
            <img
              src="https://media3.coolmate.me/cdn-cgi/image/width=160,height=181,quality=80/uploads/May2025/ao-ba-lo-mac-trong-excool-261-trang_89.jpg"
              alt=""
              class="rounded-lg !w-18 object-cover border border-[#2f5acf]"
            />
            <span class="text-xs"
              >Áo ba lỗ nam mặc trong thoáng khí nhanh khô</span
            >
          </div>
          <span class="col-span-2">4</span>
          <span class="col-span-2">89.000đ</span>
          <span class="col-span-2">L</span>
          <span class="col-span-2">356.000đ</span>
        </div>
      </div>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Mã giảm giá</span>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Tổng giá trị sản phẩm</span>
      <span class="font-bold text-sm">356.000đ</span>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Tổng khuyến mãi</span>
      <span class="font-bold text-sm">0đ</span>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Phí vận chuyển</span>
      <span class="font-bold text-sm">0đ</span>
    </div>
    <div
      class="bg-black rounded-b-lg text-white p-4 flex justify-between items-center"
    >
      <span class="font-bold text-sm">Tổng thanh toán</span>
      <span class="font-bold text-sm">356.000đ</span>
    </div>
    <div class=""></div>
  </div>
</template>
<script setup lang="ts">
import Timeline from "primevue/timeline";
import { ref } from "vue";

const events = ref([
  {
    status: "Đã hủy",
    description: "Đơn hàng đã bị hủy",
    date: "15-10-2020",
    icon: "pi pi-shopping-cart",
    color: "#2f5acf",
    time: "10:30",
  },
  {
    status: "Chờ xác nhận",
    description: "Hệ thống đang xác nhận đơn hàng của bạn",
    date: "15-10-2020",
    icon: "pi pi-cog",
    color: "#525252",
    time: "14:00",
  },
  {
    status: "Chờ xác nhận",
    description: "Hệ thống đang xác nhận đơn hàng của bạn",
    date: "15-10-2020",
    icon: "pi pi-cog",
    color: "#525252",
    time: "14:00",
  },
]);
</script>
