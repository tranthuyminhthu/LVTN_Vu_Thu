<template>
  <div class="rounded-lg bg-gray-100" >
    <div class="bg-[#2f5acf] px-4 py-2 flex justify-between items-center rounded-lg">
      <div class="text-white flex flex-col gap-2">
        <span class="font-bold cursor-pointer" @click="router.push(`/account/orders/${order.orderId}`)">#{{ order.orderId }}</span>
        <span class="text-xs">{{ formatDate(order.createdAt) }}</span>
      </div>
      <button class="bg-white text-black px-4 py-2 rounded-full text-xs">
        {{ getStatusLabel(order.status) }}
      </button>
    </div>
    <div>
      <div class="border-b border-gray-200 px-4 py-2 flex gap-4">
        <img
          :src="order.image || ''"
          alt=""
          class="rounded-lg !w-24 !h-24 object-cover"
        />
        <div class="flex flex-col gap-2 justify-center">
          <span class="font-bold"
            >Áo ba lỗ nam mặc trong thoáng khí nhanh khô</span
          >
          <span>Trắng / L</span>
          <span>x4</span>
          <span class="font-bold">89.000đ</span>
        </div>
      </div>
      <div class="px-4 flex justify-between items-center py-2">
        <div class="flex gap-4">
          <button
            class="cursor-pointer bg-white text-black px-4 py-2 rounded-full text-xs border border-black hover:!bg-black hover:!text-white transition-all duration-300"
            @click.stop="router.push(`/message`)"
            v-if="order.status === 'RECEIVED'"
          >
            Cần hỗ trợ
          </button>
          <button
            class="cursor-pointer bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black"
            v-if="order.status === 'DELIVERING'"
            @click="handleReceivedOrder"
            >
            Đã nhận hàng
          </button>
          <button
            class="cursor-pointer bg-black text-white px-4 py-2 rounded-full text-xs hover:!bg-white hover:!text-black transition-all duration-300 border border-black"
            v-if="order.status === 'PENDING'"
            @click="confirmCancelOrder"
            >
            Hủy đơn hàng
          </button>
        </div>
        <span class="font-bold text-xs">{{ formatPrice(order.totalAmount) }}</span>
      </div>
    </div>

    <!-- Cancel Order Modal -->
    <Dialog
      v-model:visible="showCancelModal"
      modal
      header="Hủy đơn hàng"
      :style="{ width: '500px' }"
      :closable="true"
      @hide="closeCancelModal"
    >
      <div class="space-y-4">
        <p class="text-gray-600 mb-4">Vui lòng chọn lý do hủy đơn hàng:</p>
        
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Lý do hủy *</label>
          <Dropdown
            v-model="cancelReason"
            :options="cancelReasons"
            optionLabel="label"
            optionValue="value"
            placeholder="Chọn lý do hủy đơn hàng"
            class="w-full"
          />
        </div>

        <div v-if="cancelReason === 'other'">
          <label class="block text-sm font-medium text-gray-700 mb-2">Lý do khác *</label>
          <Textarea
            v-model="otherReason"
            rows="3"
            placeholder="Vui lòng mô tả lý do hủy đơn hàng..."
            class="w-full"
          />
        </div>
      </div>

      <template #footer>
        <div class="flex justify-end gap-3">
          <Button
            label="Hủy"
            severity="secondary"
            outlined
            @click="closeCancelModal"
          />
          <Button
            label="Xác nhận hủy"
            severity="danger"
            @click="handleCancelConfirm"
          />
        </div>
      </template>
    </Dialog>
  </div>
</template>
<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { Order } from '@/types';
import { computed, ref, watch } from 'vue';
import { changeOrderStatusToCancelled, changeOrderStatusToReceived } from '@/api/order';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import Dialog from 'primevue/dialog';
import Dropdown from 'primevue/dropdown';
import Textarea from 'primevue/textarea';
import Button from 'primevue/button';

const props = defineProps<{ order: Order }>();
const router = useRouter();
const toast = useToast();
const confirm = useConfirm();
const cancelReason = ref('');
const otherReason = ref('');
const showCancelModal = ref(false);
const note = ref(cancelReason.value === 'other' ? otherReason.value : cancelReason.value);

const emit = defineEmits<{
  (e: 'update:order'): void
}>();

const formatDate = (date: string) => {
  return new Date(date).toLocaleDateString('vi-VN');
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getStatusLabel = (status: string) => {
  switch (status) {
    case 'PENDING': return 'Chờ xử lý';
    case 'PROCESSING': return 'Đang xử lý';
    case 'SHIPPED': return 'Đang giao hàng';
    case 'DELIVERED': return 'Đã giao hàng';
    case 'CANCELLED': return 'Đã hủy';
    case 'REFUNDED': return 'Đã hoàn trả';
    case 'ACCEPTED': return 'Đang gói hàng';
    default: return status;
  }
};

const handleReceivedOrder = async () => {
  await changeOrderStatusToReceived(props.order.orderId);
  toast.add({
    severity: 'success',
    summary: 'Thành công',
    detail: 'Đã nhận hàng',
    life: 3000
  });
  emit('update:order');
};

const cancelReasons = [
  { label: 'Thay đổi ý định mua hàng', value: 'Thay đổi ý định mua hàng' },
  { label: 'Tìm thấy sản phẩm rẻ hơn', value: 'Tìm thấy sản phẩm rẻ hơn' },
  { label: 'Đặt nhầm sản phẩm', value: 'Đặt nhầm sản phẩm' },
  { label: 'Không còn nhu cầu', value: 'Không còn nhu cầu' },
  { label: 'Vấn đề về thanh toán', value: 'Vấn đề về thanh toán' },
  { label: 'Khác', value: 'other' }
];

const confirmCancelOrder = () => {
  showCancelModal.value = true;
};

const handleCancelConfirm = () => {
  if (!cancelReason.value) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Vui lòng chọn lý do hủy đơn hàng',
      life: 3000
    });
    return;
  }

  if (cancelReason.value === 'other' && !otherReason.value.trim()) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Vui lòng nhập lý do hủy đơn hàng',
      life: 3000
    });
    return;
  }

  showCancelModal.value = false;
  handleCancelOrder();
};

const closeCancelModal = () => {
  showCancelModal.value = false;
  cancelReason.value = '';
  otherReason.value = '';
  note.value = '';
};

const handleCancelOrder = async () => {
  await changeOrderStatusToCancelled(props.order.orderId, note.value);
  toast.add({
    severity: 'success',
    summary: 'Thành công',
    detail: 'Đã hủy đơn hàng',
    life: 3000
  });
  emit('update:order');
};

watch(cancelReason, (newVal) => {
  note.value = newVal;
});
watch(otherReason, (newVal) => {
  note.value = newVal;
});
</script>
