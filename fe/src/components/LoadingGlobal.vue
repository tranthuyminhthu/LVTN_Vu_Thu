<template>
  <div
    v-if="isLoading"
    class="fixed inset-0 bg-black/30 backdrop-blur-sm flex items-center justify-center z-50"
  >
    <div class="bg-white p-8 rounded-2xl shadow-2xl text-center">
      <div class="flex flex-col items-center gap-6">
        <!-- Spinner -->
        <div class="relative w-16 h-16">
            <i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i>
        </div>

        <!-- Loading Text -->
        <p class="text-gray-800 font-medium">{{ message }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useLoading } from '@/composables/useLoading';

const { isLoading, message } = useLoading();
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