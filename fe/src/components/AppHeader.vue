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
      <MegaMenu :model="items" class="border-none">
        <template #item="{ item }">
          <router-link
            v-if="item.route"
            v-slot="{ href, navigate }"
            :to="item.route"
            custom
          >
            <a v-ripple :href="href" @click="navigate">
              <span :class="item.icon" />
              <span class="ml-2">{{ item.label }}</span>
            </a>
          </router-link>
          <a
            v-else
            v-ripple
            :href="item.url"
            :target="item.target"
            class="flex justify-center"
          >
            <span :class="item.icon" />
            <span class="font-bold uppercase text-center ml-8">{{
              item.label
            }}</span>
          </a>
        </template>
      </MegaMenu>
    </div>

    <!-- Search -->
    <div class="flex items-center gap-4 justify-center">
      <IconField>
        <InputText
          v-model="value1"
          placeholder="Tìm kiếm sản phẩm"
          size="small"
          class="!rounded-full"
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
          header="Hi, Vụ Nguyễn Duy"
          position="right"
          :showCloseIcon="false"
        >
          <p class="">
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim
            ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut
            aliquip ex ea commodo consequat.
          </p>
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
import MegaMenu from "primevue/megamenu";
import InputText from "primevue/inputtext";
import OverlayBadge from "primevue/overlaybadge";
import Drawer from "primevue/drawer";
import { ref } from "vue";

const router = useRouter();
const searchText = ref("");

const items = ref([
  {
    label: "Furniture",
    command: () => console.log("a"),
    items: [
      [
        {
          label: "Living Room",
          items: [
            {
              label: "Accessories",
              command: () => console.log("Accessories"),
              route: "/accessories",
            },
            { label: "Armchair" },
            { label: "Coffee Table" },
            { label: "Couch" },
            { label: "TV Stand" },
          ],
        },
      ],
      [
        {
          label: "Kitchen",
          items: [
            { label: "Bar stool" },
            { label: "Chair" },
            { label: "Table" },
          ],
        },
        {
          label: "Bathroom",
          items: [{ label: "Accessories" }],
        },
      ],
      [
        {
          label: "Bedroom",
          items: [
            { label: "Bed" },
            { label: "Chaise lounge" },
            { label: "Cupboard" },
            { label: "Dresser" },
            { label: "Wardrobe" },
          ],
        },
      ],
      [
        {
          label: "Office",
          items: [
            { label: "Bookcase" },
            { label: "Cabinet" },
            { label: "Chair" },
            { label: "Desk" },
            { label: "Executive Chair" },
          ],
        },
      ],
    ],
  },
  {
    label: "Electronics",
    items: [
      [
        {
          label: "Computer",
          items: [
            { label: "Monitor" },
            { label: "Mouse" },
            { label: "Notebook" },
            { label: "Keyboard" },
            { label: "Printer" },
            { label: "Storage" },
          ],
        },
      ],
      [
        {
          label: "Home Theater",
          items: [
            { label: "Projector" },
            { label: "Speakers" },
            { label: "TVs" },
          ],
        },
      ],
      [
        {
          label: "Gaming",
          items: [
            { label: "Accessories" },
            { label: "Console" },
            { label: "PC" },
            { label: "Video Games" },
          ],
        },
      ],
      [
        {
          label: "Appliances",
          items: [
            { label: "Coffee Machine" },
            { label: "Fridge" },
            { label: "Oven" },
            { label: "Vaccum Cleaner" },
            { label: "Washing Machine" },
          ],
        },
      ],
    ],
  },
  {
    label: "Sports",
    items: [
      [
        {
          label: "Football",
          items: [
            { label: "Kits" },
            { label: "Shoes" },
            { label: "Shorts" },
            { label: "Training" },
          ],
        },
      ],
      [
        {
          label: "Running",
          items: [
            { label: "Accessories" },
            { label: "Shoes" },
            { label: "T-Shirts" },
            { label: "Shorts" },
          ],
        },
      ],
      [
        {
          label: "Swimming",
          items: [
            { label: "Kickboard" },
            { label: "Nose Clip" },
            { label: "Swimsuits" },
            { label: "Paddles" },
          ],
        },
      ],
      [
        {
          label: "Tennis",
          items: [
            { label: "Balls" },
            { label: "Rackets" },
            { label: "Shoes" },
            { label: "Training" },
          ],
        },
      ],
    ],
  },
]);
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
</script>
