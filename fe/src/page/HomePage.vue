<script setup lang="ts">
import AppHeader from "../components/AppHeader.vue";
import AppBanner from "../components/AppBanner.vue";
import HomeSection2 from "@/components/HomeSection3.vue";
import HomeSection3 from "@/components/HomeSection3.vue";
import HomeSection4 from "@/components/HomeSection4.vue";
import HomeSection5 from "@/components/HomeSection5.vue";
import HomeSection6 from "@/components/HomeSection6.vue";
import HomeSection7 from "@/components/HomeSection7.vue";
import AppFooter from "@/components/AppFooter.vue";
import AuthTest from "@/components/AuthTest.vue";
// Import Swiper Vue.js components
import { Swiper, SwiperSlide } from "swiper/vue";
import type { Swiper as SwiperType } from "swiper";
import { Autoplay, Pagination, Navigation } from "swiper/modules";
import { ref } from 'vue';

// Import Swiper styles
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import ProductCard from "@/components/ProductCard.vue";
import type { Product } from "@/types";

const onSwiper = (swiper: SwiperType) => {
  console.log(swiper);
};

const modules = [Navigation, Pagination, Autoplay];

const selectedGender = ref('men');

const menProducts = [
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Tshirt.jpg",
    title: "Áo thun nam"
  },
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Polo_61.jpg",
    title: "Áo polo nam"
  },
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Short_Nam1.jpg",
    title: "Quần short nam"
  }
];

const womenProducts = [
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Phu_kien12.jpg",
    title: "Phụ kiện"
  },
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Ao_Nu12.jpg",
    title: "Áo Thể thao"
  },
  {
    image: "https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/June2025/Shorts_nu12.jpg",
    title: "Quần thể thao"
  }
];

// Sample product data for ProductCard components
const sampleProducts: Product[] = [
  {
    id: "1",
    name: "Áo thun nam basic",
    description: "Áo thun nam chất liệu cotton thoáng mát",
    price: 199000,
    rating: 4.5,
    status: "active",
    variants: []
  },
  {
    id: "2", 
    name: "Áo polo nam thể thao",
    description: "Áo polo nam phù hợp cho thể thao",
    price: 299000,
    rating: 4.8,
    status: "active",
    variants: []
  },
  {
    id: "3",
    name: "Quần jean nam slim fit",
    description: "Quần jean nam kiểu dáng slim fit hiện đại",
    price: 399000,
    rating: 4.6,
    status: "active",
    variants: []
  },
  {
    id: "4",
    name: "Áo khoác denim nam",
    description: "Áo khoác denim nam phong cách casual",
    price: 599000,
    rating: 4.7,
    status: "active",
    variants: []
  }
];
</script>

