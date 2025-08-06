<template>
  <div class="p-6 rounded-lg">
    <Toast />
    <h1 class="text-2xl font-semibold text-white mb-6">Thông báo</h1>
    
    <!-- Filter Section -->
    <div class="bg-white rounded-lg shadow-sm p-4 mb-6">
      <div class="flex flex-wrap gap-4 items-center">
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium text-gray-700">Trạng thái:</label>
          <Select
            v-model="selectedStatus"
            :options="notificationStatuses"
            optionLabel="label"
            optionValue="value"
            placeholder="Tất cả trạng thái"
            class="w-40"
          />
        </div>
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium text-gray-700">Loại thông báo:</label>
          <Select
            v-model="selectedType"
            :options="notificationTypes"
            optionLabel="label"
            optionValue="value"
            placeholder="Tất cả loại"
            class="w-40"
          />
        </div>
        <div class="flex items-center gap-2">
          <label class="text-sm font-medium text-gray-700">Danh mục:</label>
          <Select
            v-model="selectedCategory"
            :options="notificationCategories"
            optionLabel="label"
            optionValue="value"
            placeholder="Tất cả danh mục"
            class="w-40"
          />
        </div>
        <Button
          label="Làm mới"
          icon="pi pi-refresh"
          severity="secondary"
          outlined
          @click="fetchNotifications"
        />
      </div>
    </div>

    <!-- Notifications List -->
    <div class="bg-white rounded-lg shadow-sm">
      <DataTable
        :value="notifications"
        class="w-full"
        :pt="{ table: { class: 'min-w-full' } }"
        :rows="rowsPerPage"
        paginator
        :rowsPerPageOptions="[10, 20, 50]"
        :totalRecords="totalElements"
        :first="page * rowsPerPage"
        :loading="loading"
        :lazy="true"
        @page="onPage"
      >
        <Column
          field="type"
          header="Loại thông báo"
          body-class="whitespace-nowrap"
        >
          <template #body="slotProps">
            <div class="flex items-center gap-2">
              <i :class="getNotificationIcon(slotProps.data.type)" class="text-lg"></i>
              <span class="font-medium">{{ getNotificationTypeLabel(slotProps.data.type) }}</span>
            </div>
          </template>
        </Column>
        
        <Column
          field="title"
          header="Tiêu đề"
          body-class="whitespace-nowrap"
        >
          <template #body="slotProps">
            <div class="font-medium text-gray-900">{{ slotProps.data.title }}</div>
            <div class="text-sm text-gray-500">{{ slotProps.data.content }}</div>
          </template>
        </Column>
        
        <Column
          field="category"
          header="Danh mục"
          body-class="whitespace-nowrap"
        >
          <template #body="slotProps">
            <span class="font-medium text-gray-700">{{ getCategoryLabel(slotProps.data.category) }}</span>
          </template>
        </Column>
        
        <Column
          field="status"
          header="Trạng thái"
          body-class="whitespace-nowrap"
        >
          <template #body="slotProps">
            <span
              :class="[
                'px-2 py-1 text-xs font-medium rounded-full',
                getNotificationStatusClass(slotProps.data.status),
              ]"
            >
              {{ getNotificationStatusLabel(slotProps.data.status) }}
            </span>
          </template>
        </Column>
        
        <Column
          field="createdAt"
          header="Thời gian"
          body-class="whitespace-nowrap"
        >
          <template #body="slotProps">
            <div class="text-sm text-gray-900">
              {{ formatDate(slotProps.data.createdAt) }}
            </div>
            <div v-if="slotProps.data.readAt" class="text-xs text-gray-500">
              Đã đọc: {{ formatDate(slotProps.data.readAt) }}
            </div>
          </template>
        </Column>
        
        <Column
          header="Thao tác"
          body-class="whitespace-nowrap text-sm font-medium"
        >
          <template #body="slotProps">
            <div class="flex items-center justify-center gap-2">
              <button
                v-if="slotProps.data.status === 'SENT' && !slotProps.data.readAt"
                @click="markAsRead(slotProps.data)"
                class="text-blue-600 hover:text-blue-900 p-1 rounded hover:bg-blue-50"
                title="Đánh dấu đã đọc"
              >
                <i class="pi pi-check"></i>
              </button>
              <button
                v-if="hasOrderData(slotProps.data)"
                @click="viewOrderDetails(slotProps.data)"
                class="text-green-600 hover:text-green-900 p-1 rounded hover:bg-green-50"
                title="Xem đơn hàng"
              >
                <i class="pi pi-eye"></i>
              </button>
              <button
                @click="deleteNotification(slotProps.data)"
                class="text-red-600 hover:text-red-900 p-1 rounded hover:bg-red-50"
                title="Xóa thông báo"
              >
                <i class="pi pi-trash"></i>
              </button>
            </div>
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- Order Details Dialog -->
    <Dialog
      v-model:visible="showOrderModal"
      :modal="true"
      :closable="true"
      :style="{ width: '800px' }"
      header="Chi tiết đơn hàng"
    >
      <LoadingGlobal :isLoading="loadingOrderDetail">
        <div v-if="selectedOrder" class="space-y-4">
          <!-- Order Information -->
          <div class="shadow border border-gray-200 p-4 rounded-lg">
            <h4 class="font-medium mb-2">Thông tin đơn hàng</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-600">Mã đơn hàng</p>
                <p class="font-medium">#{{ selectedOrder.orderId }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Trạng thái</p>
                <p class="font-medium">{{ getStatusLabel(selectedOrder.status) }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Tổng tiền</p>
                <p class="font-medium">{{ formatPrice(selectedOrder.totalAmount) }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Ngày đặt</p>
                <p class="font-medium">{{ formatDate(selectedOrder.createdAt) }}</p>
              </div>
            </div>
          </div>

          <!-- Customer Information -->
          <div class="shadow border border-gray-200 p-4 rounded-lg">
            <h4 class="font-medium mb-2">Thông tin khách hàng</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-600">Tên khách hàng</p>
                <p class="font-medium">{{ selectedOrder.receiverName }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Số điện thoại</p>
                <p class="font-medium">{{ selectedOrder.receiverPhone }}</p>
              </div>
              <div class="col-span-2">
                <p class="text-sm text-gray-600">Địa chỉ giao hàng</p>
                <p class="font-medium">{{ selectedOrder.note || "---" }}</p>
              </div>
            </div>
          </div>

          <!-- Order Items -->
          <div>
            <h4 class="font-medium mb-2">Sản phẩm</h4>
            <div class="border rounded-lg border-gray-200 shadow">
              <div v-if="selectedOrder.items && selectedOrder.items.length > 0">
                <div
                  v-for="item in selectedOrder.items"
                  :key="item.productSku"
                  class="flex items-center p-4 border-b last:border-b-0 border-gray-200"
                >
                  <div class="w-16 h-16 flex items-center justify-center bg-gray-100 rounded-lg mr-4">
                    <Image
                      :src="item.image && item.image !== 'a' ? item.image : 'https://via.placeholder.com/80x80?text=No+Image'"
                      alt="Ảnh sản phẩm"
                      class="w-14 h-14 object-cover rounded"
                      preview
                    />
                  </div>
                  <div class="ml-0 flex-1">
                    <h5 class="font-medium">{{ item.productName }}</h5>
                    <p class="text-sm text-gray-500">Số lượng: {{ item.quantity }}</p>
                  </div>
                  <div class="text-right">
                    <p class="font-medium">{{ formatPrice(item.price) }}</p>
                    <p class="text-sm text-gray-500">
                      Tổng: {{ formatPrice(item.price * item.quantity) }}
                    </p>
                  </div>
                </div>
              </div>
              <div v-else class="p-4 text-gray-500">Không có sản phẩm</div>
            </div>
          </div>
        </div>
      </LoadingGlobal>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import Dialog from "primevue/dialog";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Select from "primevue/select";
import Button from "primevue/button";
import Image from "primevue/image";
import { useToast } from "primevue/usetoast";
import Toast from "primevue/toast";
import LoadingGlobal from "@/components/LoadingGlobal.vue";
import type { Order } from "@/types";
import type { Notification } from "@/api/notification";
import { getNotifications, markNotificationAsRead, deleteNotification as deleteNotificationAPI } from "@/api/notification";
import { getOrderDetail } from "@/api/order";

const notifications = ref<Notification[]>([]);
const loading = ref(false);
const loadingOrderDetail = ref(false);
const selectedStatus = ref<string | null>(null);
const selectedType = ref<string | null>(null);
const selectedCategory = ref<string | null>(null);
const showOrderModal = ref(false);
const selectedOrder = ref<Order | null>(null);

const page = ref(0);
const rowsPerPage = ref(10);
const totalElements = ref(0);

const toast = useToast();

const notificationStatuses = [
  { value: null, label: "Tất cả" },
  { value: "DRAFT", label: "Nháp" },
  { value: "SCHEDULED", label: "Đã lên lịch" },
  { value: "SENT", label: "Đã gửi" },
  { value: "FAILED", label: "Thất bại" },
  { value: "READ", label: "Đã đọc" },
];

const notificationTypes = [
  { value: null, label: "Tất cả" },
  { value: "PUSH", label: "Push Notification" },
  { value: "EMAIL", label: "Email" },
  { value: "SMS", label: "SMS" },
];

const notificationCategories = [
  { value: null, label: "Tất cả" },
  { value: "ORDER", label: "Đơn hàng" },
  { value: "VENDOR_PRODUCT_APPROVAL", label: "Phê duyệt sản phẩm" },
  { value: "PAYMENT", label: "Thanh toán" },
  { value: "SYSTEM", label: "Hệ thống" },
];

const fetchNotifications = async () => {
  loading.value = true;
  try {
    const response = await getNotifications(
      page.value,
      rowsPerPage.value,
      selectedStatus.value || undefined,
      selectedType.value || undefined,
      selectedCategory.value || undefined
    );
    
    notifications.value = response.notifications;
    totalElements.value = response.totalElements;
  } catch (error) {
    console.error('Error fetching notifications:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải thông báo',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

const markAsRead = async (notification: Notification) => {
  try {
    await markNotificationAsRead(notification.id);
    notification.readAt = new Date().toISOString();
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Đã đánh dấu thông báo là đã đọc',
      life: 3000
    });
  } catch (error) {
    console.error('Error marking notification as read:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể cập nhật trạng thái thông báo',
      life: 3000
    });
  }
};

const deleteNotification = async (notification: Notification) => {
  try {
    await deleteNotificationAPI(notification.id);
    const index = notifications.value.findIndex(n => n.id === notification.id);
    if (index !== -1) {
      notifications.value.splice(index, 1);
    }
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Đã xóa thông báo',
      life: 3000
    });
  } catch (error) {
    console.error('Error deleting notification:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể xóa thông báo',
      life: 3000
    });
  }
};

