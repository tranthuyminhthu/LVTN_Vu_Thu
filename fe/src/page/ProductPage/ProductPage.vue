<template>
  <div class="w-primary mx-auto pt-12">
    <Breadcrumb :home="home" :model="items" class="">
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
      <div class="col-6 !grid !grid-cols-12">
        <div class="col-span-2 flex flex-col gap-4">
          <img
            v-for="(thumbnail, index) in thumbnails"
            :key="index"
            :src="thumbnail"
            class="rounded-md cursor-pointer hover:opacity-80 transition-opacity w-8"
            :class="[
              thumbnail === selectedImage ? 'opacity-100' : 'opacity-60',
            ]"
            @click="changeImage(thumbnail)"
            alt=""
          />
        </div>
        <div class="col-span-10">
          <Image
            :src="selectedImage"
            alt="Image"
            class="rounded-md w-full"
            preview
          />
        </div>
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
            <span>Màu sắc: </span
            ><span class="font-bold">{{ selectedColor }}</span>
          </p>
          <div class="flex flex-wrap gap-4">
            <ColorPicker
              v-model="selectedColorId"
              :colorOptions="colorOptions"
            />
          </div>
          <p class="my-2">
            <span>Kích thước: </span
            ><span class="font-bold">{{ selectedSize }}</span>
          </p>
          <div class="flex flex-wrap gap-4 mb-4">
            <SizePicker v-model="selectedSizeId" :sizeOptions="sizeOptions" />
          </div>
          <Toast />
          <div class="mb-4">
            <InputNumber
            v-model="quantity"
            showButtons
            buttonLayout="horizontal"
            :min="0"
            :max="99"
            size="small"
            input-class="w-[36px]"
          >
            <template #incrementbuttonicon>
              <span class="pi pi-plus" />
            </template>
            <template #decrementbuttonicon>
              <span class="pi pi-minus" />
            </template>
          </InputNumber>
          </div>
          <button
            class="border rounded-full w-full py-2 bg-black text-white cursor-pointer"
            @click="show"
          >
            Thêm vào giỏ hàng
          </button>
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
              <i
                :class="['pi', collapsed ? 'pi-angle-down' : 'pi-angle-up']"
              ></i>
            </template>
            <p class="m-0">
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut
              enim ad minim veniam, quis nostrud exercitation ullamco laboris
              nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in
              reprehenderit in voluptate velit esse cillum dolore eu fugiat
              nulla pariatur. Excepteur sint occaecat cupidatat non proident,
              sunt in culpa qui officia deserunt mollit anim id est laborum.
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
  </div>
  <DescriptionProduct />
  <CommentProduct />
</template>
<script setup lang="ts">
import Breadcrumb from "primevue/breadcrumb";
import Rating from "primevue/rating";
import Badge from "primevue/badge";
import Panel from "primevue/panel";
import { ref, watch, computed } from "vue";
import ColorPicker from "@/components/ColorPicker.vue";
import type { ColorOption } from "@/components/ColorPicker.vue";
import SizePicker from "@/components/SizePicker.vue";
import type { SizeOption } from "@/components/SizePicker.vue";
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import Image from "primevue/image";
import CommentProduct from "./CommentProduct.vue";
import Dialog from "primevue/dialog";
import { InputNumber } from "primevue";
import DescriptionProduct from "./DescriptionProduct.vue";
const toast = useToast();
const quantity = ref(1);
const show = () => {
  toast.add({
    severity: "success",
    summary: "Đã thêm vào giỏ hàng",
    detail: "Vào giỏ hàng để thêm chi tiết",
    life: 3000,
  });
};
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
const onToggle = (event: { value: boolean }) => {
  collapsed.value = !event.value;
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
const selectedSizeId = ref(1);
const sizeOptions = ref<SizeOption[]>([
  { id: 1, label: "S" },
  { id: 2, label: "M" },
  { id: 3, label: "L" },
  { id: 4, label: "XL" },
  { id: 5, label: "XXL" },
]);

const selectedSize = computed(() => {
  const selected = sizeOptions.value.find(
    (item: SizeOption) => item.id === selectedSizeId.value
  );
  return selected?.label || "";
});
const selectedColorId = ref(1);
const colorOptions = ref<ColorOption[]>([
  {
    id: 1,
    type: "color" as const,
    color: "#3b82f6",
    alt: "Xanh dương",
  },
  {
    id: 2,
    type: "image" as const,
    src: "https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/May2025/Ao-tanktop-nam-mac-trong-anti-smell-Navy-7.jpg",
    alt: "Navy",
  },
  {
    id: 3,
    type: "image" as const,
    src: "https://media3.coolmate.me/cdn-cgi/image/width=160,height=160,quality=80,format=auto/uploads/April2025/ao-singlet-chay-bo-advanced-vent-techgraphic-pixel-381-vang.jpg",
    alt: "Đen",
  },
]);

const selectedColor = computed(() => {
  const selected = colorOptions.value.find(
    (item: ColorOption) => item.id === selectedColorId.value
  );
  return selected?.alt || "";
});

watch(selectedColorId, (newVal) => {
  console.log("Selected color:", newVal);
});

watch(selectedSizeId, (newVal) => {
  console.log("Selected size:", newVal);
});

const selectedImage = ref(
  "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/April2025/ao-singlet-chay-bo-advanced-vent-techgraphic-pixel-360-vang_60.jpg"
);

const thumbnails = [
  "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/April2025/ao-singlet-chay-bo-advanced-vent-techgraphic-pixel-360-vang_60.jpg",
  "https://media3.coolmate.me/cdn-cgi/image/format=auto/uploads/April2025/ao-singlet-chay-bo-advanced-vent-techgraphic-pixel-379-vang_87.jpg",
];

const changeImage = (image: string) => {
  selectedImage.value = image;
};

const zoomImage = () => {
  zoomDialogVisible.value = true;
};

const zoomDialogVisible = ref(false);
</script>