<template>
  <div class="relative">
    <div class="">
      <!-- <AppBanner />
      <HomeSection2 />
      <HomeSection3 />
      <HomeSection4 />
      <HomeSection5 />
      <HomeSection6 />
      <HomeSection7 /> -->
      <swiper
        :slides-per-view="1"
        @swiper="onSwiper"
        class="w-full h-screen"
        :modules="modules"
        :autoplay="{
          delay: 2500,
          disableOnInteraction: false,
        }"
        :navigation="true"
      >
        <swiper-slide>
          <a href="/ai-recommendation">
            <img
              src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/April2025/1920x788(1)promax.jpg"
              alt=""
            />
          </a>
        </swiper-slide>
        <swiper-slide>
          <a href="/ai-recommendation">
            <img
              src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/May2025/T-SHIRT__POLO_THE_THAO_-_Desktop-1.jpg"
              alt=""
            />
          </a>
        </swiper-slide>
        <swiper-slide>
          <a href="/ai-recommendation">
            <img
              src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/May2025/T-SHIRT__POLO_THE_THAO_-_Desktopzz.jpg"
              alt=""
            />
          </a>
        </swiper-slide>
      </swiper>
      <div class="w-primary mx-auto mb-8">
        <div class="flex gap-4">
          <span 
            @click="selectedGender = 'men'"
            :class="[
              'px-5 py-2 rounded-full font-bold text-xl cursor-pointer',
              selectedGender === 'men' ? 'bg-black text-white' : 'bg-gray-200'
            ]"
          >
            Đồ nam
          </span>
          <span 
            @click="selectedGender = 'women'"
            :class="[
              'px-5 py-2 rounded-full font-bold text-xl cursor-pointer',
              selectedGender === 'women' ? 'bg-black text-white' : 'bg-gray-200'
            ]"
          >
            Đồ nữ
          </span>
        </div>
        <div class="flex mt-4 gap-4">
          <div v-for="(product, index) in selectedGender === 'men' ? menProducts : womenProducts" 
               :key="index" 
               class="flex flex-col gap-4">
            <a href="/products" class="overflow-hidden rounded-2xl">
              <img
                :src="product.image"
                :alt="product.title"
                class="w-[280px] hover:scale-105 transition-all duration-300 object-cover flex-1 !rounded-2xl"
              />
            </a>
            <div class="text-2xl font-bold text-center">{{ product.title }}</div>
          </div>
        </div>
      </div>
      <div class="w-primary mx-auto mb-8 flex gap-4">
        <div class="relative flex-1 overflow-hidden rounded-2xl">
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=1800,height=1200,quality=80,format=auto/uploads/May2025/Frame_87642.jpg"
            alt=""
            class="w-full object-cover rounded-2xl hover:scale-105 transition-all duration-300"
          />
          <span class="font-bold text-2xl absolute bottom-42 left-8 text-white"
            >MEN WEAR</span
          >
          <span class="font-bold text-lg absolute bottom-32 left-8 text-white"
            >Nhập COOLNEW Giảm 30k đơn từ 199k</span
          >
          <button
            class="font-bold text-2xl absolute bottom-14 left-8 bg-white rounded-full px-4 py-2"
          >
            Mua ngay
          </button>
        </div>
        <div class="relative flex-1 overflow-hidden rounded-2xl">
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=1800,height=1200,quality=80,format=auto/uploads/May2025/Frame_87638.jpg"
            alt=""
            class="w-full object-cover rounded-2xl hover:scale-105 transition-all duration-300"
          />
          <span class="font-bold text-2xl absolute bottom-42 left-8 text-white"
            >WOMEN ACTIVE</span
          >
          <span class="font-bold text-lg absolute bottom-32 left-8 text-white"
            >Tặng Cốc Coolmate đơn từ 299k | Freeship</span
          >
          <button
            class="font-bold text-2xl absolute bottom-14 left-8 bg-white rounded-full px-4 py-2"
          >
            Mua ngay
          </button>
        </div>
      </div>
      <div class="relative">
        <div class="font-bold text-8xl absolute bottom-86 left-20 text-white">
          CASUALWEAR
        </div>
        <div class="font-bold text-8xl absolute bottom-62 left-20 text-white">
          COLLECTION
        </div>
        <div class="font-bold text-2xl absolute bottom-52 left-20 text-white">
          Nhập COOLNEW Giảm 30k đơn đầu tiên từ 199k
        </div>
        <img
          src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Casual_-_Desktop.jpg"
          alt=""
        />
        <button
          class="font-bold text-2xl absolute bottom-20 left-20 bg-white rounded-full px-6 py-4"
        >
          Mua ngay
        </button>
      </div>
      <div class="w-primary mx-auto mb-8 mt-4">
        <div class="!font-criteria font-bold mb-2 text-2xl">SẢN PHẨM MẶC HẰNG NGÀY</div>
        <div class="grid">
          <div class="col-3">
            <ProductCard :product="sampleProducts[0]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[1]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[2]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[3]" />
          </div>
        </div>
      </div>
      <div class="relative">
        <div class="font-bold text-8xl absolute bottom-86 left-20 text-white">
          CASUALWEAR
        </div>
        <div class="font-bold text-8xl absolute bottom-62 left-20 text-white">
          COLLECTION
        </div>
        <div class="font-bold text-2xl absolute bottom-52 left-20 text-white">
          Nhập COOLNEW Giảm 30k đơn đầu tiên từ 199k
        </div>
        <img
          src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Casual_-_Desktop.jpg"
          alt=""
        />
        <button
          class="font-bold text-2xl absolute bottom-20 left-20 bg-white rounded-full px-6 py-4"
        >
          Mua ngay
        </button>
      </div>
      <div class="w-primary mx-auto mb-8">
        <div>SẢN PHẨM MẶC HẰNG NGÀY</div>
        <div class="grid">
          <div class="col-3">
            <ProductCard :product="sampleProducts[0]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[1]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[2]" />
          </div>
          <div class="col-3">
            <ProductCard :product="sampleProducts[3]" />
          </div>
        </div>
      </div>
      
      <!-- <div class="w-primary mx-auto mb-8">
        <AuthTest />
      </div>
      
      <div class="w-primary mx-auto mb-8">
        <UserInfo />
      </div> -->
    </div>
  </div>
</template>