const hasOrderData = (notification: Notification): boolean => {
  if (!notification.data) return false;
  try {
    const data = JSON.parse(notification.data);
    return data.orderId !== undefined;
  } catch {
    return false;
  }
};

const viewOrderDetails = async (notification: Notification) => {
  if (!notification.data) return;
  
  try {
    const data = JSON.parse(notification.data);
    if (!data.orderId) return;
    
    loadingOrderDetail.value = true;
    const order = await getOrderDetail(data.orderId);
    selectedOrder.value = order;
    showOrderModal.value = true;
  } catch (error) {
    console.error('Error parsing notification data:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải chi tiết đơn hàng',
      life: 3000
    });
  } finally {
    loadingOrderDetail.value = false;
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
  fetchNotifications();
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

const getNotificationTypeLabel = (type: string) => {
  const labels: Record<string, string> = {
    PUSH: "Push Notification",
    EMAIL: "Email",
    SMS: "SMS",
  };
  return labels[type] || type;
};

const getNotificationIcon = (type: string) => {
  const icons: Record<string, string> = {
    PUSH: "pi pi-bell text-blue-500",
    EMAIL: "pi pi-envelope text-green-500",
    SMS: "pi pi-mobile text-orange-500",
  };
  return icons[type] || "pi pi-bell text-gray-500";
};

const getCategoryLabel = (category: string) => {
  const labels: Record<string, string> = {
    ORDER: "Đơn hàng",
    VENDOR_PRODUCT_APPROVAL: "Phê duyệt sản phẩm",
    PAYMENT: "Thanh toán",
    SYSTEM: "Hệ thống",
  };
  return labels[category] || category;
};

const getNotificationStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    DRAFT: "Nháp",
    SCHEDULED: "Đã lên lịch",
    SENT: "Đã gửi",
    FAILED: "Thất bại",
    READ: "Đã đọc",
  };
  return labels[status] || status;
};

const getNotificationStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    DRAFT: "bg-gray-100 text-gray-800",
    SCHEDULED: "bg-yellow-100 text-yellow-800",
    SENT: "bg-blue-100 text-blue-800",
    FAILED: "bg-red-100 text-red-800",
    READ: "bg-green-100 text-green-800",
  };
  return classes[status] || "bg-gray-100 text-gray-800";
};

const getStatusLabel = (status: string) => {
  const labels: Record<string, string> = {
    PENDING: "Chờ xử lý",
    PROCESSING: "Đang xử lý",
    DELIVERING: "Đang giao hàng",
    CANCELLED: "Đã hủy",
    REFUNDED: "Đã hoàn trả",
    RECEIVED: "Đã nhận hàng",
  };
  return labels[status] || status;
};

onMounted(() => {
  fetchNotifications();
});

watch([selectedStatus, selectedType, selectedCategory], () => {
  page.value = 0;
  fetchNotifications();
});
</script>

<style scoped>
:deep(.p-datatable .p-datatable-tbody > tr > td) {
  padding: 1rem;
}
</style> 