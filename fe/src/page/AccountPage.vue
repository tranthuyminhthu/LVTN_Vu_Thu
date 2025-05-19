<template>
  <div class="bg-[#d9d9d9] min-h-screen p-8">
    <div class="bg-white rounded-lg py-6 px-8 flex">
      <div class="w-[70%]">
        <div class="font-bold text-2xl">Hi Vụ Nguyễn Duy</div>
        <div class="">
          <img
            src="https://mcdn.coolmate.me/image/October2023/mceclip0_92.png"
            alt=""
            class="h-8 mt-2"
          />
        </div>
        <div class="flex items-center gap-2 mt-2">
          <p>Chi thêm <span class="text-primary font-bold">500.000đ</span> để lên hạng</p>
          <img
            src="https://mcdn.coolmate.me/image/October2023/mceclip0_92.png"
            alt=""
            class="h-4"
          />
        </div>
        <Timeline 
          :value="events" 
          layout="horizontal" 
          align="top" 
          class="mr-8"
          :pt="{
            eventContent: ({ context }) => ({
              class: context.index === events.length - 1 ? '!w-[70px]' : ''
            })
          }"
        >
          <template #content="slotProps">
            <img :src="slotProps.item" alt="" class="h-6" />
          </template>
        </Timeline>
      </div>
      <div class="w-[30%] flex flex-col gap-2 text-right">
        <span>Bạn đang có</span>
        <span>0 cool cash</span>
        <span>Giới thiệu bạn bè</span>
        <img src="https://mcdn.coolmate.me/image/March2025/mceclip1_60.png" alt="" class="cursor-pointer w-[375px] ml-auto">
      </div>
    </div>
    <div class="rounded mt-6 flex">
      <div class="w-1/4 mr-8">
        <div
          v-for="item in menuItems"
          :key="item.title"
          :class="[
            'rounded-lg p-3 flex justify-between items-center cursor-pointer mb-4 transition-all duration-300',
            currentPath === item.path
              ? 'bg-black text-white'
              : 'bg-white hover:!bg-black hover:text-white',
          ]"
          @click="navigateTo(item.path)"
        >
          <div class="flex justify-between items-center gap-2">
            <i :class="item.icon"></i>
            <p>{{ item.title }}</p>
          </div>
          <i class="pi pi-arrow-right"></i>
        </div>
      </div>
      <div class="w-full bg-white rounded-lg p-4">
        <div>
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import Timeline from "primevue/timeline";
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";

const router = useRouter();
const route = useRoute();

const currentPath = computed(() => route.path);

const menuItems = ref([
  {
    title: "Thông tin tài khoản",
    subtitle: "Thông tin cá nhân",
    icon: "pi pi-user",
    path: "/account/info",
  },
  {
    title: "Đơn hàng của tôi",
    subtitle: "Theo dõi đơn hàng",
    icon: "pi pi-shopping-bag",
    path: "/account/orders",
  },
  {
    title: "Địa chỉ giao hàng",
    subtitle: "Quản lý địa chỉ",
    icon: "pi pi-map-marker",
    path: "/account/address",
  },
  {
    title: "Đổi mật khẩu",
    subtitle: "Bảo mật tài khoản",
    icon: "pi pi-lock",
    path: "/account/password",
  },
]);

const navigateTo = (path: string) => {
  router.push(path);
};
const events = ref([
  "https://mcdn.coolmate.me/image/October2023/mceclip0_92.png",
  "https://mcdn.coolmate.me/image/September2023/mceclip0_56.png",
  "https://mcdn.coolmate.me/image/September2023/mceclip3_45.png",
  "https://mcdn.coolmate.me/image/September2023/mceclip1_32.png",
]);
</script>
