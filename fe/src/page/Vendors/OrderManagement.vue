<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-semibold">Quản lý đơn hàng</h1>
      <div class="flex gap-2">
        <button
          v-for="status in orderStatuses"
          :key="status.value"
          @click="currentStatus = status.value"
          :class="[
            'px-4 py-2 rounded-md focus:outline-none focus:ring-2',
            currentStatus === status.value
              ? 'bg-blue-600 text-white'
              : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
          ]"
        >
          {{ status.label }}
        </button>
      </div>
    </div>

    <!-- Order List -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b">
        <div class="flex items-center gap-4">
          <div class="flex-1">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Tìm kiếm đơn hàng..."
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <select
            v-model="sortBy"
            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="date">Ngày đặt</option>
            <option value="total">Tổng tiền</option>
            <option value="status">Trạng thái</option>
          </select>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Mã đơn hàng
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Khách hàng
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ngày đặt
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tổng tiền
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Trạng thái
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="order in filteredOrders" :key="order.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">#{{ order.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ order.customerName }}</div>
                <div class="text-sm text-gray-500">{{ order.customerPhone }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatDate(order.orderDate) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatPrice(order.total) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getStatusClass(order.status)
                  ]"
                >
                  {{ getStatusLabel(order.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="viewOrderDetails(order)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  <i class="pi pi-eye"></i>
                </button>
                <button
                  v-if="canProcessOrder(order)"
                  @click="processOrder(order)"
                  class="text-green-600 hover:text-green-900"
                >
                  <i class="pi pi-check"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Order Details Modal -->
    <Dialog v-model:visible="showModal" :modal="true" :closable="true" :style="{ width: '800px' }" :header="selectedOrder ? `Chi tiết đơn hàng #${selectedOrder.id}` : ''">
      <div class="mt-3">
        <div class="flex justify-between items-center mb-4">
          <!-- Đã có header của Dialog, có thể bỏ phần này nếu muốn -->
        </div>
        <div v-if="selectedOrder" class="space-y-4">
          <!-- Customer Information -->
          <div class="bg-gray-50 p-4 rounded-lg">
            <h4 class="font-medium mb-2">Thông tin khách hàng</h4>
            <div class="grid grid-cols-2 gap-4">
              <div>
                <p class="text-sm text-gray-600">Tên khách hàng</p>
                <p class="font-medium">{{ selectedOrder.customerName }}</p>
              </div>
              <div>
                <p class="text-sm text-gray-600">Số điện thoại</p>
                <p class="font-medium">{{ selectedOrder.customerPhone }}</p>
              </div>
              <div class="col-span-2">
                <p class="text-sm text-gray-600">Địa chỉ giao hàng</p>
                <p class="font-medium">{{ selectedOrder.shippingAddress }}</p>
              </div>
            </div>
          </div>
          <!-- Order Items -->
          <div>
            <h4 class="font-medium mb-2">Sản phẩm</h4>
            <div class="border rounded-lg">
              <div
                v-for="item in selectedOrder.items"
                :key="item.id"
                class="flex items-center p-4 border-b last:border-b-0"
              >
                <img
                  :src="item.image"
                  :alt="item.name"
                  class="w-16 h-16 object-cover rounded-lg"
                />
                <div class="ml-4 flex-1">
                  <h5 class="font-medium">{{ item.name }}</h5>
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
          </div>
          <!-- Order Summary -->
          <div class="bg-gray-50 p-4 rounded-lg">
            <div class="flex justify-between mb-2">
              <span>Tạm tính</span>
              <span>{{ formatPrice(selectedOrder.subtotal) }}</span>
            </div>
            <div class="flex justify-between mb-2">
              <span>Phí vận chuyển</span>
              <span>{{ formatPrice(selectedOrder.shippingFee) }}</span>
            </div>
            <div class="flex justify-between font-medium text-lg border-t pt-2 mt-2">
              <span>Tổng cộng</span>
              <span>{{ formatPrice(selectedOrder.total) }}</span>
            </div>
          </div>
          <!-- Order Status Timeline -->
          <div>
            <h4 class="font-medium mb-2">Trạng thái đơn hàng</h4>
            <div class="relative">
              <div class="absolute left-4 top-0 bottom-0 w-0.5 bg-gray-200"></div>
              <div
                v-for="(status) in orderStatuses"
                :key="status.value"
                class="relative pl-10 pb-4"
              >
                <div
                  :class="[
                    'absolute left-0 w-8 h-8 rounded-full flex items-center justify-center',
                    isStatusCompleted(selectedOrder.status, status.value)
                      ? 'bg-green-500 text-white'
                      : 'bg-gray-200 text-gray-600'
                  ]"
                >
                  <i class="pi pi-check"></i>
                </div>
                <div>
                  <p class="font-medium">{{ status.label }}</p>
                  <p class="text-sm text-gray-500">
                    {{ getStatusDate(selectedOrder, status.value) }}
                  </p>
                </div>
              </div>
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
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import Dialog from 'primevue/dialog';

interface OrderItem {
  id: number;
  name: string;
  price: number;
  quantity: number;
  image: string;
}

interface Order {
  id: number;
  customerName: string;
  customerPhone: string;
  shippingAddress: string;
  orderDate: string;
  status: string;
  items: OrderItem[];
  subtotal: number;
  shippingFee: number;
  total: number;
  statusHistory: {
    status: string;
    date: string;
  }[];
}

const orderStatuses = [
  { value: 'pending', label: 'Chờ xử lý' },
  { value: 'processing', label: 'Đang xử lý' },
  { value: 'shipped', label: 'Đang giao hàng' },
  { value: 'delivered', label: 'Đã giao hàng' },
  { value: 'cancelled', label: 'Đã hủy' },
  { value: 'refunded', label: 'Đã hoàn trả' },
];

const orders = ref<Order[]>([
  {
    id: 1,
    customerName: 'Nguyễn Văn A',
    customerPhone: '0123456789',
    shippingAddress: '123 Đường ABC, Quận XYZ, TP.HCM',
    orderDate: '2024-03-15T10:30:00',
    status: 'pending',
    items: [
      {
        id: 1,
        name: 'Sản phẩm 1',
        price: 100000,
        quantity: 2,
        image: 'https://via.placeholder.com/150',
      },
      {
        id: 2,
        name: 'Sản phẩm 2',
        price: 150000,
        quantity: 1,
        image: 'https://via.placeholder.com/150',
      },
    ],
    subtotal: 350000,
    shippingFee: 30000,
    total: 380000,
    statusHistory: [
      { status: 'pending', date: '2024-03-15T10:30:00' },
    ],
  },
  // Thêm dữ liệu mẫu khác nếu cần
]);

const searchQuery = ref('');
const sortBy = ref('date');
const currentStatus = ref('pending');
const showModal = ref(false);
const selectedOrder = ref<Order | null>(null);

const filteredOrders = computed(() => {
  let filtered = [...orders.value];
  
  // Lọc theo trạng thái
  if (currentStatus.value) {
    filtered = filtered.filter(order => order.status === currentStatus.value);
  }
  
  // Tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(order => 
      order.id.toString().includes(query) ||
      order.customerName.toLowerCase().includes(query) ||
      order.customerPhone.includes(query)
    );
  }
  
  // Sắp xếp
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'date':
        return new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime();
      case 'total':
        return b.total - a.total;
      case 'status':
        return a.status.localeCompare(b.status);
      default:
        return 0;
    }
  });
  
  return filtered;
});

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('vi-VN');
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getStatusLabel = (status: string) => {
  return orderStatuses.find(s => s.value === status)?.label || status;
};

