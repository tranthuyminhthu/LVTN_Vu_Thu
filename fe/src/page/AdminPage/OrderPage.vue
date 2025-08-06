<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <Toast />
    <p class="text-2xl font-bold mb-4">Quản lý đơn hàng</p>
    
    <div class="flex gap-4 mb-4">
      <InputText v-model="searchQuery" placeholder="Tìm kiếm đơn hàng..." class="w-64" />
      <Select v-model="selectedStatus" :options="orderStatuses" optionLabel="label" optionValue="value" placeholder="Trạng thái" class="w-48" />
      <Calendar v-model="dateRange" selectionMode="range" :showIcon="true" placeholder="Chọn ngày" class="w-64" />
      <Button
        label="Làm mới"
        icon="pi pi-refresh"
        severity="secondary"
        outlined
        @click="fetchOrders"
      />
    </div>

    <DataTable
      :value="orders"
      tableStyle="min-width: 50rem"
      class="bg-white rounded-lg"
      :paginator="true"
      :rows="rowsPerPage"
      :rowsPerPageOptions="[5, 10, 20, 50]"
      :loading="loading"
      :totalRecords="totalElements"
      :first="page * rowsPerPage"
      :lazy="true"
      @page="onPage"
    >
      <Column field="orderId" header="Mã đơn hàng" sortable></Column>
      <Column field="receiverName" header="Khách hàng" sortable>
        <template #body="slotProps">
          <div>
            <div class="font-medium">{{ slotProps.data.receiverName }}</div>
            <div class="text-sm text-gray-500">{{ slotProps.data.receiverPhone }}</div>
          </div>
        </template>
      </Column>
      <Column field="createdAt" header="Ngày đặt" sortable>
        <template #body="slotProps">
          {{ formatDate(slotProps.data.createdAt) }}
        </template>
      </Column>
      <Column field="totalAmount" header="Tổng tiền" sortable>
        <template #body="slotProps">
          {{ formatPrice(slotProps.data.totalAmount || 0) }}
        </template>
      </Column>
      <Column field="status" header="Trạng thái" sortable>
        <template #body="slotProps">
          <Tag :severity="getStatusSeverity(slotProps.data.status)" :value="getStatusLabel(slotProps.data.status)" />
        </template>
      </Column>
      <Column field="paymentStatus" header="Trạng thái thanh toán">
        <template #body="slotProps">
          <Tag :severity="getPaymentStatusSeverity(slotProps.data.paymentStatus)" :value="getPaymentStatusLabel(slotProps.data.paymentStatus)" />
        </template>
      </Column>
      <Column field="paymentMethod" header="Phương thức thanh toán">
        <template #body="slotProps">
          <Tag :severity="getPaymentMethodSeverity(slotProps.data.paymentMethod)" :value="getPaymentMethodLabel(slotProps.data.paymentMethod)" />
        </template>
      </Column>
      <Column header="Thao tác">
        <template #body="slotProps">
          <div class="flex gap-2">
            <Button icon="pi pi-eye" severity="info" @click="viewOrderDetails(slotProps.data)" />
          </div>
        </template>
      </Column>
    </DataTable>

    <Dialog v-model:visible="orderDetailsVisible" modal header="Chi tiết đơn hàng" :style="{ width: '80vw' }">
      <LoadingGlobal :isLoading="loadingDetail">
        <div v-if="selectedOrder" class="p-4 space-y-4">
          <!-- Order Information -->
          <div class="grid grid-cols-2 gap-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-bold mb-2">Thông tin đơn hàng</h4>
              <div class="space-y-2">
                <div>
                  <span class="text-gray-600">Mã đơn hàng:</span>
                  <span class="font-medium ml-2">#{{ selectedOrder.orderId }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Ngày đặt:</span>
                  <span class="font-medium ml-2">{{ formatDate(selectedOrder.createdAt) }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Trạng thái:</span>
                  <Tag :severity="getStatusSeverity(selectedOrder.status)" :value="getStatusLabel(selectedOrder.status)" class="ml-2" />
                </div>
                <div>
                  <span class="text-gray-600">Tổng tiền:</span>
                  <span class="font-medium ml-2">{{ formatPrice(selectedOrder.totalAmount || 0) }}</span>
                </div>
              </div>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <h4 class="font-bold mb-2">Thông tin khách hàng</h4>
              <div class="space-y-2">
                <div>
                  <span class="text-gray-600">Tên:</span>
                  <span class="font-medium ml-2">{{ selectedOrder.receiverName }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Số điện thoại:</span>
                  <span class="font-medium ml-2">{{ selectedOrder.receiverPhone }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Email:</span>
                  <span class="font-medium ml-2">{{ selectedOrder.receiverEmail }}</span>
                </div>
                <div>
                  <span class="text-gray-600">Địa chỉ:</span>
                  <span class="font-medium ml-2">{{ selectedOrder.note || "---" }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Payment Information -->
          <div class="bg-gray-50 p-4 rounded-lg">
            <h4 class="font-bold mb-2">Thông tin thanh toán</h4>
            <div class="grid grid-cols-3 gap-4">
              <div>
                <span class="text-gray-600">Phương thức:</span>
                <Tag :severity="getPaymentMethodSeverity(selectedOrder.paymentMethod)" :value="getPaymentMethodLabel(selectedOrder.paymentMethod)" class="ml-2" />
              </div>
              <div>
                <span class="text-gray-600">Trạng thái:</span>
                <Tag :severity="getPaymentStatusSeverity(selectedOrder.paymentStatus)" :value="getPaymentStatusLabel(selectedOrder.paymentStatus)" class="ml-2" />
              </div>
              <div>
                <span class="text-gray-600">Mã giao dịch:</span>
                <span class="font-medium ml-2">{{ selectedOrder.paymentTransactionId || "---" }}</span>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div>
            <h4 class="font-bold mb-2">Chi tiết sản phẩm</h4>
            <div class="border rounded-lg">
              <div v-if="selectedOrder.items && selectedOrder.items.length > 0">
                <div
                  v-for="item in selectedOrder.items"
                  :key="item.productSku"
                  class="flex items-center p-4 border-b last:border-b-0"
                >
                  <div class="w-16 h-16 flex items-center justify-center bg-gray-100 rounded-lg mr-4">
                    <img
                      :src="item.image && item.image !== 'a' ? item.image : 'https://via.placeholder.com/80x80?text=No+Image'"
                      alt="Ảnh sản phẩm"
                      class="w-14 h-14 object-cover rounded"
                    />
                  </div>
                  <div class="flex-1">
                    <h5 class="font-medium">{{ item.productName }}</h5>
                    <p class="text-sm text-gray-500">SKU: {{ item.productSku }}</p>
                  </div>
                  <div class="text-right">
                    <p class="font-medium">{{ formatPrice(item.price) }}</p>
                    <p class="text-sm text-gray-500">Số lượng: {{ item.quantity }}</p>
                    <p class="text-sm text-gray-500">Tổng: {{ formatPrice(item.price * item.quantity) }}</p>
                  </div>
                </div>
              </div>
              <div v-else class="p-4 text-gray-500 text-center">Không có sản phẩm</div>
            </div>
          </div>
        </div>
      </LoadingGlobal>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import Calendar from 'primevue/calendar';
import Tag from 'primevue/tag';
import Dialog from 'primevue/dialog';
import { useToast } from 'primevue/usetoast';
import Toast from 'primevue/toast';
import LoadingGlobal from '@/components/LoadingGlobal.vue';
import type { Order } from '@/types';
import { getAdminOrders, getOrderDetail } from '@/api/order';

const toast = useToast();
const loading = ref(false);
const loadingDetail = ref(false);
const searchQuery = ref('');
const selectedStatus = ref(null);
const dateRange = ref(null);
const orderDetailsVisible = ref(false);
const selectedOrder = ref<Order | null>(null);

const page = ref(0);
const rowsPerPage = ref(10);
const totalElements = ref(0);
const orders = ref<Order[]>([]);

const orderStatuses = [
  { value: null, label: "Tất cả" },
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "PROCESSING", label: "Đang xử lý" },
  { value: "DELIVERING", label: "Đang giao hàng" },
  { value: "SHIPPED", label: "Đã gửi hàng" },
  { value: "DELIVERED", label: "Đã giao hàng" },
  { value: "CANCELLED", label: "Đã hủy" },
  { value: "REFUNDED", label: "Đã hoàn trả" },
  { value: "RECEIVED", label: "Đã nhận hàng" },
];

const fetchOrders = async () => {
  loading.value = true;
  try {
    // Nếu không có status được chọn, gọi API không kèm status để lấy tất cả đơn hàng
    const res = await getAdminOrders(
      page.value,
      rowsPerPage.value,
      selectedStatus.value || undefined
    );
    orders.value = res.orders;
    totalElements.value = res.totalElements;
  } catch (error) {
    console.error('Error fetching orders:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách đơn hàng',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

const viewOrderDetails = async (order: Order) => {
  loadingDetail.value = true;
  try {
    const detail = await getOrderDetail(order.orderId);
    selectedOrder.value = detail;
    orderDetailsVisible.value = true;
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải chi tiết đơn hàng',
      life: 3000
    });
  } finally {
    loadingDetail.value = false;
  }
};

// PrimeVue DataTable page event type
interface DataTablePageEvent {
  page: number;
  first: number;
  rows: number;
  pageCount: number;
}

const onPage = (event: DataTablePageEvent) => {
  page.value = event.page;
  rowsPerPage.value = event.rows;
  fetchOrders();
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleString("vi-VN");
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PENDING: "Chờ xử lý",
    PROCESSING: "Đang xử lý",
    DELIVERING: "Đang giao hàng",
    SHIPPED: "Đã gửi hàng",
    DELIVERED: "Đã giao hàng",
    CANCELLED: "Đã hủy",
    REFUNDED: "Đã hoàn trả",
    RECEIVED: "Đã nhận hàng",
  };
  return labels[status] || status;
};

const getStatusSeverity = (status: string): 'info' | 'success' | 'danger' | 'warning' | 'contrast' => {
  switch (status) {
    case 'PENDING':
      return 'warning';
    case 'PROCESSING':
      return 'info';
    case 'DELIVERING':
      return 'contrast';
    case 'SHIPPED':
      return 'success';
    case 'DELIVERED':
      return 'success';
    case 'RECEIVED':
      return 'success';
    case 'CANCELLED':
      return 'danger';
    case 'REFUNDED':
      return 'danger';
    default:
      return 'info';
  }
};

const getPaymentStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    UNPAID: "Chưa thanh toán",
    PAID: "Đã thanh toán",
    REFUNDED: "Đã hoàn trả",
  };
  return labels[status] || status;
};

const getPaymentStatusSeverity = (status: string): 'info' | 'success' | 'danger' | 'warning' | 'contrast' => {
  switch (status) {
    case 'UNPAID':
      return 'warning';
    case 'PAID':
      return 'success';
    case 'REFUNDED':
      return 'danger';
    default:
      return 'info';
  }
};

const getPaymentMethodLabel = (method: string) => {
  const labels: Record<string, string> = {
    CASH: "Tiền mặt",
    BANK_TRANSFER: "Chuyển khoản",
    ONLINE_PAYMENT: "Thanh toán online",
    VNPAY: "VNPay",
  };
  return labels[method] || method;
};

const getPaymentMethodSeverity = (method: string): 'info' | 'success' | 'danger' | 'warning' | 'contrast' => {
  switch (method) {
    case 'CASH':
      return 'info';
    case 'BANK_TRANSFER':
      return 'contrast';
    case 'ONLINE_PAYMENT':
      return 'success';
    case 'VNPAY':
      return 'warning';
    default:
      return 'info';
  }
};

onMounted(() => {
  fetchOrders();
});

watch([selectedStatus, searchQuery], () => {
  page.value = 0;
  fetchOrders();
});
</script>

<style scoped>
.p-datatable .p-datatable-tbody > tr > td {
  padding: 1rem;
}
</style>