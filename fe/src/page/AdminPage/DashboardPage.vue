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
            <span class="text-xl font-bold">{{ formatCurrency(todayRevenue) }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #2dce89, #2dcecc)"
          >
            <i class="pi pi-wallet text-white text-2xl"></i>
          </div>
        </div>
        <span :class="revenueGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ revenueGrowth >= 0 ? '+' : '' }}{{ revenueGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Đơn hàng hôm nay</span>
            <span class="text-xl font-bold">{{ todayOrders }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #11cdef, #1171ef)"
          >
            <i class="pi pi-shopping-cart text-white text-2xl"></i>
          </div>
        </div>
        <span :class="ordersGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ ordersGrowth >= 0 ? '+' : '' }}{{ ordersGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Người dùng mới</span>
            <span class="text-xl font-bold">{{ newUsers }}</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #fb6340, #fbb140)"
          >
            <i class="pi pi-users text-white text-2xl"></i>
          </div>
        </div>
        <span :class="usersGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ usersGrowth >= 0 ? '+' : '' }}{{ usersGrowth }}%</span>
        <span class="text-sm">So với hôm qua</span>
      </div>

      <div class="bg-white rounded-lg p-4">
        <div class="flex justify-between items-center">
          <div class="flex flex-col gap-1 mb-2">
            <span class="uppercase">Tỷ lệ chuyển đổi</span>
            <span class="text-xl font-bold">{{ conversionRate }}%</span>
          </div>
          <div
            class="rounded-full p-2 !w-12 !h-12 flex items-center justify-center"
            style="background-image: linear-gradient(310deg, #8965e0, #bc65e0)"
          >
            <i class="pi pi-chart-line text-white text-2xl"></i>
          </div>
        </div>
        <span :class="conversionGrowth >= 0 ? 'text-green-400' : 'text-red-400'" class="mr-2">{{ conversionGrowth >= 0 ? '+' : '' }}{{ conversionGrowth }}%</span>
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
            <div v-for="product in topProducts" :key="product.id" class="flex items-center gap-3">
              <img :src="product.image" :alt="product.name" class="w-12 h-12 rounded-lg object-cover" />
              <div class="flex-1">
                <p class="font-medium">{{ product.name }}</p>
                <div class="flex justify-between items-center">
                  <span class="text-sm text-gray-500">{{ product.sold }} đã bán</span>
                  <span class="font-medium">{{ formatCurrency(product.revenue) }}</span>
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

onMounted(() => {
  chartData.value = setChartData();
  chartOptions.value = setChartOptions();
  consumptionChartData.value = setConsumptionChartData();
});

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

const todayRevenue = ref(1500000);
const todayOrders = ref(45);
const newUsers = ref(12);
const conversionRate = ref(3.2);

const revenueGrowth = ref(15);
const ordersGrowth = ref(-5);
const usersGrowth = ref(25);
const conversionGrowth = ref(10);

const consumptionChartData = ref();
const topProducts = ref([
  {
    id: '1',
    name: 'iPhone 15 Pro Max',
    image: 'https://example.com/iphone.jpg',
    sold: 150,
    revenue: 1500000000
  },
  {
    id: '2',
    name: 'Samsung Galaxy S24 Ultra',
    image: 'https://example.com/samsung.jpg',
    sold: 120,
    revenue: 1200000000
  },
  {
    id: '3',
    name: 'MacBook Pro M3',
    image: 'https://example.com/macbook.jpg',
    sold: 80,
    revenue: 800000000
  }
]);

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

const setChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement);

  return {
    labels: ["January", "February", "March", "April", "May", "June", "July"],
    datasets: [
      {
        label: "First Dataset",
        data: [65, 59, 80, 81, 56, 55, 40],
        fill: false,
        tension: 0.4,
        borderColor: documentStyle.getPropertyValue("--p-cyan-500"),
      },
      {
        label: "Second Dataset",
        data: [28, 48, 40, 19, 86, 27, 90],
        fill: false,
        borderDash: [5, 5],
        tension: 0.4,
        borderColor: documentStyle.getPropertyValue("--p-orange-500"),
      },
      {
        label: "Third Dataset",
        data: [12, 51, 62, 33, 21, 62, 45],
        fill: true,
        borderColor: documentStyle.getPropertyValue("--p-gray-500"),
        tension: 0.4,
        backgroundColor: "rgba(107, 114, 128, 0.2)",
      },
    ],
  };
};
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
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(value);
};

const setConsumptionChartData = () => {
  const documentStyle = getComputedStyle(document.documentElement);

  return {
    labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
    datasets: [
      {
        label: 'Đơn hàng',
        data: [65, 59, 80, 81, 56, 55, 40],
        fill: false,
        tension: 0.4,
        borderColor: documentStyle.getPropertyValue('--p-cyan-500'),
      },
      {
        label: 'Doanh thu',
        data: [28, 48, 40, 19, 86, 27, 90],
        fill: false,
        borderDash: [5, 5],
        tension: 0.4,
        borderColor: documentStyle.getPropertyValue('--p-orange-500'),
      }
    ]
  };
};
</script>
