<template>
  <div class="p-6 bg-gray-50 min-h-screen">
    <!-- Header Section -->
    <div class="mb-8">
      <div class="flex items-center space-x-3 mb-2">
        <div
          class="!w-10 !h-10 rounded-xl flex items-center justify-center mr-2 border border-gray-200"
        >
          <i class="pi pi-wallet text-lg text-black"></i>
        </div>
        <div>
          <h1 class="text-3xl font-bold text-gray-800">Quản lý tài chính</h1>
          <p class="text-gray-500">
            Theo dõi doanh thu, chi phí và giao dịch của bạn
          </p>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="isLoading" class="flex items-center justify-center py-12">
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"
        ></div>
        <p class="text-gray-600">{{ message }}</p>
      </div>
    </div>

    <!-- Summary Cards -->
    <div
      v-else
      class="!grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8"
    >
      <!-- Total Revenue Card -->
      <div
        class="bg-gradient-to-br from-blue-500 to-blue-600 rounded-2xl p-6 text-white shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
      >
        <div class="flex items-center justify-between mb-4">
          <div
            class="!w-12 !h-12 bg-white/20 rounded-xl flex items-center justify-center"
          >
            <i class="pi pi-dollar text-white text-xl"></i>
          </div>
        </div>
        <h3 class="text-2xl font-bold mb-1">
          {{ formatPrice(summary.totalRevenue) }}
        </h3>
        <p class="text-blue-100 text-sm">Tổng doanh thu</p>
      </div>

      <!-- Commission Card -->
      <div
        class="bg-gradient-to-br from-red-500 to-red-600 rounded-2xl p-6 text-white shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
      >
        <div class="flex items-center justify-between mb-4">
          <div
            class="!w-12 !h-12 bg-white/20 rounded-xl flex items-center justify-center"
          >
            <i class="pi pi-percentage text-white text-xl"></i>
          </div>
        </div>
        <h3 class="text-2xl font-bold mb-1">
          {{ formatPrice(summary.totalCommission) }}
        </h3>
        <p class="text-red-100 text-sm">Hoa hồng phải trả</p>
      </div>

      <!-- Current Balance Card -->
      <div
        class="bg-gradient-to-br from-green-500 to-green-600 rounded-2xl p-6 text-white shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
      >
        <div class="flex items-center justify-between mb-4">
          <div
            class="!w-12 !h-12 bg-white/20 rounded-xl flex items-center justify-center"
          >
            <i class="pi pi-wallet text-white text-xl"></i>
          </div>
        </div>
        <h3 class="text-2xl font-bold mb-1">
          {{ formatPrice(summary.currentBalance) }}
        </h3>
        <p class="text-green-100 text-sm">Số dư hiện tại</p>
      </div>

      <!-- Successful Orders Card -->
      <div
        class="bg-gradient-to-br from-purple-500 to-purple-600 rounded-2xl p-6 text-white shadow-lg hover:shadow-xl transition-all duration-300 transform hover:-translate-y-1"
      >
        <div class="flex items-center justify-between mb-4">
          <div
            class="!w-12 !h-12 bg-white/20 rounded-xl flex items-center justify-center"
          >
            <i class="pi pi-check-circle text-white text-xl"></i>
          </div>
        </div>
        <h3 class="text-2xl font-bold mb-1">{{ summary.successfulOrders }}</h3>
        <p class="text-purple-100 text-sm">Đơn hàng thành công</p>
      </div>
    </div>

    <!-- Charts Section -->
    <div v-if="!isLoading" class="!grid grid-cols-1 lg:grid-cols-12 gap-8 mb-8">
      <!-- Revenue Chart -->
      <!-- <div class="bg-white rounded-2xl p-6 shadow-lg border border-gray-100">
        <div class="flex justify-between items-center mb-6">
          <div class="flex items-center !space-x-3">
            <div class="!w-8 !h-8 bg-blue-100 rounded-lg flex items-center justify-center">
              <i class="pi pi-chart-bar text-blue-600"></i>
            </div>
            <h2 class="text-xl font-bold text-gray-800">Biểu đồ doanh thu</h2>
          </div>
          <select
            v-model="chartPeriod"
            class="px-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 bg-white"
          >
            <option value="week">Tuần này</option>
            <option value="month">Tháng này</option>
            <option value="year">Năm nay</option>
          </select>
        </div>
        <div class="h-80">
          <Chart type="bar" :data="chartData" :options="chartOptions" style="height: 100%" />
        </div>
      </div> -->
      <div
        v-if="!isLoading && totalStats?.topProducts?.length"
        class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden mb-8 col-span-8"
      >
        <div class="p-6 border-b border-gray-100">
          <div class="flex items-center !space-x-3">
            <div
              class="!w-8 !h-8 bg-orange-100 rounded-lg flex items-center justify-center"
            >
              <i class="pi pi-star text-orange-600"></i>
            </div>
            <h2 class="text-xl font-bold text-gray-800">Sản phẩm bán chạy</h2>
          </div>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th
                  class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Sản phẩm
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  SKU
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Số lượng bán
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
                >
                  Doanh thu
                </th>
              </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-100">
              <tr
                v-for="product in totalStats.topProducts"
                :key="product.productSku"
                class="hover:bg-gray-50 transition-colors"
              >
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">
                    {{ product.productName }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm text-gray-500 font-mono">
                    {{ product.productSku }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-medium text-gray-900">
                    {{ product.quantitySold }}
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="text-sm font-bold text-green-600">
                    {{ formatPrice(product.totalRevenue) }}
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <!-- Transaction Summary -->
      <div class="bg-white rounded-2xl p-6 shadow-lg border border-gray-100 col-span-4">
        <div class="flex items-center !space-x-3 mb-6">
          <div
            class="!w-8 !h-8 bg-green-100 rounded-lg flex items-center justify-center"
          >
            <i class="pi pi-chart-pie text-green-600"></i>
          </div>
          <h2 class="text-xl font-bold text-gray-800">Tóm tắt giao dịch</h2>
        </div>

        <div class="!space-y-4">
          <div
            class="flex items-center justify-between p-4 bg-green-50 rounded-xl"
          >
            <div class="flex items-center !space-x-3">
              <div
                class="!w-10 !h-10 bg-green-100 rounded-lg flex items-center justify-center"
              >
                <i class="pi pi-arrow-down text-green-600"></i>
              </div>
              <div>
                <p class="font-medium text-gray-800">Thu nhập</p>
                <p class="text-sm text-gray-500">Tháng này</p>
              </div>
            </div>
            <span class="text-lg font-bold text-green-600"
              >+{{ formatPrice(summary.totalRevenue) }}</span
            >
          </div>

          <div
            class="flex items-center justify-between p-4 bg-red-50 rounded-xl"
          >
            <div class="flex items-center !space-x-3">
              <div
                class="!w-10 !h-10 bg-red-100 rounded-lg flex items-center justify-center"
              >
                <i class="pi pi-arrow-up text-red-600"></i>
              </div>
              <div>
                <p class="font-medium text-gray-800">Chi phí</p>
                <p class="text-sm text-gray-500">Tháng này</p>
              </div>
            </div>
            <span class="text-lg font-bold text-red-600"
              >-{{ formatPrice(summary.totalCommission) }}</span
            >
          </div>

          <div
            class="flex items-center justify-between p-4 bg-blue-50 rounded-xl"
          >
            <div class="flex items-center !space-x-3">
              <div
                class="!w-10 !h-10 bg-blue-100 rounded-lg flex items-center justify-center"
              >
                <i class="pi pi-wallet text-blue-600"></i>
              </div>
              <div>
                <p class="font-medium text-gray-800">Lợi nhuận</p>
                <p class="text-sm text-gray-500">Tháng này</p>
              </div>
            </div>
            <span class="text-lg font-bold text-blue-600">{{
              formatPrice(summary.currentBalance)
            }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Transaction History -->
    <!-- <div v-if="!isLoading" class="bg-white rounded-2xl shadow-lg border border-gray-100 overflow-hidden">
      <div class="p-6 border-b border-gray-100">
        <div class="flex items-center justify-between">
          <div class="flex items-center !space-x-3">
            <div class="!w-8 !h-8 bg-purple-100 rounded-lg flex items-center justify-center">
              <i class="pi pi-list text-purple-600"></i>
            </div>
            <h2 class="text-xl font-bold text-gray-800">Lịch sử giao dịch</h2>
          </div>
          <div class="flex !space-x-2">
            <button class="px-4 py-2 text-sm bg-gray-100 text-gray-600 rounded-lg hover:bg-gray-200 transition-colors">
              <i class="pi pi-filter mr-2"></i>Lọc
            </button>
            <button class="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              <i class="pi pi-download mr-2"></i>Xuất
            </button>
          </div>
        </div>
      </div>
      
      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Ngày
              </th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Mã giao dịch
              </th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Loại
              </th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Số tiền
              </th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Trạng thái
              </th>
              <th class="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Ghi chú
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-100">
            <tr v-for="transaction in transactions" :key="transaction.id" class="hover:bg-gray-50 transition-colors">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm font-medium text-gray-900">{{ formatDate(transaction.date) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900 font-mono">#{{ transaction.id }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap flex items-center justify-center">
                <span
                  :class="[
                    'px-3 py-1 text-xs font-semibold rounded-full',
                    getTransactionTypeClass(transaction.type)
                  ]"
                >
                  <i :class="transaction.type === 'income' ? 'pi pi-arrow-down' : 'pi pi-arrow-up'" class="mr-1"></i>
                  {{ getTransactionTypeLabel(transaction.type) }}
                </span>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div
                  :class="[
                    'text-sm font-bold',
                    transaction.type === 'income' ? 'text-green-600' : 'text-red-600'
                  ]"
                >
                  {{ transaction.type === 'income' ? '+' : '-' }}{{ formatPrice(transaction.amount) }}
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <span
                  :class="[
                    'px-3 py-1 text-xs font-semibold rounded-full',
                    getStatusClass(transaction.status)
                  ]"
                >
                  <i :class="getStatusIcon(transaction.status)" class="mr-1"></i>
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
    </div> -->

    <!-- Top Products Section -->

    <!-- Withdrawal Request Modal -->
    <div
      v-if="showWithdrawalModal"
      class="fixed inset-0 bg-black bg-opacity-50 overflow-y-auto h-full w-full z-50"
    >
      <div
        class="relative top-20 mx-auto p-8 border w-full max-w-md shadow-2xl rounded-2xl bg-white"
      >
        <div class="text-center mb-6">
          <div
            class="!w-16 !h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4"
          >
            <i class="pi pi-wallet text-blue-600 text-2xl"></i>
          </div>
          <h3 class="text-xl font-bold text-gray-900 mb-2">Yêu cầu rút tiền</h3>
          <p class="text-gray-500">Nhập thông tin để rút tiền từ tài khoản</p>
        </div>

        <form @submit.prevent="submitWithdrawalRequest" class="space-y-6">
          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2"
              >Số tiền</label
            >
            <div class="relative">
              <span
                class="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400"
                >₫</span
              >
              <input
                v-model="withdrawalAmount"
                type="number"
                class="w-full pl-8 pr-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                required
                :max="summary.currentBalance"
                placeholder="Nhập số tiền"
              />
            </div>
            <p class="text-xs text-gray-500 mt-1">
              Số dư khả dụng: {{ formatPrice(summary.currentBalance) }}
            </p>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2"
              >Phương thức nhận tiền</label
            >
            <select
              v-model="withdrawalMethod"
              class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
            >
              <option value="bank">🏦 Chuyển khoản ngân hàng</option>
              <option value="momo">📱 Ví MoMo</option>
              <option value="zalopay">📱 Ví ZaloPay</option>
            </select>
          </div>

          <div>
            <label class="block text-sm font-semibold text-gray-700 mb-2"
              >Thông tin nhận tiền</label
            >
            <input
              v-model="withdrawalInfo"
              type="text"
              class="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              required
              placeholder="Số tài khoản / Số điện thoại"
            />
          </div>

          <div class="flex space-x-3 pt-4">
            <button
              type="button"
              @click="closeWithdrawalModal"
              class="flex-1 px-4 py-3 bg-gray-100 text-gray-700 rounded-xl hover:bg-gray-200 focus:outline-none focus:ring-2 focus:ring-gray-500 font-medium transition-colors"
            >
              Hủy
            </button>
            <button
              type="submit"
              class="flex-1 px-4 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-2 focus:ring-blue-500 font-medium transition-all duration-200 shadow-lg hover:shadow-xl"
            >
              <i class="pi pi-send mr-2"></i>Gửi yêu cầu
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import Chart from "primevue/chart";
import {
  getVendorRevenueStats,
  getVendorMonthlyRevenueStats,
  getVendorTotalRevenueStats,
  type RevenueStats,
  type DailyRevenue,
  type MonthlyRevenue,
} from "../../api/vendor";
import { useLoading } from "../../composables/useLoading";

