<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <p class="text-2xl font-bold">Quản lý sản phẩm</p>
    <Tabs value="0">
      <TabList>
        <Tab value="0">Sản phẩm chờ duyệt</Tab>
        <Tab value="1">Sản phẩm bị báo cáo</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="flex flex-col gap-4">
            <DataTable
              :value="pendingProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="10"
              :rowsPerPageOptions="[5, 10, 20, 50]"
            >
              <Column field="code" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="category" header="Danh mục"></Column>
              <Column field="price" header="Giá">
                <template #body="slotProps">
                  {{ formatPrice(slotProps.data.price) }}
                </template>
              </Column>
              <Column field="vendor" header="Người bán"></Column>
              <Column header="Thao tác">
                <template #body="slotProps">
                  <div class="flex gap-2">
                    <Button icon="pi pi-check" severity="success" @click="approveProduct(slotProps.data)" />
                    <Button icon="pi pi-times" severity="danger" @click="rejectProduct(slotProps.data)" />
                  </div>
                </template>
              </Column>
            </DataTable>
          </div>
        </TabPanel>
        <TabPanel value="1">
          <div class="flex flex-col gap-4">
            <DataTable
              :value="reportedProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="10"
              :rowsPerPageOptions="[5, 10, 20, 50]"
            >
              <Column field="code" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="category" header="Danh mục"></Column>
              <Column field="price" header="Giá">
                <template #body="slotProps">
                  {{ formatPrice(slotProps.data.price) }}
                </template>
              </Column>
              <Column field="vendor" header="Người bán"></Column>
              <Column field="reportCount" header="Số lần báo cáo"></Column>
              <Column header="Thao tác">
                <template #body="slotProps">
                  <div class="flex gap-2">
                    <Button icon="pi pi-eye" severity="info" @click="viewReportDetails(slotProps.data)" />
                    <Button icon="pi pi-trash" severity="danger" @click="removeProduct(slotProps.data)" />
                  </div>
                </template>
              </Column>
            </DataTable>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </div>
</template>

<script setup lang="ts">
import Tabs from "primevue/tabs";
import TabList from "primevue/tablist";
import Tab from "primevue/tab";
import TabPanels from "primevue/tabpanels";
import TabPanel from "primevue/tabpanel";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Button from "primevue/button";
import { ref } from "vue";

interface Product {
  code: string;
  name: string;
  category: string;
  price: number;
  vendor: string;
  reportCount?: number;
}

const pendingProducts = ref<Product[]>([
  {
    code: "P001",
    name: "Áo thun nam cotton",
    category: "Áo",
    price: 150000,
    vendor: "Nguyễn Văn A"
  },
  {
    code: "P002",
    name: "Quần jean nam",
    category: "Quần",
    price: 350000,
    vendor: "Trần Thị B"
  }
]);

const reportedProducts = ref<Product[]>([
  {
    code: "P003",
    name: "Giày thể thao",
    category: "Giày",
    price: 850000,
    vendor: "Lê Văn C",
    reportCount: 5
  },
  {
    code: "P004",
    name: "Túi xách nữ",
    category: "Phụ kiện",
    price: 450000,
    vendor: "Phạm Thị D",
    reportCount: 3
  }
]);

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const approveProduct = (product: Product) => {
  // TODO: Implement approve product logic
  console.log('Approving product:', product);
};

const rejectProduct = (product: Product) => {
  // TODO: Implement reject product logic
  console.log('Rejecting product:', product);
};

const viewReportDetails = (product: Product) => {
  // TODO: Implement view report details logic
  console.log('Viewing report details for product:', product);
};

const removeProduct = (product: Product) => {
  // TODO: Implement remove product logic
  console.log('Removing product:', product);
};
</script>

<style scoped></style>
