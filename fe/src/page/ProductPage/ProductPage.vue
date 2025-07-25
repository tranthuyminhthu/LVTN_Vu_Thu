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
          <div class="flex justify-between items-center mb-4">
            <div class="flex items-center gap-2">
              <img src="" alt="" class="!w-10 !h-10 rounded-full">
              <span class="font-bold">Adidas</span>
            </div>
            <i class="pi pi-heart"></i>
          </div>
          <p class="font-bold text-3xl">{{ product?.name || "..." }}</p>
          <span class="flex gap-2 my-2"
            ><Rating :model-value="product?.rating" readonly />(39)</span
          >
          <div class="my-2">
            <del class="text-[#c4c4c4]">{{ formatVND(currentPrice) }}</del>
          </div>
          <p class="font-bold text-2xl flex gap-4 align-items-center">
            <span class="font-bold text-2xl">{{ formatVND(currentPrice) }}</span>
            <Badge>-11%</Badge>
          </p>
          <!-- <div class="flex gap-4 my-4">
            <span>Mã giảm giá</span>
            <span
              style="
                background-image: url(&quot;https://media.coolmate.me/image/September2024/mceclip0_126.png&quot;);
              "
              class="py-1 px-2 text-[#fa6a18] bg-cover w-[108px] rounded flex justify-content-center bg-no-repeat"
            >
              Giảm 40k
            </span>
          </div> -->
          <!-- <img
            src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/April2025/mceclip0_54.jpg"
            alt=""
            class="rounded-md"
          /> -->
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
            <SizePicker v-model="selectedSizeId" :sizeOptions="availableSizes" />
          </div>
          <Toast />
          <div class="mb-4 flex gap-2">
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
          <div class="flex gap-2 mb-4">
            <button
              class="border rounded-lg flex-1 py-2 bg-black text-white cursor-pointer"
              @click="addItemToCart"
            >
              Thêm vào giỏ hàng
            </button>
            <Button 
              icon="pi pi-comments" 
              class="rounded-lg text-black bg-gray-200 border-none hover:bg-gray-300" 
              @click="openChat" 
              :pt="{ icon: { class: 'text-black' } }"
            />
          </div>
          <div class="flex gap-2 align-items-center">
            <i class="pi pi-truck"></i>
            <p class="font-medium">Freeship đơn trên 200K</p>
          </div>
          <!-- <Panel
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
          </Panel> -->
          <!-- <div class="grid">
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
          </div> -->
        </div>
      </div>
    </div>
  </div>
  <DescriptionProduct />
  <CommentProduct :productId="Number(id)" />
</template>
<script setup lang="ts">
import Breadcrumb from "primevue/breadcrumb";
import Rating from "primevue/rating";
import Badge from "primevue/badge";
import Panel from "primevue/panel";
import { ref, watch, computed, onMounted } from "vue";
import ColorPicker from "@/components/ColorPicker.vue";
import type { ColorOption } from "@/components/ColorPicker.vue";
import SizePicker from "@/components/SizePicker.vue";
import type { SizeOption } from "@/components/SizePicker.vue";
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";
import Image from "primevue/image";
import CommentProduct from "./CommentProduct.vue";
import { useRoute } from "vue-router";
import { InputNumber } from "primevue";
import DescriptionProduct from "./DescriptionProduct.vue";
import type { Product } from "@/types";
import { getProduct } from "@/api/product";
import { formatVND } from "@/common";
import { addItemToCart as addItemToCartApi } from "@/api/cart";
import Button from 'primevue/button';
import type { CartItem } from "@/types";
import { createConversation } from "@/api/chat";
import router from "@/router";
const route = useRoute();
const id = String(route.params.id);
const product = ref<Product>();
const toast = useToast();
const quantity = ref(1);
const addItemToCart = () => {
  const cartItem: Partial<CartItem> = {
    sku: currentVariant.value?.sku || "",
    quantity: quantity.value,
    price: currentPrice.value,
    productDetails: product.value || undefined,
  };
  addItemToCartApi(cartItem as CartItem).then(() => {
    toast.add({
      severity: "success",
      summary: "Đã thêm vào giỏ hàng",
      detail: "Vào giỏ hàng để thêm chi tiết",
      life: 3000,
    });
  }).catch((error) => {
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Thêm vào giỏ hàng thất bại",
      life: 3000,
    });
  });
};
const home = ref({
  icon: "pi pi-home",
  route: "/",
});

const items = ref([
  // Danh mục nếu có, ví dụ:
  // { label: product.value?.category || "Danh mục" },
  { label: "Sản phẩm", route: "/products" },
  { label: product.value?.name || "..." },
]);

watch(product, (newProduct) => {
  if (newProduct) {
    items.value = [
      { label: "Sản phẩm", route: "/products" },
      { label: newProduct.name },
    ];
  }
});
const collapsed = ref(true);
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
const selectedSizeId = ref<string | number>(1);
const sizeOptions = ref<SizeOption[]>([]);

