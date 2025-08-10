<template>
  <div class="grid p-4">
    <div class="col-3">
      <Accordion value="0">
        <AccordionPanel value="0">
          <AccordionHeader>Phù hợp với</AccordionHeader>
          <AccordionContent>
            <div class="m-0">
              <div class="flex flex-col gap-4">
                <div
                  v-for="category in categories"
                  :key="category.key"
                  class="flex items-center gap-2"
                >
                  <RadioButton
                    v-model="selectedCategory"
                    :inputId="category.key"
                    name="dynamic"
                    :value="category.name"
                  />
                  <label :for="category.key">{{ category.name }}</label>
                </div>
              </div>
            </div>
          </AccordionContent>
        </AccordionPanel>
        <AccordionPanel value="1">
          <AccordionHeader>Kích cỡ</AccordionHeader>
          <AccordionContent>
            <p class="m-0">
              <SizePicker v-model="selectedSizeId" :sizeOptions="sizeOptions" />
            </p>
          </AccordionContent>
        </AccordionPanel>
        <AccordionPanel value="2">
          <AccordionHeader>Màu sắc</AccordionHeader>
          <AccordionContent>
            <div class="m-0 grid">
              <div
                class="flex flex-col col-3 cursor-pointer justify-center items-center"
              >
                <span class="!w-8 h-8 rounded-full bg-black"></span>
                <span>Đen</span>
              </div>
              <div
                class="flex flex-col col-3 cursor-pointer justify-center items-center"
              >
                <span class="!w-8 h-8 rounded-full bg-gray-500"></span>
                <span>Xám</span>
              </div>
              <div class="flex flex-col col-3 justify-center items-center">
                <div class="!w-8 h-8 rounded-full bg-green-500"></div>
                <span>Xanh lá</span>
              </div>
              <div
                class="flex flex-col col-3 cursor-pointer justify-center items-center"
              >
                <span class="!w-8 h-8 rounded-full bg-yellow-500"></span>
                <span>Vàng</span>
              </div>
            </div>
          </AccordionContent>
        </AccordionPanel>
      </Accordion>
    </div>
    <div class="col-9">
      <!-- <Breadcrumb :model="items" class="p-0">
        <template #item="{ item, props }">
          <router-link
            v-if="item.route"
            v-slot="{ href, navigate }"
            :to="item.route"
            custom
          >
            <a :href="href" v-bind="props.action" @click="navigate">
              <span class="font-semibold">{{ item.label }}</span>
            </a>
          </router-link>
          <a
            v-else
            :href="item.url"
            :target="item.target"
            v-bind="props.action"
          >
            <span class="">{{ item.label }}</span>
          </a>
        </template>
        <template #separator> / </template>
      </Breadcrumb> -->
      <!-- <p class="uppercase text-xl font-bold my-4">Áo sơ mi nam</p>
      <div class="my-4 flex gap-4">
        <div class="flex  gap-2 flex-col cursor-pointer">
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=80,format=auto/uploads/March2025/quan-jeans-nam.png"
            alt=""
            class="w-32 rounded-lg"
          />
          <span class = "font-bold">Quần Jean</span>
        </div>
        <div class="flex  gap-2 flex-col cursor-pointer">
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=80,format=auto/uploads/March2025/quan-jogger-nam.jpg"
            alt=""
            class="w-32 rounded-lg"
          />
          <span class = "font-bold">Quần Jean</span>
        </div>
        <div class="flex  gap-2 flex-col cursor-pointer">
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=672,height=990,quality=80,format=auto/uploads/March2025/quan-kaki-nam.jpg"
            alt=""
            class="w-32 rounded-lg"
          />
          <span class = "font-bold">Quần Jean</span>
        </div>
        
      </div> -->
      <!-- <Divider align="center"> </Divider> -->
      <div class="my-4 flex justify-between items-center">
        <p>{{ productsPagination.totalElements }} kết quả</p>
        <div class="flex items-center gap-2">
          <p class="flex-1">Sắp xếp theo</p>
          <Select
            v-model="selectedOrderBy"
            :options="orderBy"
            optionLabel="name"
            placeholder="Chọn cách sắp xếp"
            :pt="{
              root: {
                class: '!w-48 !rounded-full ',
              },
            }"
          />
        </div>
      </div>
      
      <div v-if="loading" class="flex justify-center items-center py-8">
        <div class="loading-spinner"></div>
        <span class="ml-2">Đang tải sản phẩm...</span>
      </div>
      
      <div v-else class="grid">
        <div class="col-3" v-for="product in products" :key="product.id">
          <ProductCard :product="product" />
        </div>
      </div>
      
      <Paginator
        v-if="productsPagination.totalElements > 0"
        :rows="productsPagination.size"
        :totalRecords="productsPagination.totalElements"
        :first="productsPagination.page * productsPagination.size"
        @page="onProductPageChange"
        :lazy="true"
        :rowsPerPageOptions="[4, 8, 12, 16]"
        class="mt-6"
      />
    </div>
  </div>
