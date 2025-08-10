<template>
  <div>
    <p class="text-2xl font-bold">Lịch sử đơn hàng</p>
    <p class="text-sm text-gray-500 mt-2">Đơn hàng của bạn: {{ totalElements }} đơn hàng</p>
    <div class="!grid grid-cols-1 gap-4 mt-4">
      <OrderCard
        v-for="order in ordersAll"
        :key="order.orderId"
        :order="order"
        @update:order="fetchOrders"
      />
    </div>
    <Paginator
      :rows="rowsPerPage"
      :totalRecords="totalElements"
      :first="page * rowsPerPage"
      @page="onPage"
      :lazy="true"
      :rowsPerPageOptions="[5, 10, 20, 50]"
      class="mt-6"
    />
    <ConfirmDialog />
    <Toast />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import OrderCard from '@/components/OrderCard.vue';
import Paginator from 'primevue/paginator';
import ConfirmDialog from 'primevue/confirmdialog';
import Toast from 'primevue/toast';
import { getMyOrders } from '@/api/order';
import type { Order } from '@/types';

const ordersAll = ref<Order[]>([]);
const loading = ref(false);
const page = ref(0);
const rowsPerPage = ref(10);
const totalElements = ref(0);

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await getMyOrders(page.value, rowsPerPage.value);
    ordersAll.value = res.orders;
    totalElements.value = res.totalElements;
  } catch (e) {
    // handle error
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchOrders();
});

const onPage = (event: { page: number; first: number; rows: number }) => {
  page.value = event.page;
  rowsPerPage.value = event.rows;
  fetchOrders();
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('vi-VN');
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};
</script>

<style scoped>

</style>