const selectedSize = computed(() => {
  const selected = sizeOptions.value.find(
    (item: SizeOption) => item.id === selectedSizeId.value
  );
  return selected?.label || "";
});
const selectedColorId = ref<string | number>(1);
const colorOptions = ref<ColorOption[]>([]);

const selectedColor = computed(() => {
  const selected = colorOptions.value.find(
    (item: ColorOption) => item.id === selectedColorId.value
  );
  return selected?.alt || "";
});

// Computed property to get current variant based on selected size and color
const currentVariant = computed(() => {
  if (!product.value?.variants) return null;
  
  const selectedSize = sizeOptions.value.find(option => option.id === selectedSizeId.value)?.label;
  const selectedColorName = colorOptions.value.find(option => option.id === selectedColorId.value)?.alt;
  
  const foundVariant = product.value.variants.find(variant => {
    const sizeMatch = variant.size === selectedSize;
    const colorMatch = variant.colorName === selectedColorName;
    return sizeMatch && colorMatch;
  });
  
  return foundVariant;
});

// Computed property for current price
const currentPrice = computed(() => {
  return currentVariant.value?.price || product.value?.price || 0;
});

// Computed properties to get available options based on current selection
const availableSizes = computed(() => {
  if (!product.value?.variants) return sizeOptions.value;
  
  const selectedColorName = colorOptions.value.find(option => option.id === selectedColorId.value)?.alt;
  if (!selectedColorName) return sizeOptions.value;
  
  // Get all sizes that have variants with the selected color
  const availableSizeLabels = [...new Set(
    product.value.variants
      .filter(variant => variant.colorName === selectedColorName)
      .map(variant => variant.size)
  )];
  
  return sizeOptions.value.map(option => ({
    ...option,
    disabled: !availableSizeLabels.includes(option.label)
  }));
});

// Watcher to update size when color changes
watch(selectedColorId, (newColorId) => {
  const selectedColorName = colorOptions.value.find(option => option.id === newColorId)?.alt;
  if (!selectedColorName || !product.value?.variants) return;
  
  // Find the first available size for the selected color
  const availableSize = product.value.variants
    .filter(variant => variant.colorName === selectedColorName)
    .map(variant => variant.size)[0];
  
  if (availableSize) {
    // Find the size option that matches the available size
    const sizeOption = sizeOptions.value.find(option => option.label === availableSize);
    if (sizeOption) {
      selectedSizeId.value = sizeOption.id;
    }
  }
});

const thumbnails = ref<string[]>([]);
const selectedImage = ref<string>("");

const changeImage = (image: string) => {
  selectedImage.value = image;
};

const zoomImage = () => {
  zoomDialogVisible.value = true;
};

const zoomDialogVisible = ref(false);

const openChat = async () => {
  const res = await createConversation([product.value?.vendorInfo.id || "", "197170"]);
  router.push({name: "chat", params: {id: res.id}})
};

onMounted(async () => {
  window.scrollTo(0, 0);
  product.value = await getProduct(id);
  
  // Map images from API
  if (product.value?.images && product.value.images.length > 0) {
    thumbnails.value = product.value.images.filter(img => typeof img === 'string');
    selectedImage.value = thumbnails.value[0];
  } else {
    thumbnails.value = [
      "https://via.placeholder.com/300x400?text=No+Image"
    ];
    selectedImage.value = thumbnails.value[0];
  }
  // Generate size options from product variants
  if (product.value?.variants) {
    const uniqueSizes = [...new Set(product.value.variants.map(variant => variant.size))];
    sizeOptions.value = uniqueSizes.map((size, index) => ({
      id: index + 1,
      label: size
    }));
    
    // Generate color options from product variants
    const uniqueColors = [...new Set(product.value.variants.map(variant => variant.colorName))];
    colorOptions.value = uniqueColors.map((colorName, index) => {
      // Find the first variant with this color name to get the hex value
      const variant = product.value!.variants.find(v => v.colorName === colorName);
      return {
        id: index + 1,
        type: "color" as const,
        color: variant?.colorHex || '#cccccc',
        alt: colorName
      };
    });
    
    // Set default selections based on first variant
    if (product.value.variants.length > 0) {
      const firstVariant = product.value.variants[0];
      
      // Find the size option that matches the first variant's size
      const defaultSizeOption = sizeOptions.value.find(option => option.label === firstVariant.size);
      if (defaultSizeOption) {
        selectedSizeId.value = defaultSizeOption.id;
      }
      
      // Find the color option that matches the first variant's color name
      const defaultColorOption = colorOptions.value.find(option => option.alt === firstVariant.colorName);
      if (defaultColorOption) {
        selectedColorId.value = defaultColorOption.id;
      }
    }
  }
});
</script>