interface Summary {
  totalRevenue: number;
  totalCommission: number;
  commissionRate: number;
  currentBalance: number;
  successfulOrders: number;
}

interface Transaction {
  id: number;
  date: string;
  type: "income" | "expense";
  amount: number;
  status: "pending" | "completed" | "failed";
  note: string;
}

const { show, hide, isLoading, message } = useLoading();

// API data
const revenueStats = ref<RevenueStats | null>(null);
const monthlyStats = ref<RevenueStats | null>(null);
const totalStats = ref<RevenueStats | null>(null);

// Computed summary from API data
const summary = computed<Summary>(() => {
  const currentMonth = monthlyStats.value;
  const total = totalStats.value;

  if (!currentMonth || !total) {
    return {
      totalRevenue: 0,
      revenueGrowth: 0,
      totalCommission: 0,
      commissionRate: 10, // Default commission rate
      currentBalance: 0,
      successfulOrders: 0,
      orderGrowth: 0,
    };
  }

  return {
    totalRevenue: currentMonth.totalRevenue,
    totalCommission: currentMonth.totalRevenue * 0.1, // 10% commission
    commissionRate: 10,
    currentBalance: currentMonth.totalRevenue * 0.9, // Revenue minus commission
    successfulOrders: currentMonth.totalOrders,
  };
});

