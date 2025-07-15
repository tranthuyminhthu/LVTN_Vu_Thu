<template>
    <div class="layout">
      <!-- Header -->
      <AppHeader />
  
      <!-- Nội dung của page sẽ được render tại đây -->
      <main class="!h-[calc(100vh-60px)]">
        <router-view />
      </main>

    </div>
  </template>
  
  <script setup lang="ts">
  import AppHeader from "../components/AppHeader.vue";
  </script>
  
  <style scoped>
  .layout {
    flex-direction: column;
    overflow-y: hidden;
  }
  main {
    flex: 1;
    overflow-y: hidden;
  }
  </style>