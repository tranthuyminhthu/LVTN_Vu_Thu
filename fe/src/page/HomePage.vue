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
// Import Swiper Vue.js components
import { Swiper, SwiperSlide } from "swiper/vue";
import type { Swiper as SwiperType } from "swiper";
import { Autoplay, Pagination, Navigation } from "swiper/modules";

// Import Swiper styles
import "swiper/css";
import "swiper/css/pagination";
import "swiper/css/navigation";
import ProductCard from "@/components/ProductCard.vue";

const onSwiper = (swiper: SwiperType) => {
  console.log(swiper);
};

const onSlideChange = () => {
  console.log("slide change");
};

const modules = [Navigation, Pagination, Autoplay];
</script>

<template>
  <div class="relative">
    <AppHeader />
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
        @slideChange="onSlideChange"
        class="w-full h-screen"
        :modules="modules"
        :autoplay="{
          delay: 2500,
          disableOnInteraction: false,
        }"
        :navigation="true"
      >
        <swiper-slide>
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/April2025/1920x788(1)promax.jpg"
            alt=""
          />
        </swiper-slide>
        <swiper-slide>
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/May2025/T-SHIRT__POLO_THE_THAO_-_Desktop-1.jpg"
            alt=""
          />
        </swiper-slide>
        <swiper-slide>
          <img
            src="https://media3.coolmate.me/cdn-cgi/image/width=1920,quality=90,format=auto/uploads/May2025/T-SHIRT__POLO_THE_THAO_-_Desktopzz.jpg"
            alt=""
          />
        </swiper-slide>
      </swiper>
      <div class="w-primary mx-auto mb-8">
        <div class="flex gap-4">
          <span class="px-4 py-2 rounded-full bg-gray-200 font-bold text-xl"
            >Đồ nam</span
          >
          <span class="px-4 py-2 rounded-full bg-gray-200 font-bold text-xl"
            >Đồ nữ</span
          >
        </div>
        <div class="flex mt-4 gap-4">
          <div class="flex flex-col gap-4">
            <a href="" class="overflow-hidden rounded-2xl">
              <img
                src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Thun_Namz.jpg"
                alt=""
                class="w-[280px] hover:scale-105 transition-all duration-300 object-cover flex-1 !rounded-2xl"
              />
            </a>
            <div class="text-2xl font-bold text-center">Áo thun</div>
          </div>
          <div class="flex flex-col gap-4">
            <a href="" class="overflow-hidden rounded-2xl">
              <img
                src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Thun_Namz.jpg"
                alt=""
                class="w-[280px] hover:scale-105 transition-all duration-300 object-cover flex-1 !rounded-2xl"
              />
            </a>
            <div class="text-2xl font-bold text-center">Áo thun</div>
          </div>
          <div class="flex flex-col gap-4">
            <a href="" class="overflow-hidden rounded-2xl">
              <img
                src="https://media3.coolmate.me/cdn-cgi/image/quality=80,format=auto/uploads/May2025/Thun_Namz.jpg"
                alt=""
                class="w-[280px] hover:scale-105 transition-all duration-300 object-cover flex-1 !rounded-2xl"
              />
            </a>
            <div class="text-2xl font-bold text-center">Áo thun</div>
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
      <div class="w-primary mx-auto mb-8">
        <div>SẢN PHẨM MẶC HẰNG NGÀY</div>
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
        </div>
      </div>
    </div>
    <AppFooter />
  </div>
</template>
