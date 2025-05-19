<template>
    <div class="layout">
      <!-- Header -->
      <AppHeader title="Product List" />
  
      <!-- Nội dung của page sẽ được render tại đây -->
      <main class="!mt-header w-primary mx-auto pt-12">
        <router-view />
      </main>
  
      <!-- Footer -->
      <footer>
        <p>© 2025 My App</p>
      </footer>
    </div>
  </template>
  
  <script setup lang="ts">
  import AppHeader from "../components/AppHeader.vue";
  </script>
  
  <style scoped>
  .layout {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }
  main {
    flex: 1;
    padding: 2rem;
  }
  footer {
    background: #f8f9fa;
    padding: 1rem;
    text-align: center;
  }
  </style>