<template>
  <div class="h-full relative min-h-screen">
    <div class="bg-[#344767] h-[300px] w-full absolute top-0 left-0 z-[-1]"></div>
    <aside class="w-[17%] bg-white fixed top-10 left-10 rounded-lg p-4 h-[90%]">
      <div class="">
        <div class="pb-4 flex items-center gap-2">
          <img
            src="https://demos.creative-tim.com/argon-dashboard/assets/img/logo-ct-dark.png"
            alt=""
            class="!w-10 !h-10 object-cover"
          />
          <span>Vendor Dashboard</span>
        </div>
        <hr
          style="
            border: none;
            height: 1px;
            background-image: linear-gradient(
              90deg,
              transparent 0%,
              rgba(0, 0, 0, 0.4) 40%,
              rgba(0, 0, 0, 0.8) 50%,
              rgba(0, 0, 0, 0.4) 60%,
              transparent 100%
            );
          "
        />
      </div>
      <div class="flex flex-col justify-between gap-4 pt-4">
        <router-link
          v-for="item in menuItems"
          :key="item.id"
          :to="item.path"
          class="p-2 rounded-lg transition-all duration-300 cursor-pointer text-[#344767] flex items-center gap-2"
          :class="[route.path === item.path ? 'bg-[#f6f9fc] ' : '']"
        >
          <i :class="item.icon" class="text-lg"></i>
          <span
            class="text-[#344767]"
            :class="[route.path === item.path ? ' font-bold' : '']"
            >{{ item.name }}</span
          >
        </router-link>
      </div>
    </aside>
    <main class="!ml-100 !pt-10 w-[76%] rounded-lg relative z-10">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { useRoute } from "vue-router";
import { onMounted, onUnmounted } from 'vue';

const route = useRoute();

const menuItems = [
  { id: 1, name: "Dashboard", path: "/vendor/dashboard", icon: "pi pi-home" },
  {
    id: 2,
    name: "Sản phẩm của tôi",
    path: "/vendor/products",
    icon: "pi pi-box",
  },
  {
    id: 3,
    name: "Đơn hàng",
    path: "/vendor/orders",
    icon: "pi pi-shopping-bag",
  },
  {
    id: 6,
    name: "Tài chính",
    path: "/vendor/finance",
    icon: "pi pi-money-bill",
  },
  {
    id: 7,
    name: "Quảng cáo",
    path: "/vendor/advertisement",
    icon: "pi pi-bullhorn",
  },
  {
    id: 8,
    name: "Hỗ trợ",
    path: "/vendor/support",
    icon: "pi pi-support",
  },
];

onMounted(() => {
  document.body.classList.add('!bg-[#f8f9fa]');
});
onUnmounted(() => {
  document.body.classList.remove('!bg-[#f8f9fa]');
});
</script>

<style scoped></style> 