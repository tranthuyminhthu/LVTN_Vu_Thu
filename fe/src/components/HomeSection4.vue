<!-- Component cha -->
<script setup lang="ts">
import { Carousel } from 'primevue'
import HomeCard from './HomeCard.vue'
import SectionContainer from './SectionContainer.vue'
import { onMounted, ref } from 'vue'
const responsiveOptions = ref([
  {
    breakpoint: '1400px',
    numVisible: 2,
    numScroll: 1,
  },
  {
    breakpoint: '1199px',
    numVisible: 3,
    numScroll: 1,
  },
  {
    breakpoint: '767px',
    numVisible: 2,
    numScroll: 1,
  },
  {
    breakpoint: '575px',
    numVisible: 1,
    numScroll: 1,
  },
])
const products = ref()
onMounted(() => {
  products.value = [
    {
      img: 'https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fcourse-ielts.4a99b1bb.webp&w=1920&q=75',
      title: 'Luyện thi IELTS',
      description:
        'Khóa học IELTS từ cơ bản đến nâng cao. IELTS 2 kỹ năng và IELTS 4 kỹ năng. Hệ thống kiến thức tiếng Anh bài bản.',
    },
    {
      img: 'https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fcourse-ielts.4a99b1bb.webp&w=1920&q=75',
      title: 'Luyện thi IELTS',
      description:
        'Khóa học IELTS từ cơ bản đến nâng cao. IELTS 2 kỹ năng và IELTS 4 kỹ năng. Hệ thống kiến thức tiếng Anh bài bản.',
    },
    {
      img: 'https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fcourse-ielts.4a99b1bb.webp&w=1920&q=75',
      title: 'Luyện thi IELTS',
      description:
        'Khóa học IELTS từ cơ bản đến nâng cao. IELTS 2 kỹ năng và IELTS 4 kỹ năng. Hệ thống kiến thức tiếng Anh bài bản.',
    },
    {
      img: 'https://zim.vn/_next/image?url=%2F_next%2Fstatic%2Fmedia%2Fcourse-ielts.4a99b1bb.webp&w=1920&q=75',
      title: 'Luyện thi IELTS',
      description: 'Khóa học IELTS từ cơ bản đến nâng cao. IELTS 2 kỹ năng và IELTS 4 kỹ năng',
    },
  ]
})
</script>

<template>
  <SectionContainer>
    <p class="text-center font-bold text-xl text-[#8c8c8c] uppercase">Diverse Learning Content</p>
    <p class="text-center font-bold text-2xl my-3">
      <span class="text-[#b12f86] font-bold">Đa dạng</span> chương trình học
    </p>
    <p class="text-lg font-medium text-center">
      Các chương trình tiếng Anh của ZIM được chia thành hai loại chính:
      <strong> Luyện thi</strong> (IELTS, SAT, TOEIC...) , bao gồm tiếng Anh giao tiếp và tiếng Anh
      chuyên ngành. Mỗi chương trình được thiết kế để giúp học viên sử dụng tiếng Anh chính xác và
      tự nhiên.
    </p>
    <Carousel
      :value="products"
      :numVisible="3"
      :numScroll="3"
      :responsiveOptions="responsiveOptions"
      containerClass="my-4"
    >
      <template #item="slotProps">
        <div class="mx-2">
          <HomeCard/>
        </div>
      </template>
    </Carousel>
  </SectionContainer>
</template>
