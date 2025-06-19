<template>
  <header
    class="flex justify-between items-center px-4 py-2 border-b border-gray-200"
  >
    <!-- Logo -->
    <div class="flex items-center cursor-pointer" @click="handleLogo">
      <img
        src="https://www.coolmate.me/images/logo-coolmate-new-v2.png"
        alt="Logo"
        class="h-8"
      />
    </div>

    <!-- Menu -->
    <div class="flex-1 flex justify-center">
      
    </div>

    <!-- Search -->
    <div class="flex items-center gap-4 justify-center">
      <IconField>
        <InputText
          v-model="value1"
          placeholder="Tìm kiếm sản phẩm"
          size="small"
          class="!rounded-full"
          @keyup.enter="handleSearch"
        />
        <InputIcon class="pi pi-search" />
      </IconField>
      <div class="flex items-center justify-center">
        <img
          src="https://mcdn.coolmate.me/image/October2023/mceclip3_72.png"
          alt=""
          class="w-8"
          @click="visible = true"
        />
        <Drawer
          v-model:visible="visible"
          position="right"
          :showCloseIcon="false"
          :pt="{
            header: () => ({
              class: 'border-b border-gray-200',
            }),
            root: () => ({
              class: '!w-[400px]',
            }),
          }"
        >
          <template #header>
            <div class="">
              <span class="text-lg font-bold">Hi, Vụ Nguyễn Duy</span>
              <img
                src="https://mcdn.coolmate.me/image/October2023/mceclip0_92.png"
                alt=""
                class="w-5 mt-2"
              />
            </div>
          </template>
          <p class="">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat.
          </p>
          <div class="flex mt-2 gap-3 ">
            <div
              class="bg-[#f1f1f1] flex-1 rounded-lg flex items-center gap-2 flex-col p-2 cursor-pointer"
            >
              <img
                src="https://mcdn.coolmate.me/image/September2023/mceclip1_59.png"
                alt=""
                class="w-6"
              />
              <span>Ví voucher</span>
            </div>
            <div
              class="bg-[#f1f1f1] flex-1 rounded-lg flex items-center gap-2 flex-col p-2 cursor-pointer"
            >
              <img
                src="https://mcdn.coolmate.me/image/September2023/mceclip1_59.png"
                alt=""
                class="w-6"
              />
              <span>Ví voucher</span>
            </div>
            <div
              class="bg-[#f1f1f1] flex-1 rounded-lg flex items-center gap-2 flex-col p-2 cursor-pointer"
            >
              <img
                src="https://mcdn.coolmate.me/image/September2023/mceclip1_59.png"
                alt=""
                class="w-6"
              />
              <span>Ví voucher</span>
            </div>
            
            
          </div>
          <template #footer>
            <div
              class="bg-primary w-full py-3 text-center cursor-pointer absolute bottom-0 left-0"
              @click="handleAccount"
            >
              Đi đến tài khoản
            </div>
          </template>
        </Drawer>
      </div>
      <OverlayBadge value="4" severity="danger" :size="'small'">
        <img
          src="https://www.coolmate.me/images/header/icon-cart-new-v2.svg?v=1"
          alt=""
          @click="handleCart"
          class="cursor-pointer"
        />
      </OverlayBadge>
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
import Drawer from "primevue/drawer";
import { ref } from "vue";

const router = useRouter();

const value1 = ref("");
const handleCart = () => {
  router.push("/cart");
};
const handleLogo = () => {
  router.push("/home");
};
const visible = ref(false);
const handleAccount = () => {
  router.push({ name: "info" });
};
const handleSearch = () => {
  router.push({ name: "spotlight" });
};
</script>
