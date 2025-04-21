<script setup lang="ts">
import AppHeader from '../components/AppHeader.vue'
import AppBanner from '../components/AppBanner.vue'
import HomeSection2 from '@/components/HomeSection2.vue';
import HomeSection3 from '@/components/HomeSection3.vue';
import HomeSection4 from '@/components/HomeSection4.vue';
import HomeSection5 from '@/components/HomeSection5.vue';
import HomeSection6 from '@/components/HomeSection6.vue';
import HomeSection7 from '@/components/HomeSection7.vue';
import AppFooter from '@/components/AppFooter.vue';
</script>

<template>
  <div class="relative">
    <AppHeader />
    <div class="mt-7">
      <AppBanner />
      <HomeSection2 />
      <HomeSection3 />
      <HomeSection4 />
      <HomeSection5 />
      <HomeSection6 />
      <HomeSection7 />
    </div>
    <AppFooter/>
  </div>
</template>
