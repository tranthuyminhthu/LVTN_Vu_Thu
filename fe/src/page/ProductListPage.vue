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
      <Breadcrumb :model="items" class="p-0">
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
      </Breadcrumb>
      <p class="uppercase text-xl font-bold my-4">Áo sơ mi nam</p>
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
        
      </div>
      <Divider align="center"> </Divider>
      <div class="my-4 flex justify-between items-center">
        <p>9 kết quả</p>
        <div class="flex items-center gap-2">
          <p class="flex-1">Sắp xếp theo</p>
          <Select
            v-model="selectedOrderBy"
            :options="orderBy"
            optionLabel="name"
            placeholder="Select a City"
            :pt="{
              root: {
                class: '!w-48 !rounded-full ',
              },
            }"
          />
        </div>
      </div>
      <div class="grid">
        <div class="col-3" v-for="product in products" :key="product.id">
          <ProductCard :product="product" />
        </div>
      
        
      </div>
      <button
          class="mx-auto mt-4 bg-black text-white px-4 py-2 rounded-full uppercase block"
        >
          Xem thêm
        </button>
    </div>
  </div>
</template>
<script setup lang="ts">
import Divider from "primevue/divider";
import ProductCard from "@/components/ProductCard.vue";
import Breadcrumb from "primevue/breadcrumb";
import { onMounted, ref } from "vue";
import Select from "primevue/select";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import RadioButton from "primevue/radiobutton";
import SizePicker, { type SizeOption } from "@/components/SizePicker.vue";
import { getProducts } from "@/api/product";
import type { Product } from "@/types";

const products = ref<Product[]>([]);
const loading = ref(false);

const items = ref([
  { label: "Trang chủ", route: "/" },
  { label: "Đồ nam", route: "/products" },
  { label: "Áo nam", route: "/inputtext" },
]);
const selectedOrderBy = ref({ name: "Bán chạy"});
const orderBy = ref([
  { name: "Bán chạy"},
  { name: "Mới nhất"},
  { name: "Giá thấp đến cao"},
  { name: "Giá cao đến thấp"},
  { name: "% Giảm giá nhiều"},
]);
const selectedCategory = ref("Production");
const categories = ref([
  { name: "Đi tiệc", key: "A" },
  { name: "Mặc hàng ngày", key: "M" },
  { name: "Công sở", key: "P" },
  { name: "Thể thao", key: "R" },
]);
const selectedSizeId = ref(1);
const sizeOptions = ref<SizeOption[]>([
  { id: 1, label: "S" },
  { id: 2, label: "M" },
  { id: 3, label: "L" },
  { id: 4, label: "XL" },
  { id: 5, label: "XXL" },
]);

onMounted(async () => {
  loading.value = true;
  products.value = await getProducts();
  loading.value = false;
});
</script>
