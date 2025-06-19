<template>
  <div class="p-6">
    <!-- Support Summary -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Tổng số ticket</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.totalTickets }}</h3>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <i class="pi pi-ticket text-blue-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Đang chờ xử lý</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.pendingTickets }}</h3>
          </div>
          <div class="w-12 h-12 bg-yellow-100 rounded-full flex items-center justify-center">
            <i class="pi pi-clock text-yellow-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Đã xử lý hôm nay</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.resolvedToday }}</h3>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
            <i class="pi pi-check-circle text-green-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Thời gian phản hồi TB</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.avgResponseTime }} phút</h3>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
            <i class="pi pi-stopwatch text-purple-600 text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <!-- Ticket List -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b flex justify-between items-center">
        <h2 class="text-lg font-semibold">Danh sách yêu cầu hỗ trợ</h2>
        <div class="flex gap-4">
          <select
            v-model="filterStatus"
            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Tất cả trạng thái</option>
            <option value="pending">Đang chờ xử lý</option>
            <option value="in_progress">Đang xử lý</option>
            <option value="resolved">Đã xử lý</option>
            <option value="closed">Đã đóng</option>
          </select>
          <select
            v-model="filterType"
            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Tất cả loại</option>
            <option value="product">Sản phẩm</option>
            <option value="order">Đơn hàng</option>
            <option value="other">Khác</option>
          </select>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Tìm kiếm..."
            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="min-w-full divide-y divide-gray-200">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                ID
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Khách hàng
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Loại
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tiêu đề
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Trạng thái
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thời gian
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="ticket in filteredTickets" :key="ticket.id">
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                #{{ ticket.id }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <div class="flex-shrink-0 h-10 w-10">
                    <img
                      :src="ticket.customer.avatar"
                      :alt="ticket.customer.name"
                      class="h-10 w-10 rounded-full"
                    />
                  </div>
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">
                      {{ ticket.customer.name }}
                    </div>
                    <div class="text-sm text-gray-500">
                      {{ ticket.customer.email }}
                    </div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getTypeClass(ticket.type)
                  ]"
                >
                  {{ getTypeLabel(ticket.type) }}
                </span>
              </td>
              <td class="px-6 py-4">
                <div class="text-sm text-gray-900">{{ ticket.title }}</div>
                <div class="text-sm text-gray-500">{{ ticket.description }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getStatusClass(ticket.status)
                  ]"
                >
                  {{ getStatusLabel(ticket.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                {{ formatDate(ticket.createdAt) }}
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="viewTicketDetails(ticket)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  Xem chi tiết
                </button>
                <button
                  v-if="ticket.status === 'pending'"
                  @click="startProcessing(ticket)"
                  class="text-green-600 hover:text-green-900"
                >
                  Xử lý
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Ticket Details Modal -->
    <Dialog v-model:visible="showTicketModal" :modal="true" :closable="true" :style="{ width: '800px' }" :header="selectedTicket ? `Chi tiết yêu cầu hỗ trợ #${selectedTicket.id}` : ''">
      <div v-if="selectedTicket" class="space-y-4">
        <!-- Ticket Info -->
        <div class="bg-gray-50 p-4 rounded-lg">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <p class="text-sm text-gray-500">Khách hàng</p>
              <p class="font-medium">{{ selectedTicket.customer.name }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Email</p>
              <p class="font-medium">{{ selectedTicket.customer.email }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Loại yêu cầu</p>
              <p class="font-medium">{{ getTypeLabel(selectedTicket.type) }}</p>
            </div>
            <div>
              <p class="text-sm text-gray-500">Trạng thái</p>
              <p class="font-medium">{{ getStatusLabel(selectedTicket.status) }}</p>
            </div>
          </div>
        </div>

        <!-- Ticket Content -->
        <div>
          <h4 class="text-lg font-medium mb-2">{{ selectedTicket.title }}</h4>
          <p class="text-gray-600">{{ selectedTicket.description }}</p>
        </div>

        <!-- Conversation -->
        <div class="border-t pt-4">
          <h4 class="text-lg font-medium mb-4">Lịch sử hội thoại</h4>
          <div class="space-y-4">
            <div
              v-for="message in selectedTicket.messages"
              :key="message.id"
              :class="[
                'p-4 rounded-lg',
                message.isCustomer ? 'bg-gray-100' : 'bg-blue-50'
              ]"
            >
              <div class="flex justify-between items-start mb-2">
                <span class="font-medium">
                  {{ message.isCustomer ? selectedTicket.customer.name : 'Nhân viên hỗ trợ' }}
                </span>
                <span class="text-sm text-gray-500">
                  {{ formatDate(message.timestamp) }}
                </span>
              </div>
              <p class="text-gray-600">{{ message.content }}</p>
            </div>
          </div>
        </div>

        <!-- Reply Form -->
        <div class="border-t pt-4">
          <form @submit.prevent="sendReply" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Phản hồi</label>
              <textarea
                v-model="replyContent"
                rows="4"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              ></textarea>
            </div>
            <div class="flex justify-end gap-3">
              <button
                v-if="selectedTicket.status === 'in_progress'"
                @click="resolveTicket"
                type="button"
                class="px-4 py-2 bg-green-600 text-white rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                Đánh dấu đã xử lý
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                Gửi phản hồi
              </button>
            </div>
          </form>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import Dialog from 'primevue/dialog';

interface Customer {
  id: number;
  name: string;
  email: string;
  avatar: string;
}

interface Message {
  id: number;
  content: string;
  timestamp: string;
  isCustomer: boolean;
}

interface Ticket {
  id: number;
  customer: Customer;
  type: 'product' | 'order' | 'other';
  title: string;
  description: string;
  status: 'pending' | 'in_progress' | 'resolved' | 'closed';
  createdAt: string;
  messages: Message[];
}

interface Summary {
  totalTickets: number;
  pendingTickets: number;
  resolvedToday: number;
  avgResponseTime: number;
}

const summary = ref<Summary>({
  totalTickets: 150,
  pendingTickets: 25,
  resolvedToday: 12,
  avgResponseTime: 45,
});

const tickets = ref<Ticket[]>([
  {
    id: 1,
    customer: {
      id: 1,
      name: 'Nguyễn Văn A',
      email: 'nguyenvana@example.com',
      avatar: 'https://via.placeholder.com/150',
    },
    type: 'product',
    title: 'Sản phẩm không đúng mô tả',
    description: 'Tôi đã nhận được sản phẩm nhưng không giống với mô tả trên website.',
    status: 'pending',
    createdAt: '2024-03-15T10:30:00',
    messages: [
      {
        id: 1,
        content: 'Tôi đã nhận được sản phẩm nhưng không giống với mô tả trên website.',
        timestamp: '2024-03-15T10:30:00',
        isCustomer: true,
      },
    ],
  },
  {
    id: 2,
    customer: {
      id: 2,
      name: 'Trần Thị B',
      email: 'tranthib@example.com',
      avatar: 'https://via.placeholder.com/150',
    },
    type: 'order',
    title: 'Đơn hàng bị trễ',
    description: 'Đơn hàng của tôi đã quá hạn giao hàng 3 ngày.',
    status: 'in_progress',
    createdAt: '2024-03-14T15:45:00',
    messages: [
      {
        id: 1,
        content: 'Đơn hàng của tôi đã quá hạn giao hàng 3 ngày.',
        timestamp: '2024-03-14T15:45:00',
        isCustomer: true,
      },
      {
        id: 2,
        content: 'Chúng tôi đang kiểm tra tình trạng đơn hàng của bạn.',
        timestamp: '2024-03-14T16:00:00',
        isCustomer: false,
      },
    ],
  },
]);

const filterStatus = ref('');
const filterType = ref('');
const searchQuery = ref('');
const showTicketModal = ref(false);
const selectedTicket = ref<Ticket | null>(null);
const replyContent = ref('');

const filteredTickets = computed(() => {
  return tickets.value.filter(ticket => {
    const matchesStatus = !filterStatus.value || ticket.status === filterStatus.value;
    const matchesType = !filterType.value || ticket.type === filterType.value;
    const matchesSearch = !searchQuery.value || 
      ticket.title.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      ticket.description.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      ticket.customer.name.toLowerCase().includes(searchQuery.value.toLowerCase());
    
    return matchesStatus && matchesType && matchesSearch;
  });
});

const getTypeLabel = (type: string) => {
  const labels = {
    product: 'Sản phẩm',
    order: 'Đơn hàng',
    other: 'Khác',
  };
  return labels[type as keyof typeof labels] || type;
};

const getTypeClass = (type: string) => {
  const classes = {
    product: 'bg-blue-100 text-blue-800',
    order: 'bg-green-100 text-green-800',
    other: 'bg-gray-100 text-gray-800',
  };
  return classes[type as keyof typeof classes] || 'bg-gray-100 text-gray-800';
};

const getStatusLabel = (status: string) => {
  const labels = {
    pending: 'Đang chờ xử lý',
    in_progress: 'Đang xử lý',
    resolved: 'Đã xử lý',
    closed: 'Đã đóng',
  };
  return labels[status as keyof typeof labels] || status;
};

const getStatusClass = (status: string) => {
  const classes = {
    pending: 'bg-yellow-100 text-yellow-800',
    in_progress: 'bg-blue-100 text-blue-800',
    resolved: 'bg-green-100 text-green-800',
    closed: 'bg-gray-100 text-gray-800',
  };
  return classes[status as keyof typeof classes] || 'bg-gray-100 text-gray-800';
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const viewTicketDetails = (ticket: Ticket) => {
  selectedTicket.value = ticket;
  showTicketModal.value = true;
};

const closeTicketModal = () => {
  showTicketModal.value = false;
  selectedTicket.value = null;
  replyContent.value = '';
};

const startProcessing = (ticket: Ticket) => {
  // TODO: Implement start processing
  console.log('Start processing ticket:', ticket);
};

const sendReply = () => {
  if (!selectedTicket.value || !replyContent.value) return;

  // TODO: Implement send reply
  console.log('Send reply:', {
    ticketId: selectedTicket.value.id,
    content: replyContent.value,
  });

  replyContent.value = '';
};

const resolveTicket = () => {
  if (!selectedTicket.value) return;

  // TODO: Implement resolve ticket
  console.log('Resolve ticket:', selectedTicket.value.id);
};
</script> 