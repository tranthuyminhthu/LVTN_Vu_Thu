<template>
    <h2 class="text-2xl font-bold">Ví voucher của tôi</h2>
    <div class="my-4 grid grid-cols-3 gap-4">
        <CouponCard />
        <CouponCard />
        <CouponCard />
        <CouponCard />
    </div>
    <div class="w-full relative">
        <img src="https://mcdn.coolmate.me/image/September2023/mceclip0_96.jpg" alt="" class="w-full object-cover ">
        <div class="absolute bottom-10 left-10">
            <p class="text-xl font-bold">Nhiều ưu đãi hấp dẫn</p>
            <p class="text-xl font-bold">đang chờ bạn</p>
            <button class="bg-black text-white px-4 py-2 rounded-full mt-4">Xem thêm</button>
        </div>
    </div>
</template>
<script setup lang="ts">
import CouponCard from '@/components/CouponCard.vue';


</script>
<style scoped>

</style>