const chartPeriod = ref("month");

// Chart data computed from API
const chartData = computed(() => {
  if (chartPeriod.value === "month" && revenueStats.value?.dailyRevenue) {
    return {
      labels: revenueStats.value.dailyRevenue.map((item) =>
        new Date(item.date).toLocaleDateString("vi-VN", {
          day: "2-digit",
          month: "2-digit",
        })
      ),
      datasets: [
        {
          label: "Doanh thu",
          backgroundColor: "rgba(59, 130, 246, 0.8)",
          borderColor: "rgba(59, 130, 246, 1)",
          borderWidth: 2,
          borderRadius: 8,
          data: revenueStats.value.dailyRevenue.map((item) => item.revenue),
        },
      ],
    };
  } else if (
    chartPeriod.value === "year" &&
    monthlyStats.value?.monthlyRevenue
  ) {
    return {
      labels: monthlyStats.value.monthlyRevenue.map((item) =>
        new Date(2024, item.month - 1).toLocaleDateString("vi-VN", {
          month: "short",
        })
      ),
      datasets: [
        {
          label: "Doanh thu",
          backgroundColor: "rgba(59, 130, 246, 0.8)",
          borderColor: "rgba(59, 130, 246, 1)",
          borderWidth: 2,
          borderRadius: 8,
          data: monthlyStats.value.monthlyRevenue.map((item) => item.revenue),
        },
      ],
    };
  }

  // Default data
  return {
    labels: ["Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4"],
    datasets: [
      {
        label: "Doanh thu",
        backgroundColor: "rgba(59, 130, 246, 0.8)",
        borderColor: "rgba(59, 130, 246, 1)",
        borderWidth: 2,
        borderRadius: 8,
        data: [5000000, 7000000, 6000000, 8000000],
      },
    ],
  };
});

