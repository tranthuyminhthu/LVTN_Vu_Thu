<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import ChatPopup from './components/ChatPopup.vue';
import { ref, provide } from 'vue';
import LoadingGlobal from '@/components/LoadingGlobal.vue';
import { useLoading } from '@/composables/useLoading';

const loadingRef = ref<InstanceType<typeof LoadingGlobal> | null>(null);
const { show: showLoading, hide: hideLoading } = useLoading();

// Provide loading methods to all components
provide('showLoading', showLoading);
provide('hideLoading', hideLoading);
</script>

<template>
  <div class="min-h-screen admin-bg">
    <!-- <ChatPopup /> -->
    <RouterView />
  </div>
</template>

<style>
@media (min-width: 1024px) {
  .about {
    min-height: 100vh;
    display: flex;
    align-items: center;
  }

  .bg-about-banner {
    background-image:
      linear-gradient(#d4d6ff 1.9000000000000001px, transparent 0),
      linear-gradient(90deg, #d4d6ff 1.9000000000000001px, #fbfcff 0);
  }

  .bg-pattern {
    background-size: 50px 50px;
  }

  .opacity-30 {
    opacity: 0.3;
  }

  .skew-x-12 {
    --tw-skew-x: skewX(-30deg);
    transform: var(--tw-rotate-x) var(--tw-rotate-y) var(--tw-rotate-z) var(--tw-skew-x)
      var(--tw-skew-y);
  }
  .rotate-3 {
    rotate: 30deg;
  }
  @property --tw-rotate-x {
    syntax: '*';
    inherits: false;
    initial-value: rotateX(0);
  }
  @property --tw-rotate-y {
    syntax: '*';
    inherits: false;
    initial-value: rotateY(0);
  }
  @property --tw-rotate-z {
    syntax: '*';
    inherits: false;
    initial-value: rotateZ(0);
  }
  @property --tw-skew-x {
    syntax: '*';
    inherits: false;
    initial-value: skewX(0);
  }
  @property --tw-skew-y {
    syntax: '*';
    inherits: false;
    initial-value: skewY(0);
  }
  
}
/* body, html, div {
     scrollbar-width: none;
     -ms-overflow-style: none;
   }
   body::-webkit-scrollbar, html::-webkit-scrollbar {
     display: none;
   } */
</style>
