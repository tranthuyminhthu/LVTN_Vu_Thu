<template>
  <BlockUI :blocked="isLoading">
    <template #default>
      <div
        class="absolute inset-0 flex flex-col items-center justify-center"
        v-if="isLoading"
      >
        <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i>
      </div>
      <slot />
    </template>
  </BlockUI>
</template>

<script setup lang="ts">
import { defineProps } from "vue";
import BlockUI from "primevue/blockui";

defineProps<{
  isLoading: boolean;
}>();
</script>

<style scoped>
.spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style> 