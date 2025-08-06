<template>
  <div class="product-card">
    <div class="image-wrapper">
      <img
        :src="typeof props.product.images?.[0] === 'string' ? props.product.images[0] : 'https://via.placeholder.com/300'"
        alt=""
        class="product-image"
        @click="handleClick"
      />
      <img
        :src="typeof props.product.images?.[1] === 'string'
          ? props.product.images[1]
          : (typeof props.product.images?.[0] === 'string'
              ? props.product.images[0]
              : 'https://via.placeholder.com/300')"
        alt=""
        class="product-image hover-image"
        @click="handleClick"
      />
    </div>
    <div class="product-content">
      <p class="product-name">{{ props.product.name }}</p>
      <ColorPicker v-model="selectedColor" :colorOptions="colorOptions" :name="'product_' + props.product.id" />
      <div class="flex items-center gap-2 text-sm mt-2">
        <span class="font-bold">{{ formatVND(selectedVariant?.price || props.product.price) }}</span>
        <!-- <span
          class="rounded-lg bg-[#273bcd] p-1 text-white text-sm font-bold"
          style="font-size: 10px !important"
          >-20%</span
        > -->
        <!-- <span class="line-through text-[#c4c4c4]">{{ formatVND(selectedVariant?.price || props.product.price) }}</span> -->
      </div>
    </div>
    <div class="quick-add p-2">
      <p class="font-bold">Thêm nhanh vào giỏ hàng</p>
      <div class="flex gap-2 my-2">
        <span
          v-for="size in availableSizes"
          :key="size"
          class="bg-white px-3 py-1 text-center rounded-lg cursor-pointer size"
          :class="{ 'selected-size': selectedSize === size }"
          @click="selectSize(size)"
        >
          {{ size }}
        </span>
      </div>
    </div>
    <!-- <div
      class="absolute uppercase text-white text-sm font-bold bg-[#273bcd] px-2 py-1 rounded-full right-[10px] top-[10px]"
      style="font-size: 10px !important"
    >
      New
    </div> -->
    <div class="absolute top-2 left-2 flex gap-1 text-sm">
      <span class="font-bold">{{ props.product.rating || 0 }}</span>
      <Rating :stars="1" :modelValue="1"/>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import ColorPicker, { type ColorOption } from "./ColorPicker.vue";
import { ref, watch, computed } from "vue";
import { Rating } from "primevue";
import type { Product, ProductVariant } from "@/types";
import { formatVND } from "@/common";

const router = useRouter();

const props = defineProps<{
  product: Product;
}>();

const handleClick = () => {
  router.push({ name: "product" , params: { id: props.product.id } });
};

const selectedColor = ref(1);
const selectedSize = ref<string>('');

// Generate color options from variants
const colorOptions = computed<ColorOption[]>(() => {
  const uniqueColors = new Map<string, { colorHex: string; colorName: string }>();
  
  props.product.variants?.forEach(variant => {
    if (!uniqueColors.has(variant.colorHex)) {
      uniqueColors.set(variant.colorHex, {
        colorHex: variant.colorHex,
        colorName: variant.colorName
      });
    }
  });

  return Array.from(uniqueColors.values()).map((color, index) => ({
    id: index + 1,
    type: 'color' as const,
    color: color.colorHex,
    alt: color.colorName
  }));
});

// Generate available sizes from variants
const availableSizes = computed<string[]>(() => {
  const sizes = new Set<string>();
  props.product.variants?.forEach(variant => {
    sizes.add(variant.size);
  });
  return Array.from(sizes).sort();
});

// Get selected variant based on color and size
const selectedVariant = computed<ProductVariant | undefined>(() => {
  if (!selectedColor.value || !selectedSize.value) return undefined;
  
  const selectedColorOption = colorOptions.value[selectedColor.value - 1];
  if (!selectedColorOption) return undefined;
  
  return props.product.variants?.find(variant => 
    variant.colorHex === selectedColorOption.color && 
    variant.size === selectedSize.value
  );
});

const selectSize = (size: string) => {
  selectedSize.value = selectedSize.value === size ? '' : size;
};

// Set default selections
watch(() => props.product, (newProduct) => {
  if (newProduct.variants?.length > 0) {
    selectedColor.value = 1;
    selectedSize.value = availableSizes.value[0] || '';
  }
}, { immediate: true });

</script>

<style scoped>
.size:hover {
  background-color: black !important;
  color: white;
}

.selected-size {
  background-color: black !important;
  color: white;
}

.product-card {
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
  min-height: 400px;
  background: white;
  border-radius: 0.5rem;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.product-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.image-wrapper {
  position: relative;
  width: 100%;
  height: 300px;
  overflow: hidden;
}

.product-image {
  border-radius: 0.5rem 0.5rem 0 0;
  cursor: pointer;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

.hover-image {
  position: absolute;
  top: 0;
  left: 0;
  opacity: 0;
}

.product-card:hover .hover-image {
  opacity: 1;
}

.product-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 1rem;
  gap: 0.5rem;
}

.product-name {
  font-weight: 600;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 2.8em;
}

.quick-add {
  position: absolute;
  background:
    linear-gradient(0deg, rgba(0, 0, 0, 0.1), rgba(0, 0, 0, 0.1)),
    hsla(0, 0%, 100%, 0.4);
  backdrop-filter: blur(20px);
  width: 80%;
  left: 50%;
  transform: translateX(-50%) translateY(20px);
  bottom: 25%;
  border-radius: 0.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-direction: column;
  opacity: 0;
  transition: all 0.3s ease;
  visibility: hidden;
}

.product-card:hover .quick-add {
  opacity: 1;
  transform: translateX(-50%) translateY(0);
  visibility: visible;
}
</style>
