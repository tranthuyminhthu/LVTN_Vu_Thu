<script setup lang="ts">
import OverlayBadge from 'primevue/overlaybadge'
</script>

<template>
  <header class="z-1 fixed w-full shadow-1 top-0 bg-white">
    <div class="px-4 mx-auto flex justify-content-between py-2 align-content-center">
      <span>
        <svg
          width="40"
          height="40"
          viewBox="0 0 32 32"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
          class="cursor-pointer"
        >
          <path fill="#fff" d="M7 6h19v21H7z"></path>
          <path
            d="M16 32C7.165 32 0 24.84 0 16S7.165 0 16 0s16 7.16 16 16-7.165 16-16 16Zm5.235-24.43h-7.676s-2.067-.029-2.657 1.983L8.18 21.02s-.598 2.952 2.592 3.425h9.556s.482.028.655-.516l2.873-12.137s.094-.706-.655-.805c0 0-.49 0-.655.501L19.91 22.63s-.122.509-.655.509h-8.353s-.972.07-1.13-.982c0 0-.137-1.01 1.058-1.137h7.546s.533 0 .655-.522l2.86-12.11s.093-.734-.656-.812"
            fill="#DA1E39"
          ></path>
        </svg>
      </span>
      <div class="flex items-center gap-4 align-content-center">
        <div class="flex align-items-center justify-content-center">
          <i class="pi pi-search" style="font-size: 1.25rem" />
        </div>

        <div class="flex align-items-center justify-content-center">
          <OverlayBadge severity="danger">
            <i class="pi pi-shopping-cart" style="font-size: 1.25rem" />
          </OverlayBadge>
        </div>

        <div class="flex align-items-center justify-content-center">
          <i class="pi pi-align-justify" style="font-size: 1.25rem" />
        </div>
      </div>
    </div>
  </header>
</template>