const getStatusClass = (status: string) => {
  const classes = {
    pending: 'bg-yellow-100 text-yellow-800',
    processing: 'bg-blue-100 text-blue-800',
    shipped: 'bg-purple-100 text-purple-800',
    delivered: 'bg-green-100 text-green-800',
    cancelled: 'bg-red-100 text-red-800',
    refunded: 'bg-gray-100 text-gray-800',
  };
  return classes[status as keyof typeof classes] || 'bg-gray-100 text-gray-800';
};

const canProcessOrder = (order: Order) => {
  const statusFlow = ['pending', 'processing', 'shipped', 'delivered'];
  const currentIndex = statusFlow.indexOf(order.status);
  return currentIndex >= 0 && currentIndex < statusFlow.length - 1;
};

const getNextActionLabel = (status: string) => {
  const actions = {
    pending: 'Xác nhận đơn hàng',
    processing: 'Đóng gói và giao hàng',
    shipped: 'Xác nhận đã giao',
  };
  return actions[status as keyof typeof actions] || 'Cập nhật trạng thái';
};

const viewOrderDetails = (order: Order) => {
  selectedOrder.value = order;
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  selectedOrder.value = null;
};

const processOrder = (order: Order) => {
  const statusFlow = ['pending', 'processing', 'shipped', 'delivered'];
  const currentIndex = statusFlow.indexOf(order.status);
  
  if (currentIndex >= 0 && currentIndex < statusFlow.length - 1) {
    const nextStatus = statusFlow[currentIndex + 1];
    // TODO: Implement API call to update order status
    order.status = nextStatus;
    order.statusHistory.push({
      status: nextStatus,
      date: new Date().toISOString(),
    });
  }
};

const isStatusCompleted = (currentStatus: string, checkStatus: string) => {
  const statusFlow = ['pending', 'processing', 'shipped', 'delivered'];
  const currentIndex = statusFlow.indexOf(currentStatus);
  const checkIndex = statusFlow.indexOf(checkStatus);
  return checkIndex <= currentIndex;
};

const getStatusDate = (order: Order, status: string) => {
  const history = order.statusHistory.find(h => h.status === status);
  return history ? formatDate(history.date) : '-';
};
</script> 