<template>
  <div class="flex gap-4">
    <h2 class="text-2xl font-bold">Thông tin đơn hàng #45326960942</h2>
    <button
      class="bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black block"
    >
      Đã hủy
    </button>
  </div>
  <div class="flex gap-4 my-4 text-sm">
    <div class="flex flex-col gap-1">
      <span>Ngày đặt hàng:</span>
      <span>Tên người nhận:</span>
      <span>Địa chỉ email:</span>
      <span>Số điện thoại:</span>
      <span>Phương thức thanh toán:</span>
      <span>Địa chỉ giao hàng:</span>
      <span>Ghi chú:</span>
    </div>
    <div class="flex flex-col gap-1">
      <span>{{ formatDate(new Date(order?.createdAt || '')) }}</span>
      <span>{{ order?.receiverName || 'N/A' }}</span>
      <span>{{ order?.receiverEmail || 'N/A' }}</span>
      <span>{{ order?.receiverPhone || 'N/A' }}</span>
      <span>Thanh toán khi nhận hàng</span>
      <span>{{ order?.receiverAddress || 'N/A' }}</span>
      <span>{{ order?.note || '' }}</span>
    </div>
  </div>
  <div class="flex gap-4">
    <button
      class="bg-white text-black px-4 py-2 rounded-full text-xs hover:!bg-black hover:!text-white transition-all duration-300 border border-black"
    >
      Cần hỗ trợ
    </button>
    <button
      class="bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black"
    >
      Mua lại
    </button>
  </div>

  <!-- Modal báo cáo -->
  <Dialog 
    v-model:visible="showReportModal" 
    modal 
    header="Báo cáo sản phẩm" 
    :style="{ width: '50vw' }"
    :closable="true"
  >
    <div class="flex flex-col gap-4">
      <div>
        <label class="block text-sm font-medium mb-2">Lý do báo cáo:</label>
        <Dropdown
          v-model="selectedReportReason"
          :options="reportReasons"
          optionLabel="label"
          optionValue="value"
          placeholder="Chọn lý do báo cáo"
          class="w-full"
        />
      </div>
      
      <div v-if="selectedReportReason === 'other'">
        <label class="block text-sm font-medium mb-2">Lý do khác:</label>
        <Textarea
          v-model="otherReason"
          rows="4"
          placeholder="Vui lòng mô tả lý do báo cáo..."
          class="w-full"
        />
      </div>
      
      <div>
        <label class="block text-sm font-medium mb-2">Minh chứng (tùy chọn):</label>
        <FileUpload
          v-model="evidenceFiles"
          :multiple="true"
          accept="image/*,.pdf,.doc,.docx"
          :maxFileSize="5000000"
          chooseLabel="Chọn file"
          cancelLabel="Hủy"
          :auto="true"
          :showCancelButton="false"
          class="w-full"
        >
          <template #empty>
            <p class="text-gray-500 text-sm">Kéo thả file vào đây hoặc click để chọn file</p>
          </template>
        </FileUpload>
        <p class="text-xs text-gray-500 mt-1">
          Hỗ trợ: JPG, PNG, PDF, DOC, DOCX (tối đa 5MB/file)
        </p>
      </div>
      
      <div>
        <label class="block text-sm font-medium mb-2">Yêu cầu xử lý:</label>
        <div class="flex flex-col gap-2">
          <div class="flex items-center">
            <Checkbox
              v-model="requestExchange"
              :binary="true"
              inputId="exchange"
              class="mr-2"
            />
            <label for="exchange" class="text-sm cursor-pointer">Yêu cầu đổi trả sản phẩm</label>
          </div>
          <div class="flex items-center">
            <Checkbox
              v-model="requestRefund"
              :binary="true"
              inputId="refund"
              class="mr-2"
            />
            <label for="refund" class="text-sm cursor-pointer">Yêu cầu hoàn tiền</label>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="flex justify-end gap-2">
        <Button 
          label="Hủy" 
          @click="closeReportModal" 
          severity="secondary"
          outlined
        />
        <Button 
          label="Gửi báo cáo" 
          @click="submitReport" 
          severity="danger"
        />
      </div>
    </template>
  </Dialog>
  <div class="my-4">
    <p class="text-xl font-bold">Tình trạng đơn hàng</p>
    <Timeline
      :value="timelineEvents"
      class="my-8"
      :pt="{
        eventOpposite: () => ({
          class: '!flex-0 whitespace-nowrap pl-0',
        }),
      }"
    >
      <template #marker="slotProps">
        <span
          class="flex !w-3 !h-3 items-center justify-center text-white rounded-full z-10 shadow-sm"
          :style="{ backgroundColor: getStatusColor(slotProps.item.status) }"
        >
        </span>
      </template>
      <template #opposite="slotProps">
        <div class="text-surface-500 dark:text-surface-400 text-xs">
          {{ formatTime(slotProps.item.changedAt) }}
        </div>
        <div class="text-surface-500 dark:text-surface-400 text-xs">
          {{ formatDate(new Date(slotProps.item.changedAt)) }}
        </div>
      </template>
      <template #content="slotProps">
        <div class="text-sm font-bold">
          {{ getStatusText(slotProps.item.status) }}
        </div>
        <div class="text-xs">
          {{ slotProps.item.note }}
        </div>
      </template>
    </Timeline>
  </div>
  <div class="rounded-lg">
    <div class="">
      <div
        class="bg-[#2f5acf] !grid grid-cols-12 text-white text-center py-4 rounded-lg"
      >
        <span class="col-span-2">Tên sản phẩm</span>
        <span class="col-span-2">Số lượng</span>
        <span class="col-span-2">Giá niêm yết</span>
        <span class="col-span-2">Biến thể</span>
        <span class="col-span-2">Thành tiền</span>
        <span class="col-span-2">Hành động</span>
      </div>
      <div class="bg-gray-100 rounded-b-lg">
        <div
          v-for="item in order?.items || []"
          :key="item.productSku"
          class="!grid grid-cols-12 text-center py-4 px-2 items-center border-b border-gray-200"
        >
          <div class="col-span-2 flex gap-2 items-center">
            <img
              :src="item.image"
              :alt="item.productName"
              class="rounded-lg !w-18 object-cover border border-[#2f5acf]"
            />
            <span class="text-xs underline text-blue-600 hover:text-blue-800 cursor-pointer" @click="navigateToProductDetail(item.productSku)">{{ item.productName }}</span>
          </div>
          <span class="col-span-2">{{ item.quantity }}</span>
          <span class="col-span-2">{{ formatPrice(item.price) }}</span>
          <span class="col-span-2">{{ getVariantFromSku(item.productSku) }}</span>
          <span class="col-span-2">{{ formatPrice(item.price * item.quantity) }}</span>
          <span class="col-span-2">
            <button 
              class="text-red-600 hover:text-red-800 text-xs"
              @click="openReportModal(item.productSku)"
            >
              Báo cáo
            </button>
          </span>
        </div>
      </div>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Mã giảm giá</span>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Tổng giá trị sản phẩm</span>
      <span class="font-bold text-sm">{{ formatPrice(order?.totalAmount || 0) }}</span>
    </div>
    <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Tổng khuyến mãi</span>
      <span class="font-bold text-sm">0đ</span>
    </div>
    <!-- <div
      class="flex justify-between items-center py-4 border-b border-gray-200"
    >
      <span class="font-bold text-sm">Phí vận chuyển</span>
      <span class="font-bold text-sm">0đ</span>
    </div> -->
    <div
      class="bg-black rounded-b-lg text-white p-4 flex justify-between items-center"
    >
      <span class="font-bold text-sm">Tổng thanh toán</span>
      <span class="font-bold text-sm">{{ formatPrice(order?.totalAmount || 0) }}</span>
    </div>
    <div class=""></div>
  </div>
