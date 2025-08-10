<template>
  <header
    class="flex justify-between items-center px-4 py-2 border-b border-gray-200"
  >
    <!-- Logo -->
    <div class="flex items-center cursor-pointer" @click="handleLogo">
      <img
        :src="logoImage"
        alt="Logo"
        class="!h-16"
      />
    </div>

    <!-- Menu -->
    <div class="flex-1 flex justify-center">
      
    </div>

    <!-- Search and User Menu -->
    <div class="flex items-center gap-4 justify-center">
      <IconField>
        <InputText
          v-model="q"
          placeholder="Tìm kiếm sản phẩm"
          size="small"
          class="!rounded-full"
          @keyup.enter="handleSearch"
        />
        <InputIcon class="pi pi-search" />
      </IconField>
      
      <!-- User Menu -->
      <UserMenu />
      
      <!-- Chat Link -->
      <Button
        icon="pi pi-comments"
        class="p-button-text p-button-secondary"
        @click="handleChat"
      />
      
      <!-- <OverlayBadge value="4" severity="danger" :size="'small'"> -->
        <img
          src="https://www.coolmate.me/icons/icon-cart.svg"
          alt=""
          @click="handleCart"
          class="cursor-pointer"
        />
      <!-- </OverlayBadge> -->
    </div>
  </header>
</template>
<style scoped></style>
<script setup lang="ts">
import IconField from "primevue/iconfield";
import InputIcon from "primevue/inputicon";
import { useRouter } from "vue-router";
import InputText from "primevue/inputtext";
import OverlayBadge from "primevue/overlaybadge";
import Button from "primevue/button";
import UserMenu from "./UserMenu.vue";
import { ref } from "vue";
// Import ảnh từ assets
import logoImage from '@/assets/logo.jpg'
// hoặc
// import logoImage from '../assets/logo.jpg'

const router = useRouter();

const q = ref("");
const handleCart = () => {
  router.push("/cart");
};
const handleLogo = () => {
  router.push({ name: "home" });
};
const handleSearch = () => {
  router.push({ name: "spotlight", query: { q: q.value } });
};
const handleChat = () => {
  router.push({ name: "chat" });
};
</script>
