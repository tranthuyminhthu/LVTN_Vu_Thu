<template>
  <div class="p-6">
    <!-- Summary Cards -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Tổng doanh thu</p>
            <h3 class="text-2xl font-semibold mt-1">{{ formatPrice(summary.totalRevenue) }}</h3>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <i class="pi pi-dollar text-blue-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="pi pi-arrow-up"></i>
            {{ summary.revenueGrowth }}% so với tháng trước
          </span>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Hoa hồng phải trả</p>
            <h3 class="text-2xl font-semibold mt-1">{{ formatPrice(summary.totalCommission) }}</h3>
          </div>
          <div class="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center">
            <i class="pi pi-percentage text-red-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-gray-500">
            {{ summary.commissionRate }}% trên mỗi đơn hàng
          </span>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Số dư hiện tại</p>
            <h3 class="text-2xl font-semibold mt-1">{{ formatPrice(summary.currentBalance) }}</h3>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-full flex items-center justify-center">
            <i class="pi pi-wallet text-green-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <button
            @click="requestWithdrawal"
            class="text-sm text-blue-600 hover:text-blue-800"
          >
            Yêu cầu rút tiền
          </button>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Đơn hàng thành công</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.successfulOrders }}</h3>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-full flex items-center justify-center">
            <i class="pi pi-check-circle text-purple-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-4">
          <span class="text-sm text-green-600">
            <i class="pi pi-arrow-up"></i>
            {{ summary.orderGrowth }}% so với tháng trước
          </span>
        </div>
      </div>
    </div>

    <!-- Revenue Chart -->
    <div class="bg-white rounded-lg p-6 shadow-sm mb-6">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-lg font-semibold">Biểu đồ doanh thu</h2>
        <select
          v-model="chartPeriod"
          class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
        >
          <option value="week">Tuần này</option>
          <option value="month">Tháng này</option>
          <option value="year">Năm nay</option>
        </select>
      </div>
      <div class="h-80">
        <Chart type="bar" :data="chartData" :options="chartOptions" style="height: 100%" />
      </div>
    </div>

    <!-- Transaction History -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b">
        <h2 class="text-lg font-semibold">Lịch sử giao dịch</h2>
      </div>
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ngày
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Mã giao dịch
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Loại
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Số tiền
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Trạng thái
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Ghi chú
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="transaction in transactions" :key="transaction.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatDate(transaction.date) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">#{{ transaction.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getTransactionTypeClass(transaction.type)
                  ]"
                >
                  {{ getTransactionTypeLabel(transaction.type) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div
                  :class="[
                    'text-sm font-medium',
                    transaction.type === 'income' ? 'text-green-600' : 'text-red-600'
                  ]"
                >
                  {{ transaction.type === 'income' ? '+' : '-' }}{{ formatPrice(transaction.amount) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-2 py-1 text-xs font-medium rounded-full',
                    getStatusClass(transaction.status)
                  ]"
                >
                  {{ getStatusLabel(transaction.status) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-500">{{ transaction.note }}</div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Withdrawal Request Modal -->
    <div v-if="showWithdrawalModal" class="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full">
      <div class="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
        <div class="mt-3">
          <h3 class="text-lg font-medium leading-6 text-gray-900 mb-4">
            Yêu cầu rút tiền
          </h3>
          <form @submit.prevent="submitWithdrawalRequest" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700">Số tiền</label>
              <input
                v-model="withdrawalAmount"
                type="number"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
                :max="summary.currentBalance"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Phương thức nhận tiền</label>
              <select
                v-model="withdrawalMethod"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              >
                <option value="bank">Chuyển khoản ngân hàng</option>
                <option value="momo">Ví MoMo</option>
                <option value="zalopay">Ví ZaloPay</option>
              </select>
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700">Thông tin nhận tiền</label>
              <input
                v-model="withdrawalInfo"
                type="text"
                class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
                placeholder="Số tài khoản / Số điện thoại"
              />
            </div>
            <div class="flex justify-end gap-3 mt-4">
              <button
                type="button"
                @click="closeWithdrawalModal"
                class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                Gửi yêu cầu
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import Chart from 'primevue/chart';

interface Summary {
  totalRevenue: number;
  revenueGrowth: number;
  totalCommission: number;
  commissionRate: number;
  currentBalance: number;
  successfulOrders: number;
  orderGrowth: number;
}

interface Transaction {
  id: number;
  date: string;
  type: 'income' | 'expense';
  amount: number;
  status: 'pending' | 'completed' | 'failed';
  note: string;
}

const summary = ref<Summary>({
  totalRevenue: 15000000,
  revenueGrowth: 15,
  totalCommission: 1500000,
  commissionRate: 10,
  currentBalance: 13500000,
  successfulOrders: 150,
  orderGrowth: 20,
});

const chartPeriod = ref('month');

const transactions = ref<Transaction[]>([
  {
    id: 1,
    date: '2024-03-15T10:30:00',
    type: 'income',
    amount: 1000000,
    status: 'completed',
    note: 'Thanh toán đơn hàng #123',
  },
  {
    id: 2,
    date: '2024-03-14T15:45:00',
    type: 'expense',
    amount: 100000,
    status: 'completed',
    note: 'Hoa hồng hệ thống',
  },
  // Thêm dữ liệu mẫu khác nếu cần
]);

const showWithdrawalModal = ref(false);
const withdrawalAmount = ref(0);
const withdrawalMethod = ref('bank');
const withdrawalInfo = ref('');

const chartData = ref({
  labels: ['Tuần 1', 'Tuần 2', 'Tuần 3', 'Tuần 4'],
  datasets: [
    {
      label: 'Doanh thu',
      backgroundColor: '#3b82f6',
      data: [5000000, 7000000, 6000000, 8000000]
    }
  ]
});

const chartOptions = ref({
  responsive: true,
  plugins: {
    legend: {
      display: true,
      position: 'top'
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value: number) {
          return value.toLocaleString('vi-VN') + ' đ';
        }
      }
    }
  }
});

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleString('vi-VN');
};

const getTransactionTypeLabel = (type: string) => {
  return type === 'income' ? 'Thu nhập' : 'Chi phí';
};

const getTransactionTypeClass = (type: string) => {
  return type === 'income'
    ? 'bg-green-100 text-green-800'
    : 'bg-red-100 text-red-800';
};

const getStatusLabel = (status: string) => {
  const labels = {
    pending: 'Đang xử lý',
    completed: 'Hoàn thành',
    failed: 'Thất bại',
  };
  return labels[status as keyof typeof labels] || status;
};

const getStatusClass = (status: string) => {
  const classes = {
    pending: 'bg-yellow-100 text-yellow-800',
    completed: 'bg-green-100 text-green-800',
    failed: 'bg-red-100 text-red-800',
  };
  return classes[status as keyof typeof classes] || 'bg-gray-100 text-gray-800';
};

const requestWithdrawal = () => {
  showWithdrawalModal.value = true;
};

const closeWithdrawalModal = () => {
  showWithdrawalModal.value = false;
  withdrawalAmount.value = 0;
  withdrawalMethod.value = 'bank';
  withdrawalInfo.value = '';
};

const submitWithdrawalRequest = () => {
  // TODO: Implement API call to submit withdrawal request
  console.log('Withdrawal request:', {
    amount: withdrawalAmount.value,
    method: withdrawalMethod.value,
    info: withdrawalInfo.value,
  });
  closeWithdrawalModal();
};
</script> 