<template>
  <div class="p-6 rounded-lg">
    <Toast />
    <h1 class="text-2xl font-semibold text-white mb-6">Quản lý đơn hàng</h1>
    <TabView v-model:activeIndex="activeTab" class="rounded-lg">
      <TabPanel
        v-for="status in orderStatuses"
        :key="status.value"
        :value="status.value"
      >
        <template #header>
          {{ status.label }}
        </template>
        <div class="bg-white rounded-lg shadow-sm">
          <!-- Đã xóa filter -->
          <DataTable
            :value="orders"
            class="w-full"
            :pt="{ table: { class: 'min-w-full' } }"
            :rows="rowsPerPage"
            paginator
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :totalRecords="totalElements"
            :first="page * rowsPerPage"
            :loading="loading"
            :lazy="true"
            @page="onPage"
          >
            <Column
              field="orderId"
              header="Mã đơn hàng"
              body-class="px-6 py-4 whitespace-nowrap font-medium"
            />
            <Column
              header="Khách hàng"
              body-class="px-6 py-4 whitespace-nowrap"
            >
              <template #body="slotProps">
                <div class="text-sm text-gray-900">
                  {{ slotProps.data.receiverName }}
                </div>
                <div class="text-sm text-gray-500">
                  {{ slotProps.data.receiverPhone }}
                </div>
              </template>
            </Column>
            <Column
              field="createdAt"
              header="Ngày đặt"
              body-class="px-6 py-4 whitespace-nowrap"
            >
              <template #body="slotProps">
                <div class="text-sm text-gray-900">
                  {{ formatDate(slotProps.data.createdAt) }}
                </div>
              </template>
            </Column>
            <Column
              field="totalAmount"
              header="Tổng tiền"
              body-class="px-6 py-4 whitespace-nowrap"
            >
              <template #body="slotProps">
                <div class="text-sm text-gray-900">
                  {{ formatPrice(slotProps.data.totalAmount || 0) }}
                </div>
              </template>
            </Column>
            <Column
              field="status"
              header="Trạng thái"
              body-class="px-6 py-4 whitespace-nowrap"
            >
              <template #body="slotProps">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getStatusClass(slotProps.data.status),
                  ]"
                >
                  {{ getStatusLabel(slotProps.data.status) }}
                </span>
              </template>
            </Column>
            <Column
              header="Thao tác"
              body-class="px-6 py-4 whitespace-nowrap text-sm font-medium"
            >
              <template #body="slotProps">
                <button
                  v-if="currentStatus === 'PENDING'"
                  @click="handleClickAccept(slotProps.data)"
                  class="text-green-600 hover:text-green-900 mr-3"
                >
                  <i class="pi pi-check"></i>
                </button>
                <button
                  @click="viewOrderDetails(slotProps.data)"
                  class="text-blue-600 hover:text-blue-900"
                >
                  <i class="pi pi-eye"></i>
                </button>
              </template>
            </Column>
          </DataTable>
        </div>
      </TabPanel>
    </TabView>
    <!-- Dialog chi tiết giữ nguyên -->
    <Dialog
      v-model:visible="showModal"
      :modal="true"
      :closable="true"
      :style="{ width: '800px' }"
      :header="
        selectedOrder ? `Chi tiết đơn hàng #${selectedOrder.orderId}` : ''
      "
    >
      <LoadingGlobal :isLoading="loadingDetail">
        <div class="mt-3">
          <div class="flex justify-between items-center mb-4">
            <!-- Đã có header của Dialog, có thể bỏ phần này nếu muốn -->
          </div>
          <div v-if="selectedOrder" class="space-y-4">
            <!-- Lưu ý giao hàng & Ghi chú -->
            <div class="p-4 rounded-lg shadow border border-gray-200 mb-2">
              <h4 class="font-medium mb-2">Lưu ý - Ghi chú</h4>
              <div class="!grid grid-cols-2 gap-4">
                <div>
                  <p class="text-sm text-gray-600 font-semibold mb-2">
                    Lưu ý giao hàng <span class="text-red-500">*</span>
                  </p>
                  <div class="flex flex-col gap-2">
                    <label class="flex items-center gap-2">
                      <input
                        type="radio"
                        value="KHONGCHOXEMHANG"
                        v-model="shippingNoteTypeEdit"
                        :disabled="!canProcessOrder(selectedOrder)"
                      />
                      <span>Không cho xem hàng</span>
                    </label>
                    <label class="flex items-center gap-2">
                      <input
                        type="radio"
                        value="CHOXEMHANGKHONGTHU"
                        v-model="shippingNoteTypeEdit"
                        :disabled="!canProcessOrder(selectedOrder)"
                        checked
                      />
                      <span>Cho xem hàng không cho thử</span>
                    </label>
                    <label class="flex items-center gap-2">
                      <input
                        type="radio"
                        value="CHOTHUHANG"
                        v-model="shippingNoteTypeEdit"
                        :disabled="!canProcessOrder(selectedOrder)"
                      />
                      <span>Cho thử hàng</span>
                    </label>
                  </div>
                </div>
                <div>
                  <p class="text-sm text-gray-600 font-semibold mb-2" >
                    Ghi chú
                  </p>
                  <Textarea
                  :disabled="!canProcessOrder(selectedOrder)"
                    class="w-full border rounded p-2 min-h-[80px]"
                    v-model="shippingNoteDetailEdit"
                  ></Textarea>
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
                <div
                  v-if="selectedOrder.items && selectedOrder.items.length > 0"
                >
                  <div
                    v-for="item in selectedOrder.items as OrderItem[]"
                    :key="item.productSku"
                    class="flex items-center p-4 border-b last:border-b-0 border-gray-200"
                  >
                    <div
                      class="w-16 h-16 flex items-center justify-center bg-gray-100 rounded-lg mr-4"
                    >
                      <Image
                        :src="
                          item.image && item.image !== 'a'
                            ? item.image
                            : 'https://via.placeholder.com/80x80?text=No+Image'
                        "
                        alt="Ảnh sản phẩm"
                        class="w-14 h-14 object-cover rounded"
                        preview
                      />
                    </div>
                    <div class="ml-0 flex-1">
                      <h5 class="font-medium">{{ item.productName }}</h5>
                      <p class="text-sm text-gray-500">
                        Số lượng: {{ item.quantity }}
                      </p>
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
            <!-- Order Summary -->
            <div class="rounded-lg">
              <div class="flex justify-between font-medium text-lg pt-2 mt-2">
                <span>Tổng cộng</span>
                <span>{{ formatPrice(selectedOrder.totalAmount || 0) }}</span>
              </div>
            </div>
            <!-- Action Buttons -->
            <div class="flex justify-end gap-3 mt-4">
              <button
                v-if="canProcessOrder(selectedOrder)"
                @click="processOrder(selectedOrder)"
                class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                {{ getNextActionLabel(selectedOrder.status) }}
              </button>
            </div>
          </div>
        </div>
      </LoadingGlobal>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, computed } from "vue";