// Mock transactions (you can replace with real API data)
const transactions = ref<Transaction[]>([
  {
    id: 1,
    date: "2024-03-15T10:30:00",
    type: "income",
    amount: 1000000,
    status: "completed",
    note: "Thanh toán đơn hàng #123",
  },
  {
    id: 2,
    date: "2024-03-14T15:45:00",
    type: "expense",
    amount: 100000,
    status: "completed",
    note: "Hoa hồng hệ thống",
  },
  {
    id: 3,
    date: "2024-03-13T09:15:00",
    type: "income",
    amount: 2500000,
    status: "completed",
    note: "Thanh toán đơn hàng #124",
  },
  {
    id: 4,
    date: "2024-03-12T14:20:00",
    type: "expense",
    amount: 150000,
    status: "pending",
    note: "Phí dịch vụ",
  },
  {
    id: 5,
    date: "2024-03-11T11:30:00",
    type: "income",
    amount: 1800000,
    status: "completed",
    note: "Thanh toán đơn hàng #125",
  },
]);

const showWithdrawalModal = ref(false);
const withdrawalAmount = ref(0);
const withdrawalMethod = ref("bank");
const withdrawalInfo = ref("");

// API functions
const loadRevenueStats = async () => {
  try {
    show("Đang tải dữ liệu doanh thu...");

    // Get current month stats
    const now = new Date();
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const endOfMonth = new Date(
      now.getFullYear(),
      now.getMonth() + 1,
      0,
      23,
      59,
      59
    );

    const [monthlyData, totalData, currentMonthData] = await Promise.all([
      getVendorMonthlyRevenueStats(now.getFullYear()),
      getVendorTotalRevenueStats(),
      getVendorRevenueStats(
        startOfMonth.toISOString(),
        endOfMonth.toISOString()
      ),
    ]);

    monthlyStats.value = monthlyData;
    totalStats.value = totalData;
    revenueStats.value = currentMonthData;
  } catch (error) {
    console.error("Error loading revenue stats:", error);
  } finally {
    hide();
  }
};

