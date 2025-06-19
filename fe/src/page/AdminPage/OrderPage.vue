<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <p class="text-2xl font-bold mb-4">Quản lý đơn hàng</p>
    
    <div class="flex gap-4 mb-4">
      <InputText v-model="searchQuery" placeholder="Tìm kiếm đơn hàng..." class="w-64" />
      <Select v-model="selectedStatus" :options="orderStatuses" optionLabel="name" placeholder="Trạng thái" class="w-48" />
      <Calendar v-model="dateRange" selectionMode="range" :showIcon="true" placeholder="Chọn ngày" class="w-64" />
    </div>

    <DataTable
      :value="orders"
      tableStyle="min-width: 50rem"
      class="bg-white rounded-lg"
      :paginator="true"
      :rows="10"
      :rowsPerPageOptions="[5, 10, 20, 50]"
      :loading="loading"
      :filters="filters"
      filterDisplay="menu"
    >
      <Column field="orderId" header="Mã đơn hàng" sortable></Column>
      <Column field="customerName" header="Khách hàng" sortable></Column>
      <Column field="orderDate" header="Ngày đặt" sortable>
        <template #body="slotProps">
          {{ formatDate(slotProps.data.orderDate) }}
        </template>
      </Column>
      <Column field="totalAmount" header="Tổng tiền" sortable>
        <template #body="slotProps">
          {{ formatPrice(slotProps.data.totalAmount) }}
        </template>
      </Column>
      <Column field="status" header="Trạng thái" sortable>
        <template #body="slotProps">
          <Tag :severity="getStatusSeverity(slotProps.data.status)" :value="slotProps.data.status" />
        </template>
      </Column>
      <Column field="paymentMethod" header="Phương thức thanh toán"></Column>
      <Column header="Thao tác">
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button icon="pi pi-eye" severity="info" @click="viewOrderDetails(slotProps.data)" />
            <Button 
              v-if="slotProps.data.status === 'Chờ xác nhận'" 
              icon="pi pi-check" 
              severity="success" 
              @click="updateOrderStatus(slotProps.data, 'Đã xác nhận')" 
            />
            <Button 
              v-if="slotProps.data.status === 'Đã xác nhận'" 
              icon="pi pi-truck" 
              severity="warning" 
              @click="updateOrderStatus(slotProps.data, 'Đang giao hàng')" 
            />
            <Button 
              v-if="slotProps.data.status === 'Đang giao hàng'" 
              icon="pi pi-check-circle" 
              severity="success" 
              @click="updateOrderStatus(slotProps.data, 'Hoàn thành')" 
            />
            <Button 
              v-if="['Chờ xác nhận', 'Đã xác nhận'].includes(slotProps.data.status)" 
              icon="pi pi-times" 
              severity="danger" 
              @click="updateOrderStatus(slotProps.data, 'Đã hủy')" 
            />
          </div>
        </template>
      </Column>
    </DataTable>

    <Dialog v-model:visible="orderDetailsVisible" modal header="Chi tiết đơn hàng" :style="{ width: '50vw' }">
      <div v-if="selectedOrder" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin khách hàng</p>
            <p>Tên: {{ selectedOrder.customerName }}</p>
            <p>Số điện thoại: {{ selectedOrder.customerPhone }}</p>
            <p>Địa chỉ: {{ selectedOrder.customerAddress }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin đơn hàng</p>
            <p>Mã đơn hàng: {{ selectedOrder.orderId }}</p>
            <p>Ngày đặt: {{ formatDate(selectedOrder.orderDate) }}</p>
            <p>Trạng thái: <Tag :severity="getStatusSeverity(selectedOrder.status)" :value="selectedOrder.status" /></p>
          </div>
        </div>
        <Divider />
        <p class="font-bold mb-2">Chi tiết sản phẩm</p>
        <DataTable :value="selectedOrder.items" class="mt-2">
          <Column field="name" header="Sản phẩm"></Column>
          <Column field="quantity" header="Số lượng"></Column>
          <Column field="price" header="Đơn giá">
            <template #body="slotProps">
              {{ formatPrice(slotProps.data.price) }}
            </template>
          </Column>
          <Column field="total" header="Thành tiền">
            <template #body="slotProps">
              {{ formatPrice(slotProps.data.price * slotProps.data.quantity) }}
            </template>
          </Column>
        </DataTable>
        <div class="flex justify-content-end mt-4">
          <p class="font-bold">Tổng tiền: {{ formatPrice(selectedOrder.totalAmount) }}</p>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import Calendar from 'primevue/calendar';
import Tag from 'primevue/tag';
import Dialog from 'primevue/dialog';
import Divider from 'primevue/divider';
import { useToast } from 'primevue/usetoast';

const toast = useToast();
const loading = ref(false);
const searchQuery = ref('');
const selectedStatus = ref(null);
const dateRange = ref(null);
const orderDetailsVisible = ref(false);
const selectedOrder = ref<Order | null>(null);
const filters = ref({});

interface OrderItem {
  name: string;
  quantity: number;
  price: number;
}

interface Order {
  orderId: string;
  customerName: string;
  customerPhone: string;
  customerAddress: string;
  orderDate: Date;
  totalAmount: number;
  status: string;
  paymentMethod: string;
  items: OrderItem[];
}

const orderStatuses = [
  { name: 'Chờ xác nhận' },
  { name: 'Đã xác nhận' },
  { name: 'Đang giao hàng' },
  { name: 'Hoàn thành' },
  { name: 'Đã hủy' }
];

const orders = ref<Order[]>([
  {
    orderId: 'ORD001',
    customerName: 'Nguyễn Văn A',
    customerPhone: '0123456789',
    customerAddress: '123 Đường ABC, Quận 1, TP.HCM',
    orderDate: new Date('2024-03-15'),
    totalAmount: 1500000,
    status: 'Chờ xác nhận',
    paymentMethod: 'Chuyển khoản',
    items: [
      { name: 'Áo thun nam', quantity: 2, price: 250000 },
      { name: 'Quần jean', quantity: 1, price: 1000000 }
    ]
  },
  {
    orderId: 'ORD002',
    customerName: 'Trần Thị B',
    customerPhone: '0987654321',
    customerAddress: '456 Đường XYZ, Quận 2, TP.HCM',
    orderDate: new Date('2024-03-14'),
    totalAmount: 2300000,
    status: 'Đang giao hàng',
    paymentMethod: 'Tiền mặt',
    items: [
      { name: 'Giày thể thao', quantity: 1, price: 1500000 },
      { name: 'Túi xách', quantity: 1, price: 800000 }
    ]
  }
]);

const formatDate = (date: Date) => {
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getStatusSeverity = (status: string): 'info' | 'success' | 'danger' | 'warning' | 'contrast' => {
  switch (status) {
    case 'Chờ xác nhận':
      return 'warning';
    case 'Đã xác nhận':
      return 'info';
    case 'Đang giao hàng':
      return 'contrast';
    case 'Hoàn thành':
      return 'success';
    case 'Đã hủy':
      return 'danger';
    default:
      return 'info';
  }
};

const viewOrderDetails = (order: Order) => {
  selectedOrder.value = order;
  orderDetailsVisible.value = true;
};

const updateOrderStatus = (order: Order, newStatus: string) => {
  // TODO: Implement API call to update order status
  const index = orders.value.findIndex(o => o.orderId === order.orderId);
  if (index !== -1) {
    orders.value[index].status = newStatus;
    toast.add({
      severity: 'success',
      summary: 'Cập nhật thành công',
      detail: `Đơn hàng ${order.orderId} đã được cập nhật trạng thái thành ${newStatus}`,
      life: 3000
    });
  }
};

onMounted(() => {
  // TODO: Fetch orders from API
});
</script>

<style scoped>
.p-datatable .p-datatable-tbody > tr > td {
  padding: 1rem;
}
</style>