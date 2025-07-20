<template>
  <div class="rounded-lg">
    <!-- Store Info Card -->
    <div
      class="bg-white rounded-xl shadow flex items-center justify-between p-6 mb-6"
    >
      <div class="flex items-center gap-4">
        <img
          src="https://randomuser.me/api/portraits/men/32.jpg"
          alt="avatar"
          class="w-16 h-16 rounded-full border-4 border-blue-100 object-cover"
        />
        <div>
          <div class="text-2xl font-bold text-gray-800">Vendor Store</div>
          <div class="text-gray-500">Premium Electronics Store</div>
        </div>
      </div>
      <button class="text-gray-500 hover:text-blue-600 text-2xl">
        <i class="pi pi-external-link"></i>
      </button>
    </div>

    <!-- Stats Cards -->
    <div class="!grid grid-cols-1 md:grid-cols-4 gap-6">
      <div
        class="bg-white rounded-xl shadow p-6 flex items-center justify-between"
      >
        <div>
          <div class="text-gray-500 text-sm mb-1">Total Revenue</div>
          <div class="text-2xl font-bold">$45890.75</div>
        </div>
        <div
          class="bg-blue-100 rounded-full !w-12 !h-12 flex items-center justify-center"
        >
          <i class="pi pi-dollar text-blue-500 text-2xl"></i>
        </div>
      </div>
      <div
        class="bg-white rounded-xl shadow p-6 flex items-center justify-between"
      >
        <div>
          <div class="text-gray-500 text-sm mb-1">Total Orders</div>
          <div class="text-2xl font-bold">167</div>
        </div>
        <div
          class="bg-blue-100 rounded-full !w-12 !h-12 flex items-center justify-center"
        >
          <i class="pi pi-shopping-bag text-blue-500 text-2xl"></i>
        </div>
      </div>
      <div
        class="bg-white rounded-xl shadow p-6 flex items-center justify-between"
      >
        <div>
          <div class="text-gray-500 text-sm mb-1">Customers</div>
          <div class="text-2xl font-bold">124</div>
        </div>
        <div
          class="bg-blue-100 rounded-full !w-12 !h-12 flex items-center justify-center"
        >
          <i class="pi pi-users text-blue-500 text-2xl"></i>
        </div>
      </div>
      <div
        class="bg-white rounded-xl shadow p-6 flex items-center justify-between"
      >
        <div>
          <div class="text-gray-500 text-sm mb-1">Average Order</div>
          <div class="text-2xl font-bold">$274.79</div>
        </div>
        <div
          class="bg-blue-100 rounded-full !w-12 !h-12 flex items-center justify-center"
        >
          <i class="pi pi-chart-bar text-blue-500 text-2xl"></i>
        </div>
      </div>
    </div>
    <div class="mt-8">
      <div class="flex items-center justify-between mb-4">
        <h2 class="text-xl font-semibold">Recent Orders</h2>
      </div>
      <DataTable
        :value="orders"
        class="w-full"
        :pt="{ table: { class: 'min-w-full' } }"
      >
        <Column
          field="id"
          header="ORDER ID"
          body-class="px-4 py-3 font-medium"
        />
        <Column field="customer" header="CUSTOMER" body-class="px-4 py-3" />
        <Column field="date" header="DATE" body-class="px-4 py-3" />
        <Column
          field="amount"
          header="AMOUNT"
          body-class="px-4 py-3 font-semibold"
        >
          <template #body="slotProps">
            <span v-html="amountTemplate(slotProps.data)"></span>
          </template>
        </Column>
        <Column field="status" header="STATUS" body-class="px-4 py-3">
          <template #body="slotProps">
            <span v-html="statusTemplate(slotProps.data)"></span>
          </template>
        </Column>
      </DataTable>
    </div>
  </div>
</template>

<script setup lang="ts">
import DataTable from "primevue/datatable";
import Column from "primevue/column";

interface Order {
  id: string;
  customer: string;
  date: string;
  amount: string;
  status: string;
}

const orders: Order[] = [
  {
    id: "ORD001",
    customer: "John Smith",
    date: "2024-01-15",
    amount: "299.99",
    status: "Delivered",
  },
  {
    id: "ORD002",
    customer: "Mary Johnson",
    date: "2024-01-10",
    amount: "149.99",
    status: "Processing",
  },
  {
    id: "ORD003",
    customer: "Robert Brown",
    date: "2024-01-05",
    amount: "499.99",
    status: "Pending",
  },
];

const amountTemplate = (row: Order) => `$${row.amount}`;
const statusTemplate = (row: Order) => {
  if (row.status === "Delivered") {
    return `<span class='!bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-medium'>Delivered</span>`;
  } else if (row.status === "Processing") {
    return `<span class='!bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-xs font-medium'>Processing</span>`;
  } else {
    return `<span class='!bg-blue-50 text-blue-500 px-3 py-1 rounded-full text-xs font-medium'>Pending</span>`;
  }
};
</script>