const loadChartData = async () => {
  try {
    show("Đang tải dữ liệu biểu đồ...");

    const now = new Date();

    if (chartPeriod.value === "week") {
      // Get last 7 days
      const endDate = new Date();
      const startDate = new Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000);

      const data = await getVendorRevenueStats(
        startDate.toISOString(),
        endDate.toISOString()
      );
      revenueStats.value = data;
    } else if (chartPeriod.value === "month") {
      // Get current month
      const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
      const endOfMonth = new Date(
        now.getFullYear(),
        now.getMonth() + 1,
        0,
        23,
        59,
        59
      );

      const data = await getVendorRevenueStats(
        startOfMonth.toISOString(),
        endOfMonth.toISOString()
      );
      revenueStats.value = data;
    } else if (chartPeriod.value === "year") {
      // Get current year
      const data = await getVendorMonthlyRevenueStats(now.getFullYear());
      monthlyStats.value = data;
    }
  } catch (error) {
    console.error("Error loading chart data:", error);
  } finally {
    hide();
  }
};

// Watch for chart period changes
watch(chartPeriod, () => {
  loadChartData();
});

// Load data on mount
onMounted(() => {
  loadRevenueStats();
});

const chartOptions = ref({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      display: true,
      position: "top",
      labels: {
        usePointStyle: true,
        padding: 20,
        font: {
          size: 12,
          weight: "bold",
        },
      },
    },
    tooltip: {
      callbacks: {
        label: function (context: {
          dataset: { label: string };
          parsed: { y: number };
        }) {
          return `${context.dataset.label}: ${formatPrice(context.parsed.y)}`;
        },
      },
    },
  },
  scales: {
    y: {
      beginAtZero: true,
      grid: {
        color: "rgba(0, 0, 0, 0.05)",
        drawBorder: false,
      },
      ticks: {
        callback: function (value: number) {
          if (value >= 1000000) {
            return (value / 1000000).toFixed(1) + "M";
          } else if (value >= 1000) {
            return (value / 1000).toFixed(0) + "K";
          }
          return value.toString();
        },
        font: {
          size: 12,
        },
      },
    },
    x: {
      grid: {
        display: false,
      },
      ticks: {
        font: {
          size: 12,
        },
      },
    },
  },
});

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const formatDate = (date: string) => {
  return new Date(date).toLocaleString("vi-VN", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const getTransactionTypeLabel = (type: string) => {
  return type === "income" ? "Thu nhập" : "Chi phí";
};

const getTransactionTypeClass = (type: string) => {
  return type === "income"
    ? "bg-green-100 text-green-800"
    : "bg-red-100 text-red-800";
};

const getStatusLabel = (status: string) => {
  const labels = {
    pending: "Đang xử lý",
    completed: "Hoàn thành",
    failed: "Thất bại",
  };
  return labels[status as keyof typeof labels] || status;
};

const getStatusClass = (status: string) => {
  const classes = {
    pending: "bg-yellow-100 text-yellow-800",
    completed: "bg-green-100 text-green-800",
    failed: "bg-red-100 text-red-800",
  };
  return classes[status as keyof typeof classes] || "bg-gray-100 text-gray-800";
};

const getStatusIcon = (status: string) => {
  const icons = {
    pending: "pi pi-clock",
    completed: "pi pi-check-circle",
    failed: "pi pi-times-circle",
  };
  return icons[status as keyof typeof icons] || "pi pi-info-circle";
};

const requestWithdrawal = () => {
  showWithdrawalModal.value = true;
};

const closeWithdrawalModal = () => {
  showWithdrawalModal.value = false;
  withdrawalAmount.value = 0;
  withdrawalMethod.value = "bank";
  withdrawalInfo.value = "";
};

const submitWithdrawalRequest = () => {
  // TODO: Implement API call to submit withdrawal request
  console.log("Withdrawal request:", {
    amount: withdrawalAmount.value,
    method: withdrawalMethod.value,
    info: withdrawalInfo.value,
  });
  closeWithdrawalModal();
};
</script>

<style scoped>
/* Custom scrollbar */
::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* Smooth transitions */
* {
  transition: all 0.2s ease-in-out;
}

/* Chart container */
.chart-container {
  position: relative;
  height: 100%;
  width: 100%;
}
</style>