</template>
<script setup lang="ts">
import Timeline from "primevue/timeline";
import Dialog from "primevue/dialog";
import Dropdown from "primevue/dropdown";
import Textarea from "primevue/textarea";
import Button from "primevue/button";
import FileUpload from "primevue/fileupload";
import Checkbox from "primevue/checkbox";
import { onMounted, ref, computed } from "vue";
import { getOrderDetail } from "@/api/order";
import { useRoute } from "vue-router";
import type { Order } from "@/types";
import { formatDate } from "@/utils/date"
import { useRouter } from "vue-router";
const route = useRoute();
const orderId = route.params.id as string;
const order = ref<Order | null>(null);
const router = useRouter();

// Modal báo cáo
const showReportModal = ref(false);
const selectedReportReason = ref<string>('');
const otherReason = ref('');
const currentProductSku = ref('');
const evidenceFiles = ref<File[]>([]);

const reportReasons = [
  { label: 'Sản phẩm không đúng mô tả', value: 'wrong_description' },
  { label: 'Sản phẩm bị hỏng', value: 'damaged' },
  { label: 'Sản phẩm không đúng kích thước', value: 'wrong_size' },
  { label: 'Sản phẩm không đúng màu sắc', value: 'wrong_color' },
  { label: 'Sản phẩm giả mạo', value: 'fake' },
  { label: 'Lý do khác', value: 'other' }
];

// Yêu cầu đổi trả và hoàn tiền
const requestExchange = ref(false);
const requestRefund = ref(false);
const exchangeReason = ref<string>('');
const refundMethod = ref<string>('');