import Dialog from "primevue/dialog";
import TabView from "primevue/tabview";
import TabPanel from "primevue/tabpanel";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import type {
  Order as OrderBase,
  OrderItem as OrderItemBase,
} from "@/api/order";
import { getOrders, getOrderDetail, changeOrderStatusToDelivering, changeOrderStatusToAccepted } from "@/api/order";
import Image from "primevue/image";
import { useToast } from "primevue/usetoast";
import Toast from "primevue/toast";
import { getService } from "@/api/ghn";
import type { OrderAcceptedRequestDto } from "@/types";
import LoadingGlobal from "@/components/LoadingGlobal.vue";
import { Textarea } from "primevue";

// Extend OrderItem to include image
interface OrderItem extends OrderItemBase {
  image?: string;
}

const orderStatuses = [
  { value: "PENDING", label: "Chờ xử lý" },
  { value: "PROCESSING", label: "Đang xử lý" },
  { value: "DELIVERING", label: "Đang giao hàng" },
  { value: "DELIVERED", label: "Đã giao hàng" },
  { value: "CANCELLED", label: "Đã hủy" },
  { value: "REFUNDED", label: "Đã hoàn trả" },
];

const orders = ref<OrderBase[]>([]);
const loading = ref(false);
const searchQuery = ref("");
const sortBy = ref("createdAt");
const activeTab = ref(0);
const showModal = ref(false);
const selectedOrder = ref<OrderBase | null>(null);
const loadingDetail = ref(false);
const serviceId = ref(0);
const orderAcceptedRequest = ref<OrderAcceptedRequestDto | null>(null);
const shippingNoteTypeEdit = ref("CHOXEMHANGKHONGTHU");
const shippingNoteDetailEdit = ref("");

