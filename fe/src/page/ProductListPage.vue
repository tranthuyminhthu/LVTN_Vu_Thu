<template>
  <div class="grid">
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
              Sed ut perspiciatis unde omnis iste natus error sit voluptatem
              accusantium doloremque laudantium, totam rem aperiam, eaque ipsa
              quae ab illo inventore veritatis et quasi architecto beatae vitae
              dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit
              aspernatur aut odit aut fugit, sed quia consequuntur magni dolores
              eos qui ratione voluptatem sequi nesciunt. Consectetur, adipisci
              velit, sed quia non numquam eius modi.
            </p>
          </AccordionContent>
        </AccordionPanel>
        <AccordionPanel value="2">
          <AccordionHeader>Màu sắc</AccordionHeader>
          <AccordionContent>
            <div class="m-0 grid">
              <div class="flex flex-col col-3 cursor-pointer  justify-center items-center">
                <span class="!w-8 h-8 rounded-full bg-black"></span>
                <span>Đen</span>
              </div>
              <div class="flex flex-col col-3 cursor-pointer justify-center items-center">
                <span class="!w-8 h-8 rounded-full bg-gray-500"></span>
                <span>Xám</span>
              </div>
              <div class="flex flex-col col-3 justify-center items-center">
                <div class="!w-8 h-8 rounded-full bg-green-500"></div>
                <span>Xanh lá</span>
              </div>
              <div class="flex flex-col col-3 cursor-pointer justify-center items-center">
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
      <Divider align="center"> </Divider>
      <div class="my-4 flex justify-between items-center">
        <p>9 kết quả</p>
        <div class="flex items-center gap-2">
          <p class="flex-1">Sắp xếp theo</p>
          <Select
            v-model="selectedCity"
            :options="cities"
            optionLabel="name"
            placeholder="Select a City"
            class="w-44"
          />
        </div>
      </div>
      <div class="grid">
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <div class="col-3">
          <ProductCard />
        </div>
        <button
          class="mx-auto mt-4 bg-black text-white px-4 py-2 rounded-full uppercase"
        >
          Xem thêm
        </button>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import Divider from "primevue/divider";
import ProductCard from "@/components/ProductCard.vue";
import Breadcrumb from "primevue/breadcrumb";
import { ref } from "vue";
import Select from "primevue/select";
import Accordion from "primevue/accordion";
import AccordionPanel from "primevue/accordionpanel";
import AccordionHeader from "primevue/accordionheader";
import AccordionContent from "primevue/accordioncontent";
import RadioButton from "primevue/radiobutton";
const items = ref([
  { label: "Trang chủ", route: "/" },
  { label: "Đồ nam", route: "/products" },
  { label: "Áo nam", route: "/inputtext" },
]);
const selectedCity = ref();
const cities = ref([
  { name: "New York", code: "NY" },
  { name: "Rome", code: "RM" },
  { name: "London", code: "LDN" },
  { name: "Istanbul", code: "IST" },
  { name: "Paris", code: "PRS" },
]);
const selectedCategory = ref("Production");
const categories = ref([
  { name: "Accounting", key: "A" },
  { name: "Marketing", key: "M" },
  { name: "Production", key: "P" },
  { name: "Research", key: "R" },
]);
</script>
