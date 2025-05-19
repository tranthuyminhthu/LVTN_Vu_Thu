<template>
  <Breadcrumb :home="home" :model="items" class="pl-6">
    <template #item="{ item, props }">
      <router-link
        v-if="item.route"
        v-slot="{ href, navigate }"
        :to="item.route"
        custom
      >
        <a :href="href" v-bind="props.action" @click="navigate">
          <span :class="[item.icon, 'text-color']" />
          <span class="text-primary font-semibold">{{ item.label }}</span>
        </a>
      </router-link>
      <a v-else :href="item.url" :target="item.target" v-bind="props.action">
        <span class="text-surface-700 dark:text-surface-0">{{
          item.label
        }}</span>
      </a>
    </template>
    <template #separator> / </template>
  </Breadcrumb>
  <div class="grid">
    <div class="col-6">
      <Galleria
        :value="images"
        :responsiveOptions="responsiveOptions"
        :numVisible="5"
        :thumbnailsPosition="'left'"
      >
        <template #item="slotProps">
          <img
            :src="slotProps.item.itemImageSrc"
            :alt="slotProps.item.alt"
            style="width: 100%; display: block"
          />
        </template>
        <template #thumbnail="slotProps">
          <div class="grid gap-4 justify-center">
            <img
              :src="slotProps.item.thumbnailImageSrc"
              :alt="slotProps.item.alt"
              style="width: 50px; display: block"
              class="cover"
            />
          </div>
        </template>
      </Galleria>
    </div>
    <div class="flex gap-4 col-6">
      <div class="flex-1">
        <p class="font-bold text-3xl">Áo Thun Nam Cotton 220GSM</p>
        <p class="">100% Cotton</p>
        <span class="flex gap-2 my-2"
          ><Rating v-model="rating" readonly />({{ rating }})</span
        >
        <div class="my-2">
          <del class="text-[#c4c4c4]">179.000đ</del>
        </div>
        <p class="font-bold text-2xl flex gap-4 align-items-center">
          <span class="font-bold text-2xl">129.000đ</span>
          <Badge>-11%</Badge>
        </p>
        <div class="mt-2 flex gap-2 align-items-center">
          <i class="pi pi-truck"></i>
          <p class="font-medium">Freeship đơn trên 200K</p>
        </div>
        <div class="flex gap-4 my-4">
          <span>Mã giảm giá</span>
          <span
            style="
              background-image: url(&quot;https://media.coolmate.me/image/September2024/mceclip0_126.png&quot;);
            "
            class="py-1 px-2 text-[#fa6a18] bg-cover w-[108px] rounded flex justify-content-center bg-no-repeat"
          >
            Giảm 40k
          </span>
        </div>
        <img
          src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/April2025/mceclip0_54.jpg"
          alt=""
          class="rounded-md"
        />
        <p class="my-2">
          <span>Màu sắc: </span><span class="font-bold">{{ selectedColor }}</span>
        </p>
        <div class="flex flex-wrap gap-4">
          <label 
            v-for="item in colorOptions" 
            :key="item.id"
            :for="'size_' + item.id" 
            class="cursor-pointer"
          >
            <RadioButton
              v-model="size"
              name="size"
              :value="item.id"
              class="hidden"
              :inputId="'size_' + item.id"
            />
            <div 
              class="rounded-full w-[50px] h-[25px] overflow-hidden" 
              :style="size === item.id ? 'border: 2px solid #2f5acf; padding: 2px; background: white;' : ''"
            >
              <img
                v-if="item.type === 'image'"
                :src="item.src"
                :alt="item.alt"
                class="rounded-full w-full h-full cursor-pointer"
              />
              <span 
                v-else 
                class="rounded-full w-full h-full block"
                :style="{ backgroundColor: item.color }"
              ></span>
            </div>
          </label>
        </div>
        <p class="my-2">
          <span>Kích thước: </span><span class="font-bold">S</span>
        </p>
        <div class="">aaa</div>
        <Panel
          toggleable
          class="!bg-[#f1f3ff] my-4"
          ref="panelRef"
          :collapsed="false"
          @toggle="onToggle"
        >
          <template #header>
            <p>
              Được hoàn lên đến 12.000 CoolCash.
              <span class="text-primary">Chi tiết</span>
            </p>
          </template>
          <template #toggleicon="{ collapsed }">
            <i :class="['pi', collapsed ? 'pi-angle-down' : 'pi-angle-up']"></i>
          </template>
          <p class="m-0">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat. Duis aute irure dolor in
            reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla
            pariatur. Excepteur sint occaecat cupidatat non proident, sunt in
            culpa qui officia deserunt mollit anim id est laborum.
          </p>
        </Panel>
        <div class="grid">
          <div class="col-6 flex gap-2">
            <img
              src="https://www.coolmate.me/images/product-detail/return.svg"
              alt=""
            />
            <span>Đổi trả cực dễ chỉ cần số điện thoại</span>
          </div>
          <div class="col-6 flex gap-2">
            <img
              src="https://www.coolmate.me/images/product-detail/return-60.svg"
              alt=""
            />
            <span>60 ngày đổi trả vì bất kỳ lý do gì</span>
          </div>
          <div class="col-6 flex gap-2">
            <img
              src="https://www.coolmate.me/images/product-detail/phone.svg"
              alt=""
            />
            <span>Hotline 1900.27.27.37 hỗ trợ từ 8h30 - 22h mỗi ngày</span>
          </div>
          <div class="col-6 flex gap-2">
            <img
              src="https://www.coolmate.me/images/product-detail/location.svg"
              alt=""
            />
            <span>Đến tận nơi nhận hàng trả, hoàn tiền trong 24h</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import Breadcrumb from "primevue/breadcrumb";
import Rating from "primevue/rating";
import Badge from "primevue/badge";
import Panel from "primevue/panel";
import { ref, watch, computed } from "vue";
import Galleria from "primevue/galleria";
import RadioButton from "primevue/radiobutton";
const home = ref({
  icon: "pi pi-home",
  route: "/introduction",
});
const items = ref([
  { label: "Components" },
  { label: "Form" },
  { label: "InputText", route: "/inputtext" },
]);
const collapsed = ref(true);
const rating = ref(4.9);
const onToggle = (event) => {
  collapsed.value = !event.value; // Cập nhật trạng thái collapsed
};
const position = ref("bottom");
const responsiveOptions = ref([
  {
    breakpoint: "1300px",
    numVisible: 4,
  },
  {
    breakpoint: "575px",
    numVisible: 1,
  },
]);
const images = ref([
  {
    itemImageSrc:
      "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Navy_1.jpg",
    thumbnailImageSrc:
      "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Navy_1.jpg",
    alt: "Description for Image 1",
    title: "Title 1",
  },
  {
    itemImageSrc:
      "https://primefaces.org/cdn/primevue/images/galleria/galleria4.jpg",
    thumbnailImageSrc:
      "https://primefaces.org/cdn/primevue/images/galleria/galleria4s.jpg",
    alt: "Description for Image 1",
    title: "Title 1",
  },
]);
const size = ref(1);
const colorOptions = ref([
  {
    id: 1,
    type: 'color',
    color: '#3b82f6',
    alt: 'Xanh dương'
  },
  {
    id: 2,
    type: 'image',
    src: 'https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Navy-7.jpg',
    alt: 'Navy'
  },
  {
    id: 3,
    type: 'image',
    src: 'https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Den-7.jpg',
    alt: 'Đen'
  }
]);
const selectedColor = computed(() => {
  const selected = colorOptions.value.find(item => item.id === size.value);
  return selected?.alt || '';
});
watch(size, (newVal) => {
  console.log(newVal);
});
</script>