const page = ref(0);
const rowsPerPage = ref(5);
const totalElements = ref(0);
const currentStatus = ref(orderStatuses[0].value);

const toast = useToast();

const fetchOrders = async () => {
  loading.value = true;
  try {
    const res = await getOrders(
      page.value,
      rowsPerPage.value,
      currentStatus.value
    );
    orders.value = res.orders;
    totalElements.value = res.totalElements;
  } catch (e) {
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchOrders();
});

watch(activeTab, (newIndex) => {
  currentStatus.value = orderStatuses[newIndex].value;
  page.value = 0; // reset về trang đầu
  fetchOrders();
});

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
  return orderStatuses.find((s) => s.value === status)?.label || status;
};

const getStatusClass = (status: string) => {
  const classes: Record<string, string> = {
    PENDING: "bg-yellow-100 text-yellow-800",
    PROCESSING: "bg-blue-100 text-blue-800",
    SHIPPED: "bg-purple-100 text-purple-800",
    DELIVERED: "bg-green-100 text-green-800",
    CANCELLED: "bg-red-100 text-red-800",
    REFUNDED: "bg-gray-100 text-gray-800",
  };
  return classes[status] || "bg-gray-100 text-gray-800";
};

const canProcessOrder = (order: OrderBase) => {
  if (order.status === "PROCESSING") {
    return true;
  } else {
    return false;
  }
};

const getNextActionLabel = (status: string) => {
  const actions: Record<string, string> = {
    PENDING: "Xác nhận đơn hàng",
    PROCESSING: "Đóng gói và giao hàng",
    SHIPPED: "Xác nhận đã giao",
  };
  return actions[status as keyof typeof actions] || "Cập nhật trạng thái";
};

const viewOrderDetails = async (order: OrderBase) => {
  loadingDetail.value = true;
  try {
    const detail = await getOrderDetail(order.orderId);
    selectedOrder.value = detail;
    shippingNoteDetailEdit.value = detail.note || "";
    showModal.value = true;
    const service = await getService({
      from_district: selectedOrder.value?.senderDistrictId || 1,
      to_district: selectedOrder.value?.receiverDistrictId || 1,
      shop_id: selectedOrder.value?.shopId || 1,
    });
    serviceId.value = service.data.find(
      (item: any) => item.short_name === "Hàng nhẹ"
    )?.service_id;
    
  } catch (e) {
    // handle error, e.g. show toast
  } finally {
    loadingDetail.value = false;
  }
};

const closeModal = () => {
  showModal.value = false;
  selectedOrder.value = null;
};

const processOrder = async (order: OrderBase) => {
  loadingDetail.value = true;
  try {
    orderAcceptedRequest.value = {
      orderId: order.orderId,
      serviceId: serviceId.value,
      note: shippingNoteDetailEdit.value,
      requiredNote: shippingNoteTypeEdit.value,
      content: "Đơn hàng quần áo",
    };
    await changeOrderStatusToDelivering(orderAcceptedRequest.value as OrderAcceptedRequestDto);
    toast.add({
      severity: "success",
      summary: "Thành công",
      detail: "Đơn hàng đã được xác nhận",
      life: 3000,
    });
    fetchOrders();
    if (showModal.value && selectedOrder.value) {
      // reload detail if dialog is open
      const detail = await getOrderDetail(order.orderId);
      selectedOrder.value = detail;
    }
    showModal.value = false;
  } catch (e) {
    toast.add({
      severity: "info",
      summary: "Lỗi",
      detail: "Không thể giao ở khu vực này",
      life: 3000,
    });
  } finally {
    loadingDetail.value = false;
  }
};

const isStatusCompleted = (currentStatus: string, checkStatus: string) => {
  const statusFlow = ["PENDING", "PROCESSING", "SHIPPED", "DELIVERED"];
  const currentIndex = statusFlow.indexOf(currentStatus);
  const checkIndex = statusFlow.indexOf(checkStatus);
  return checkIndex <= currentIndex;
};

const getStatusDate = (order: OrderBase, status: string) => {
  // Assuming statusHistory is not part of the Order interface from API
  // If it were, you would iterate through it to find the date for the specific status
  return "-";
};

const handleClickAccept = async (order: OrderBase) => {
  await changeOrderStatusToAccepted(order.orderId);
  fetchOrders();
};
</script>
