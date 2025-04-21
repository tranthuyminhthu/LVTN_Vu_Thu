<!-- Component cha -->
<script setup lang="ts">
import {
  Accordion,
  AccordionContent,
  AccordionHeader,
  AccordionPanel,
  Avatar,
  Badge,
  Button,
} from 'primevue'
import SectionContainer from './SectionContainer.vue'
</script>

<template>
  <SectionContainer>
    <p class="text-center font-bold text-xl text-[#8c8c8c] uppercase">the books</p>
    <p class="text-center font-bold text-2xl my-3">
      Hành trình của tri thức cùng hệ thống
      <span class="text-[#ec9035] font-bold">tài nguyên học tập </span>
      <span class="text-[#9bca37] font-bold">đa dạng</span>
    </p>
    <p class="text-lg font-medium text-center my-4">
      Contextualzied Learning giúp giải thích kiến thức rõ ràng và dễ hiểu, tài liệu học tập đa
      dạng, ngân hàng câu hỏi mở rộng và thiết kế bài học tinh tế - ZIM đã tổng kết và chia sẻ kiến
      thức và hiểu biết qua hệ thống sách ZIM BOOKS.
    </p>
    <Button
      label="Khám phá các đầu sách mới nhất"
      icon="pi pi-arrow-right"
      iconPos="right"
      class="!rounded-2xl shadow-sm text-sm font-bold my-2 fit-content mx-auto"
    />
    <div class="mt-16 relative" style="min-height: 300px">
      <div
        class="animation-fly z-1 w-[142px] absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 inline-block"
      >
        <img
          src="https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fbook-1.5b39ebb9.webp&w=1080&q=75"
          alt=""
        />
      </div>
      <div
        class="animation-fly-side scale-[0.8] rotate-[15deg] w-[142px] absolute top-1/2 -translate-y-1/2 left-1/2 inline-block"
      >
        <img
          src="https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fbook-1.5b39ebb9.webp&w=1080&q=75"
          alt=""
        />
      </div>
      <div
        class="animation-fly-side scale-[0.8] rotate-[-15deg] w-[142px] absolute top-1/2 -translate-y-1/2 translate-x-1/2 inline-block"
      >
        <img
          src="https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fbook-1.5b39ebb9.webp&w=1080&q=75"
          alt=""
        />
      </div>
    </div>
  </SectionContainer>
</template>

<style scoped>
.animation-fly {
  animation: fly 3s ease-in-out infinite;
}

.animation-fly-side {
  animation: fly 4s ease-in-out infinite;
}

@keyframes fly {
  0% {
    transform: translateY(0); /* Vị trí ban đầu */
  }
  50% {
    transform: translateY(-20px); /* Bay lên 20px */
  }
  100% {
    transform: translateY(0); /* Trở về vị trí ban đầu */
  }
}
</style>