const exchangeReasons = [
  { label: 'Sản phẩm bị lỗi', value: 'defective' },
  { label: 'Không đúng kích thước', value: 'wrong_size' },
  { label: 'Không đúng màu sắc', value: 'wrong_color' },
  { label: 'Sản phẩm bị hỏng trong quá trình vận chuyển', value: 'damaged_shipping' },
  { label: 'Lý do khác', value: 'other' }
];

const refundMethods = [
  { label: 'Hoàn tiền qua tài khoản ngân hàng', value: 'bank_transfer' },
  { label: 'Hoàn tiền qua ví điện tử', value: 'e_wallet' },
  { label: 'Hoàn tiền qua thẻ tín dụng', value: 'credit_card' },
  { label: 'Hoàn tiền qua cửa hàng', value: 'store_credit' }
];

onMounted(async () => {
  order.value = await getOrderDetail(orderId);
  window.scrollTo(0, 0);
});


const events = ref([
  {
    status: "Đã hủy",
    description: "Đơn hàng đã bị hủy",
    date: "15-10-2020",
    icon: "pi pi-shopping-cart",
    color: "#2f5acf",
    time: "10:30",
  },
  {
    status: "Chờ xác nhận",
    description: "Hệ thống đang xác nhận đơn hàng của bạn",
    date: "15-10-2020",
    icon: "pi pi-cog",
    color: "#525252",
    time: "14:00",
  },
  {
    status: "Chờ xác nhận",
    description: "Hệ thống đang xác nhận đơn hàng của bạn",
    date: "15-10-2020",
    icon: "pi pi-cog",
    color: "#525252",
    time: "14:00",
  },
]);

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getVariantFromSku = (sku: string) => {
  const parts = sku.split('-');
  return parts[parts.length - 1] || 'N/A';
};

const timelineEvents = computed(() => {
  if (!order.value?.timeline) return [];
  return [...order.value.timeline].sort((a, b) => 
    new Date(b.changedAt).getTime() - new Date(a.changedAt).getTime()
  );
});

const formatTime = (dateString: string) => {
  return new Date(dateString).toLocaleTimeString('vi-VN', {
    hour: '2-digit',
    minute: '2-digit'
  });
};

const getStatusColor = (status: string) => {
  // Nếu là trạng thái hiện tại của đơn hàng, màu xanh nước biển
  if (status === order.value?.status) {
    return '#0066cc'; // Màu xanh nước biển
  }
  
  const colors: Record<string, string> = {
    'PENDING': '#525252',
    'PROCESSING': '#2f5acf',
    'DELIVERING': '#ffa500',
    'COMPLETED': '#28a745',
    'CANCELLED': '#dc3545'
  };
  return colors[status] || '#525252';
};

const getStatusText = (status: string) => {
  const statusTexts: Record<string, string> = {
    'PENDING': 'Chờ xác nhận',
    'PROCESSING': 'Đang xử lý',
    'DELIVERING': 'Đang giao hàng',
    'COMPLETED': 'Đã hoàn thành',
    'CANCELLED': 'Đã hủy'
  };
  return statusTexts[status] || status;
};

const navigateToProductDetail = (productSku: string) => {
  const productId = productSku.split('-')[0];
  router.push({name: 'product', params: {id: productId}});
};

const openReportModal = (productSku: string) => {
  currentProductSku.value = productSku;
  showReportModal.value = true;
  selectedReportReason.value = '';
  otherReason.value = '';
  evidenceFiles.value = [];
  requestExchange.value = false;
  requestRefund.value = false;
  exchangeReason.value = '';
  refundMethod.value = '';
};

const closeReportModal = () => {
  showReportModal.value = false;
  selectedReportReason.value = '';
  otherReason.value = '';
  currentProductSku.value = '';
  evidenceFiles.value = [];
  requestExchange.value = false;
  requestRefund.value = false;
  exchangeReason.value = '';
  refundMethod.value = '';
};

const submitReport = () => {
  if (!selectedReportReason.value) {
    alert('Vui lòng chọn lý do báo cáo');
    return;
  }
  
  if (selectedReportReason.value === 'other' && !otherReason.value.trim()) {
    alert('Vui lòng nhập lý do báo cáo');
    return;
  }
  
  if (requestExchange.value && !exchangeReason.value) {
    alert('Vui lòng chọn lý do đổi trả');
    return;
  }
  
  if (requestRefund.value && !refundMethod.value) {
    alert('Vui lòng chọn phương thức hoàn tiền');
    return;
  }
  
  // TODO: Gửi báo cáo lên server
  console.log('Báo cáo sản phẩm:', {
    productSku: currentProductSku.value,
    reason: selectedReportReason.value,
    otherReason: otherReason.value,
    evidenceFiles: evidenceFiles.value,
    requestExchange: requestExchange.value,
    requestRefund: requestRefund.value,
    exchangeReason: exchangeReason.value,
    refundMethod: refundMethod.value
  });
  
  alert('Báo cáo đã được gửi thành công!');
  closeReportModal();
};
</script>