</template>
<script setup lang="ts">
import Divider from "primevue/divider";
import ProductCard from "@/components/ProductCard.vue";
import Breadcrumb from "primevue/breadcrumb";
import Paginator from "primevue/paginator";
import { onMounted, ref, watch } from "vue";
import Select from "primevue/select";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import RadioButton from "primevue/radiobutton";
import SizePicker, { type SizeOption } from "@/components/SizePicker.vue";
import { getProducts, type ProductResponse, type ProductQueryParams } from "@/api/product";
import type { Product } from "@/types";

// API data for products
const products = ref<Product[]>([]);
const loading = ref(false);
const productsPagination = ref({
  page: 0,
  size: 8,
  totalElements: 0,
  totalPages: 0
});

// Gender mapping
const genderMap = {
  'Nam': 'MEN',
  'Nữ': 'WOMEN'
};

// Load products from API
const loadProducts = async () => {
  try {
    loading.value = true;
    const params: ProductQueryParams = {
      page: productsPagination.value.page,
      size: productsPagination.value.size,
      status: 'ACCEPTED'
    };
    
    // Add gender parameter if selected
    if (selectedCategory.value && genderMap[selectedCategory.value as keyof typeof genderMap]) {
      params.gender = genderMap[selectedCategory.value as keyof typeof genderMap];
    }
    
    const response = await getProducts(params);
    
    // Convert API Product to types Product
    products.value = response.products.map(product => ({
      id: product.id.toString(), // Convert number to string
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || '',
      isFavorite: false,
      viewCount: product.viewCount || 0,
      variants: product.variants?.map(variant => ({
        sku: variant.sku || '',
        productId: variant.productId?.toString() || '',
        size: variant.size,
        colorName: variant.colorName,
        colorHex: variant.colorHex,
        price: variant.price,
        stockQuantity: variant.stockQuantity
      })),
      images: product.images || [],
      vendorInfo: {
        id: product.createdBy || '',
        name: 'Vendor',
        image: ''
      }
    })) as unknown as Product[];
    
    productsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages
    };
  } catch (error) {
    console.error('Error loading products:', error);
  } finally {
    loading.value = false;
  }
};

// Handle product pagination
const onProductPageChange = (event: { first: number; rows: number; page: number }) => {
  console.log('Product page change event:', event);
  productsPagination.value.page = event.page;
  productsPagination.value.size = event.rows;
  loadProducts();
};

const items = ref([
  { label: "Trang chủ", route: "/" },
  { label: "Đồ nam", route: "/products" },
  { label: "Áo nam", route: "/inputtext" },
]);
const selectedOrderBy = ref({ name: "Bán chạy", value: "popular" });
const orderBy = ref([
  { name: "Bán chạy", value: "popular" },
  { name: "Mới nhất", value: "latest" },
  { name: "Giá thấp đến cao", value: "price-asc" },
  { name: "Giá cao đến thấp", value: "price-desc" },
  { name: "% Giảm giá nhiều", value: "discount" },
]);
const selectedCategory = ref("Nam");
const categories = ref([
  { name: "Nữ", key: "F" },
  { name: "Nam", key: "M" },
]);
const selectedSizeId = ref(1);
const sizeOptions = ref<SizeOption[]>([
  { id: 1, label: "S" },
  { id: 2, label: "M" },
  { id: 3, label: "L" },
  { id: 4, label: "XL" },
  { id: 5, label: "XXL" },
]);

// Sort products function
const sortProducts = (products: Product[], sortType: string) => {
  const sortedProducts = [...products];
  
  switch (sortType) {
    case 'price-asc':
      return sortedProducts.sort((a, b) => a.price - b.price);
    case 'price-desc':
      return sortedProducts.sort((a, b) => b.price - a.price);
    case 'latest':
      // Assuming products have createdAt field, if not, keep original order
      return sortedProducts;
    case 'popular':
      // Assuming products have viewCount field, if not, keep original order
      return sortedProducts.sort((a, b) => ((b as any).viewCount || 0) - ((a as any).viewCount || 0));
    case 'discount':
      // For now, keep original order since discount calculation might need more logic
      return sortedProducts;
    default:
      return sortedProducts;
  }
};

// Watch for category changes to reload products
watch(selectedCategory, () => {
  productsPagination.value.page = 0; // Reset to first page
  loadProducts();
});

// Watch for sort changes
watch(selectedOrderBy, () => {
  if (products.value.length > 0) {
    products.value = sortProducts(products.value, selectedOrderBy.value.value);
  }
});

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
