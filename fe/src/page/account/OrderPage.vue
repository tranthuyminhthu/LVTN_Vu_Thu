<template>
    <div>
        <p class="text-2xl font-bold">Lịch sử đơn hàng</p>
        <p class="text-sm text-gray-500 mt-2">Đơn hàng của bạn: 1 đơn hàng</p>
        <div class="!grid grid-cols-1 gap-4 mt-4">
            <OrderCard />
            <OrderCard />
        </div>
    </div>
</template>

<script setup lang="ts">
import OrderCard from '@/components/OrderCard.vue';


</script>

<style scoped>

</style>
