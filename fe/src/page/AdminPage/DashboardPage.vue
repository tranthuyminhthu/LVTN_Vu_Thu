<template>
  <div class="">
    <div class="flex justify-between items-center text-white mb-4">
      <div class="flex flex-col gap-2">
        <span>Page / Dashboard</span>
        <span class="text-lg font-bold">Dashboard</span>
      </div>
      <div class="">
        <span>input</span>
      </div>
    </div>
    <div class="!grid grid-cols-4 gap-4">
      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Doanh thu hôm nay</span>
            <span class="text-xl font-bold">{{ formatCurrency(dashboardStats.todayRevenue) }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #2dce89, #2dcecc)"
          >
            <i class="pi pi-wallet text-white text-2xl"></i>
          </div>
        </div>
        <span :class="dashboardStats.revenueGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ dashboardStats.revenueGrowth >= 0 ? '+' : '' }}{{ dashboardStats.revenueGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Đơn hàng hôm nay</span>
            <span class="text-xl font-bold">{{ dashboardStats.todayOrders }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #11cdef, #1171ef)"
          >
            <i class="pi pi-shopping-cart text-white text-2xl"></i>
          </div>
        </div>
        <span :class="dashboardStats.ordersGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ dashboardStats.ordersGrowth >= 0 ? '+' : '' }}{{ dashboardStats.ordersGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Người dùng mới</span>
            <span class="text-xl font-bold">{{ dashboardStats.newUsers }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #fb6340, #fbb140)"
          >
            <i class="pi pi-users text-white text-2xl"></i>
          </div>
        </div>
        <span :class="dashboardStats.usersGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ dashboardStats.usersGrowth >= 0 ? '+' : '' }}{{ dashboardStats.usersGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Tổng doanh thu</span>
            <span class="text-xl font-bold">{{ formatCurrency(dashboardStats.totalRevenue) }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #8965e0, #bc65e0)"
          >
            <i class="pi pi-money-bill text-white text-2xl"></i>
          </div>
        </div>
        <span :class="dashboardStats.revenueGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ dashboardStats.revenueGrowth >= 0 ? '+' : '' }}{{ dashboardStats.revenueGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>
    </div>
    <div class="!mt-3">
      <div class="!grid grid-cols-10 gap-4">
        <div class="col-span-6 bg-white rounded-lg p-4">
          <h3 class="text-lg font-semibold mb-4">Xu hướng tiêu dùng</h3>
          <Chart
            type="line"
            :data="consumptionChartData"
            :options="chartOptions"
            class="h-[250px]"
          />
        </div>
        <div class="col-span-4 bg-white rounded-lg p-4">
          <h3 class="text-lg font-semibold mb-4">Sản phẩm bán chạy</h3>
          <div class="flex flex-col gap-4">
            <div v-for="product in topProducts" :key="product.productSku" class="flex items-center gap-3">
              <img :src="'https://via.placeholder.com/80x80?text=No+Image'" :alt="product.productName" class="w-12 h-12 rounded-lg object-cover" />
              <div class="flex-1">
                <p class="font-medium">{{ product.productName }}</p>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-500">{{ product.quantitySold }} đã bán</span>
                  <span class="font-medium">{{ formatCurrency(product.totalRevenue) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="!grid grid-cols-10 gap-4 mt-4">
        <div class="col-span-6 bg-white rounded-lg p-4">
          <h3 class="text-lg font-semibold mb-4">Danh sách sản phẩm</h3>
          <DataTable
            v-model:editingRows="editingRows"
            :value="products"
            editMode="row"
            dataKey="id"
            @row-edit-save="onRowEditSave"
            :pt="{
              table: { style: 'min-width: 50rem' },
              column: {
                bodycell: ({ state }: ColumnState) => ({
                  style: state.d_editing
                    ? 'padding-top: 0.75rem; padding-bottom: 0.75rem'
                    : undefined,
                }),
              },
            }"
          >
            <Column field="code" header="Code" style="width: 20%">
              <template #editor="{ data, field }">
                <InputText v-model="data[field]" />
              </template>
            </Column>
            <Column field="name" header="Name" style="width: 20%">
              <template #editor="{ data, field }">
                <InputText v-model="data[field]" fluid />
              </template>
            </Column>
            <Column field="inventoryStatus" header="Status" style="width: 20%">
              <template #editor="{ data, field }">
                <Select
                  v-model="data[field]"
                  :options="statuses"
                  optionLabel="label"
                  optionValue="value"
                  placeholder="Select a Status"
                  fluid
                >
                  <template #option="slotProps">
                    <Tag
                      :value="slotProps.option.value"
                      :severity="getStatusLabel(slotProps.option.value)"
                    />
                  </template>
                </Select>
              </template>
              <template #body="slotProps">
                <Tag
                  :value="slotProps.data.inventoryStatus"
                  :severity="getStatusLabel(slotProps.data.inventoryStatus)"
                />
              </template>
            </Column>
            <Column field="price" header="Price" style="width: 20%">
              <template #body="{ data, field }">
                {{ formatCurrency(data[field]) }}
              </template>
              <template #editor="{ data, field }">
                <InputNumber
                  v-model="data[field]"
                  mode="currency"
                  currency="USD"
                  locale="en-US"
                  fluid
                />
              </template>
            </Column>
            <Column
              :rowEditor="true"
              style="width: 10%; min-width: 8rem"
              bodyStyle="text-align:center"
            ></Column>
          </DataTable>
        </div>
        <div class="col-span-4 bg-white rounded-lg p-4">
          <h3 class="text-lg font-semibold mb-4">Thống kê theo danh mục</h3>
          <div class="flex flex-col gap-2">
            <div v-for="category in categoryStats" :key="category.name"
              class="flex justify-between items-center hover:bg-gray-100 rounded-lg p-2 cursor-pointer mt-2 transition-all duration-300 px-2"
            >
              <div class="flex items-center gap-2">
                <span class="!w-12 !h-12 flex items-center justify-center rounded-full bg-gray-200">
                  <i :class="category.icon"></i>
                </span>
                <div>
                  <span class="text-sm font-bold">{{ category.name }}</span>
                  <div>
                    <span>{{ category.products }} sản phẩm,</span>
                    <span> {{ category.sold }} đã bán</span>
                  </div>
                </div>
              </div>
              <div>
                <span class="font-medium">{{ formatCurrency(category.revenue) }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import Chart from "primevue/chart";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import { ref, onMounted } from "vue";
import { useToast } from "primevue/usetoast";
import type { 
  RevenueStats,
  TopProduct 
} from "@/api/vendor";
import { 
  getVendorTotalRevenueStats,
  getVendorRevenueStats,
  getVendorMonthlyRevenueStats
} from "@/api/vendor";

const toast = useToast();

interface Product {
  id: string;
  code: string;
  name: string;
  inventoryStatus: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK";
  price: number;
  description: string;
  image: string;
  category: string;
  quantity: number;
  rating: number;
}

interface TableState {
  d_editing: boolean;
}

interface ColumnState {
  state: TableState;
}

// Reactive data
const dashboardStats = ref({
  todayRevenue: 0,
  todayOrders: 0,
  newUsers: 0,
  conversionRate: 0,
  totalRevenue: 0,
  revenueGrowth: 0,
  ordersGrowth: 0,
  usersGrowth: 0,
  conversionGrowth: 0,
});

const topProducts = ref<TopProduct[]>([]);
const categoryStats = ref([
  {
    name: 'Điện thoại',
    icon: 'pi pi-mobile',
    products: 250,
    sold: 346,
    revenue: 3500000000
  },
  {
    name: 'Laptop',
    icon: 'pi pi-laptop',
    products: 150,
    sold: 245,
    revenue: 2500000000
  },
  {
    name: 'Phụ kiện',
    icon: 'pi pi-headphones',
    products: 500,
    sold: 1200,
    revenue: 800000000
  },
  {
    name: 'Đồng hồ',
    icon: 'pi pi-clock',
    products: 200,
    sold: 180,
    revenue: 600000000
  }
]);
const revenueStats = ref<RevenueStats | null>(null);

const chartData = ref();
const chartOptions = ref();
const products = ref<Product[]>([
  {
    id: "1000",
    code: "f230fh0g3",
    name: "Bamboo Watch",
    description: "Product Description",
    image: "bamboo-watch.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
]);
const editingRows = ref([]);
const statuses = ref([
  { label: "In Stock", value: "INSTOCK" },
  { label: "Low Stock", value: "LOWSTOCK" },
  { label: "Out of Stock", value: "OUTOFSTOCK" },
]);

const consumptionChartData = ref();

// API calls
const fetchDashboardStats = async () => {
  try {
    const stats = await getVendorTotalRevenueStats();
    
    // Map vendor stats to dashboard format
    dashboardStats.value = {
      todayRevenue: stats.totalRevenue,
      todayOrders: stats.totalOrders,
      newUsers: 0, // Not available in vendor API
      conversionRate: 0, // Not available in vendor API
      totalRevenue: stats.totalRevenue,
      revenueGrowth: 0, // Not available in vendor API
      ordersGrowth: 0, // Not available in vendor API
      usersGrowth: 0, // Not available in vendor API
      conversionGrowth: 0, // Not available in vendor API
    };
  } catch (error) {
    console.error('Error fetching dashboard stats:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải thống kê dashboard',
      life: 3000
    });
  }
};

const fetchTopProducts = async () => {
  try {
    // Get current month data to extract top products
    const now = new Date();
    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0, 23, 59, 59);
    
    const stats = await getVendorRevenueStats(
      startOfMonth.toISOString(),
      endOfMonth.toISOString()
    );
    
    if (stats.topProducts) {
      topProducts.value = stats.topProducts;
    }
  } catch (error) {
    console.error('Error fetching top products:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải sản phẩm bán chạy',
      life: 3000
    });
  }
};

const fetchRevenueStats = async () => {
  try {
    // Get last 7 days data for chart
    const endDate = new Date();
    const startDate = new Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000);
    
    const stats = await getVendorRevenueStats(
      startDate.toISOString(),
      endDate.toISOString()
    );
    revenueStats.value = stats;
    updateChartData();
  } catch (error) {
    console.error('Error fetching revenue stats:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải dữ liệu biểu đồ',
      life: 3000
    });
  }
};

const updateChartData = () => {
  if (revenueStats.value?.dailyRevenue) {
    consumptionChartData.value = {
      labels: revenueStats.value.dailyRevenue.map(stat => {
        const date = new Date(stat.date);
        return date.toLocaleDateString('vi-VN', { weekday: 'short' });
      }),
      datasets: [
        {
          label: 'Đơn hàng',
          data: revenueStats.value.dailyRevenue.map(stat => stat.orderCount),
          fill: false,
          tension: 0.4,
          borderColor: '#11cdef',
        },
        {
          label: 'Doanh thu (triệu)',
          data: revenueStats.value.dailyRevenue.map(stat => stat.revenue / 1000000),
          fill: false,
          borderDash: [5, 5],
          tension: 0.4,
          borderColor: '#fb6340',
        }
      ]
    };
  }
};

onMounted(async () => {
  // Load all dashboard data
  await Promise.all([
    fetchDashboardStats(),
    fetchTopProducts(),
    fetchRevenueStats()
  ]);
  
  // Set up chart options
  chartOptions.value = setChartOptions();
});

const setChartOptions = () => {
  const documentStyle = getComputedStyle(document.documentElement);
  const textColor = documentStyle.getPropertyValue("--p-text-color");
  const textColorSecondary = documentStyle.getPropertyValue(
    "--p-text-muted-color"
  );
  const surfaceBorder = documentStyle.getPropertyValue(
    "--p-content-border-color"
  );

  return {
    maintainAspectRatio: false,
    aspectRatio: 0.6,
    plugins: {
      legend: {
        labels: {
          color: textColor,
        },
      },
    },
    scales: {
      x: {
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
        },
      },
      y: {
        ticks: {
          color: textColorSecondary,
        },
        grid: {
          color: surfaceBorder,
        },
      },
    },
  };
};

const onRowEditSave = (event: { newData: Product; index: number }) => {
  const { newData, index } = event;
  products.value[index] = newData;
};

const getStatusLabel = (status: "INSTOCK" | "LOWSTOCK" | "OUTOFSTOCK") => {
  switch (status) {
    case "INSTOCK":
      return "success";
    case "LOWSTOCK":
      return "warn";
    case "OUTOFSTOCK":
      return "danger";
    default:
      return "secondary";
  }
};

const formatCurrency = (value: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(value);
};
</script>
