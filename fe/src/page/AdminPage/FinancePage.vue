<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <p class="text-2xl font-bold mb-4">Quản lý tài chính</p>

    <!-- Tổng quan tài chính -->
    <div class="grid grid-cols-4 gap-4 mb-6">
      <div class="bg-white rounded-lg p-4 shadow-sm border">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Tổng doanh thu</span>
            <span class="text-xl font-bold">{{ formatCurrency(totalRevenue) }}</span>
          </div>
          <div class="rounded-full p-2 w-12 h-12 flex items-center justify-center bg-green-100">
            <i class="pi pi-wallet text-green-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-2">
          <span :class="revenueGrowth >= 0 ? 'text-green-500' : 'text-red-500'">
            {{ revenueGrowth >= 0 ? '+' : '' }}{{ revenueGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">So với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-lg p-4 shadow-sm border">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Chi phí</span>
            <span class="text-xl font-bold">{{ formatCurrency(totalExpenses) }}</span>
          </div>
          <div class="rounded-full p-2 w-12 h-12 flex items-center justify-center bg-red-100">
            <i class="pi pi-money-bill text-red-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-2">
          <span :class="expensesGrowth >= 0 ? 'text-red-500' : 'text-green-500'">
            {{ expensesGrowth >= 0 ? '+' : '' }}{{ expensesGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">So với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-lg p-4 shadow-sm border">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Lợi nhuận</span>
            <span class="text-xl font-bold">{{ formatCurrency(totalProfit) }}</span>
          </div>
          <div class="rounded-full p-2 w-12 h-12 flex items-center justify-center bg-blue-100">
            <i class="pi pi-chart-line text-blue-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-2">
          <span :class="profitGrowth >= 0 ? 'text-green-500' : 'text-red-500'">
            {{ profitGrowth >= 0 ? '+' : '' }}{{ profitGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">So với tháng trước</span>
        </div>
      </div>

      <div class="bg-white rounded-lg p-4 shadow-sm border">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1">
            <span class="text-sm text-gray-500">Hoa hồng</span>
            <span class="text-xl font-bold">{{ formatCurrency(totalCommission) }}</span>
          </div>
          <div class="rounded-full p-2 w-12 h-12 flex items-center justify-center bg-purple-100">
            <i class="pi pi-percentage text-purple-600 text-xl"></i>
          </div>
        </div>
        <div class="mt-2">
          <span :class="commissionGrowth >= 0 ? 'text-green-500' : 'text-red-500'">
            {{ commissionGrowth >= 0 ? '+' : '' }}{{ commissionGrowth }}%
          </span>
          <span class="text-sm text-gray-500 ml-2">So với tháng trước</span>
        </div>
      </div>
    </div>

    <!-- Biểu đồ và báo cáo -->
    <div class="grid grid-cols-3 gap-4 mb-6">
      <div class="col-span-2 bg-white rounded-lg p-4 shadow-sm border">
        <h3 class="text-lg font-semibold mb-4">Xu hướng doanh thu</h3>
        <Chart type="line" :data="revenueChartData" :options="chartOptions" class="h-[300px]" />
      </div>
      <div class="bg-white rounded-lg p-4 shadow-sm border">
        <h3 class="text-lg font-semibold mb-4">Phân bố doanh thu</h3>
        <Chart type="pie" :data="revenueDistributionData" :options="pieChartOptions" class="h-[300px]" />
      </div>
    </div>

    <!-- Bảng giao dịch -->
    <div class="bg-white rounded-lg p-4 shadow-sm border">
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-lg font-semibold">Giao dịch gần đây</h3>
        <div class="flex gap-2">
          <Select v-model="selectedTransactionType" :options="transactionTypes" optionLabel="name" placeholder="Loại giao dịch" class="w-48" />
          <Calendar v-model="dateRange" selectionMode="range" :showIcon="true" placeholder="Chọn khoảng thời gian" class="w-64" />
        </div>
      </div>
      <DataTable
        :value="transactions"
        :paginator="true"
        :rows="10"
        :rowsPerPageOptions="[5, 10, 20, 50]"
        tableStyle="min-width: 50rem"
      >
        <Column field="id" header="Mã giao dịch" sortable></Column>
        <Column field="date" header="Ngày" sortable>
          <template #body="slotProps">
            {{ formatDate(slotProps.data.date) }}
          </template>
        </Column>
        <Column field="type" header="Loại" sortable>
          <template #body="slotProps">
            <Tag :severity="getTransactionTypeSeverity(slotProps.data.type)" :value="slotProps.data.type" />
          </template>
        </Column>
        <Column field="amount" header="Số tiền" sortable>
          <template #body="slotProps">
            <span :class="slotProps.data.type === 'Thu' ? 'text-green-600' : 'text-red-600'">
              {{ formatCurrency(slotProps.data.amount) }}
            </span>
          </template>
        </Column>
        <Column field="description" header="Mô tả"></Column>
        <Column field="status" header="Trạng thái" sortable>
          <template #body="slotProps">
            <Tag :severity="getTransactionStatusSeverity(slotProps.data.status)" :value="slotProps.data.status" />
          </template>
        </Column>
        <Column header="Thao tác">
          <template #body="slotProps">
            <div class="flex gap-2">
              <Button icon="pi pi-eye" severity="info" @click="viewTransactionDetails(slotProps.data)" />
              <Button icon="pi pi-file-pdf" severity="secondary" @click="exportTransaction(slotProps.data)" />
            </div>
          </template>
        </Column>
      </DataTable>
    </div>

    <!-- Dialog chi tiết giao dịch -->
    <Dialog v-model:visible="transactionDetailsVisible" modal header="Chi tiết giao dịch" :style="{ width: '50vw' }">
      <div v-if="selectedTransaction" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin giao dịch</p>
            <p>Mã giao dịch: {{ selectedTransaction.id }}</p>
            <p>Ngày: {{ formatDate(selectedTransaction.date) }}</p>
            <p>Loại: <Tag :severity="getTransactionTypeSeverity(selectedTransaction.type)" :value="selectedTransaction.type" /></p>
            <p>Số tiền: {{ formatCurrency(selectedTransaction.amount) }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin bổ sung</p>
            <p>Trạng thái: <Tag :severity="getTransactionStatusSeverity(selectedTransaction.status)" :value="selectedTransaction.status" /></p>
            <p>Mô tả: {{ selectedTransaction.description }}</p>
            <p v-if="selectedTransaction.relatedTransaction">Giao dịch liên quan: {{ selectedTransaction.relatedTransaction }}</p>
          </div>
        </div>
        <Divider />
        <p class="font-bold mb-2">Lịch sử giao dịch</p>
        <DataTable :value="selectedTransaction.history" class="mt-2">
          <Column field="date" header="Thời gian">
            <template #body="slotProps">
              {{ formatDate(slotProps.data.date) }}
            </template>
          </Column>
          <Column field="action" header="Hành động"></Column>
          <Column field="user" header="Người thực hiện"></Column>
        </DataTable>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import Chart from 'primevue/chart';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import Dialog from 'primevue/dialog';
import Tag from 'primevue/tag';
import Select from 'primevue/select';
import Calendar from 'primevue/calendar';
import Divider from 'primevue/divider';

interface Transaction {
  id: string;
  date: Date;
  type: string;
  amount: number;
  description: string;
  status: string;
  history: {
    date: Date;
    action: string;
    user: string;
  }[];
  relatedTransaction?: string;
}

// Dữ liệu tổng quan
const totalRevenue = ref(1500000000);
const totalExpenses = ref(500000000);
const totalProfit = ref(1000000000);
const totalCommission = ref(150000000);

const revenueGrowth = ref(15);
const expensesGrowth = ref(-5);
const profitGrowth = ref(25);
const commissionGrowth = ref(10);

// Dữ liệu biểu đồ
const revenueChartData = ref();
const revenueDistributionData = ref();
const chartOptions = ref();
const pieChartOptions = ref();

// Dữ liệu giao dịch
const transactions = ref([
  {
    id: 'TXN001',
    date: new Date('2024-03-15'),
    type: 'Thu',
    amount: 1500000,
    description: 'Thanh toán đơn hàng #ORD001',
    status: 'Hoàn thành',
    history: [
      { date: new Date('2024-03-15 10:00'), action: 'Tạo giao dịch', user: 'System' },
      { date: new Date('2024-03-15 10:05'), action: 'Xác nhận thanh toán', user: 'Admin' }
    ]
  },
  {
    id: 'TXN002',
    date: new Date('2024-03-14'),
    type: 'Chi',
    amount: 500000,
    description: 'Hoa hồng nhà cung cấp #V001',
    status: 'Đang xử lý',
    history: [
      { date: new Date('2024-03-14 15:00'), action: 'Tạo giao dịch', user: 'System' },
      { date: new Date('2024-03-14 15:30'), action: 'Chờ xác nhận', user: 'Admin' }
    ]
  }
]);

const transactionTypes = [
  { name: 'Tất cả', value: 'all' },
  { name: 'Thu', value: 'Thu' },
  { name: 'Chi', value: 'Chi' }
];

const selectedTransactionType = ref(transactionTypes[0]);
const dateRange = ref(null);
const transactionDetailsVisible = ref(false);
const selectedTransaction = ref<Transaction | null>(null);

// Hàm format tiền tệ
const formatCurrency = (value: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(value);
};

// Hàm format ngày tháng
const formatDate = (date: Date) => {
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

// Hàm lấy màu cho loại giao dịch
const getTransactionTypeSeverity = (type: string) => {
  return type === 'Thu' ? 'success' : 'danger';
};

// Hàm lấy màu cho trạng thái giao dịch
const getTransactionStatusSeverity = (status: string) => {
  switch (status) {
    case 'Hoàn thành':
      return 'success';
    case 'Đang xử lý':
      return 'warning';
    case 'Thất bại':
      return 'danger';
    default:
      return 'info';
  }
};

// Hàm xem chi tiết giao dịch
const viewTransactionDetails = (transaction: Transaction) => {
  selectedTransaction.value = transaction;
  transactionDetailsVisible.value = true;
};

// Hàm xuất giao dịch
const exportTransaction = (transaction: Transaction) => {
  // TODO: Implement export functionality
  console.log('Export transaction:', transaction);
};

// Khởi tạo dữ liệu biểu đồ
onMounted(() => {
  const documentStyle = getComputedStyle(document.documentElement);
  
  // Biểu đồ doanh thu
  revenueChartData.value = {
    labels: ['Tháng 1', 'Tháng 2', 'Tháng 3', 'Tháng 4', 'Tháng 5', 'Tháng 6'],
    datasets: [
      {
        label: 'Doanh thu',
        data: [65, 59, 80, 81, 56, 55],
        fill: false,
        borderColor: documentStyle.getPropertyValue('--green-500'),
        tension: 0.4
      },
      {
        label: 'Chi phí',
        data: [28, 48, 40, 19, 86, 27],
        fill: false,
        borderColor: documentStyle.getPropertyValue('--red-500'),
        tension: 0.4
      }
    ]
  };

  // Biểu đồ phân bố doanh thu
  revenueDistributionData.value = {
    labels: ['Sản phẩm', 'Dịch vụ', 'Hoa hồng', 'Khác'],
    datasets: [
      {
        data: [40, 30, 20, 10],
        backgroundColor: [
          documentStyle.getPropertyValue('--blue-500'),
          documentStyle.getPropertyValue('--yellow-500'),
          documentStyle.getPropertyValue('--green-500'),
          documentStyle.getPropertyValue('--red-500')
        ]
      }
    ]
  };

  // Cấu hình biểu đồ
  chartOptions.value = {
    maintainAspectRatio: false,
    aspectRatio: 0.6,
    plugins: {
      legend: {
        labels: {
          color: documentStyle.getPropertyValue('--text-color')
        }
      }
    },
    scales: {
      x: {
        ticks: {
          color: documentStyle.getPropertyValue('--text-color-secondary')
        },
        grid: {
          color: documentStyle.getPropertyValue('--surface-border')
        }
      },
      y: {
        ticks: {
          color: documentStyle.getPropertyValue('--text-color-secondary')
        },
        grid: {
          color: documentStyle.getPropertyValue('--surface-border')
        }
      }
    }
  };

  pieChartOptions.value = {
    plugins: {
      legend: {
        labels: {
          color: documentStyle.getPropertyValue('--text-color')
        }
      }
    }
  };
});
</script>

<style scoped>
.p-datatable .p-datatable-tbody > tr > td {
  padding: 1rem;
}
</style> 