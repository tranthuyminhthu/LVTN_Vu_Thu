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
            <div v-if="loading" class="flex justify-center items-center py-8">
              <div class="loading-spinner"></div>
              <span class="ml-2">Đang tải sản phẩm...</span>
            </div>
            
            <DataTable
              v-else
              :value="pendingProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="pendingProductsPagination.size"
              :totalRecords="pendingProductsPagination.totalElements"
              :first="pendingProductsPagination.page * pendingProductsPagination.size"
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onPendingProductsPageChange"
            >
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50) }}{{ slotProps.data.description?.length > 50 ? '...' : '' }}
                </template>
              </Column>
              <Column field="price" header="Giá">
                <template #body="slotProps">
                  {{ formatPrice(slotProps.data.price) }}
                </template>
              </Column>
              <Column field="createdBy" header="Người tạo"></Column>
              <Column field="status" header="Trạng thái">
                <template #body="slotProps">
                  <span class="px-2 py-1 rounded-full text-xs font-medium" 
                        :class="{
                          'bg-yellow-100 text-yellow-800': slotProps.data.status === 'IN_PROGRESS',
                          'bg-green-100 text-green-800': slotProps.data.status === 'APPROVED',
                          'bg-red-100 text-red-800': slotProps.data.status === 'REJECTED'
                        }">
                    {{ getStatusDisplayName(slotProps.data.status) }}
                  </span>
                </template>
              </Column>
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
            <div v-if="loading" class="flex justify-center items-center py-8">
              <div class="loading-spinner"></div>
              <span class="ml-2">Đang tải sản phẩm...</span>
            </div>
            
            <DataTable
              v-else
              :value="reportedProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="reportedProductsPagination.size"
              :totalRecords="reportedProductsPagination.totalElements"
              :first="reportedProductsPagination.page * reportedProductsPagination.size"
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onReportedProductsPageChange"
            >
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50) }}{{ slotProps.data.description?.length > 50 ? '...' : '' }}
                </template>
              </Column>
              <Column field="price" header="Giá">
                <template #body="slotProps">
                  {{ formatPrice(slotProps.data.price) }}
                </template>
              </Column>
              <Column field="createdBy" header="Người tạo"></Column>
              <Column field="reportCount" header="Số lần báo cáo">
                <template #body="slotProps">
                  <span class="px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
                    {{ slotProps.data.reportCount || 0 }}
                  </span>
                </template>
              </Column>
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
import { ref, onMounted } from "vue";
import { getProducts, type ProductResponse, type ProductQueryParams } from "@/api/product";
import { useToast } from "primevue/usetoast";

interface Product {
  id: string;
  name: string;
  description: string;
  price: number;
  rating: number;
  status: string;
  variants: unknown[];
  images?: string[];
  createdBy?: string;
  reportCount?: number;
}

const toast = useToast();
const loading = ref(false);

// Pagination state for pending products
const pendingProductsPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0
});

// Pagination state for reported products  
const reportedProductsPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0
});

const pendingProducts = ref<Product[]>([]);
const reportedProducts = ref<Product[]>([]);

// Load pending products from API
const loadPendingProducts = async () => {
  try {
    loading.value = true;
    const params: ProductQueryParams = {
      page: pendingProductsPagination.value.page,
      size: pendingProductsPagination.value.size,
      status: "IN_PROGRESS"
    };
    
    const response = await getProducts(params);
    console.log('Pending Products API Response:', response);
    
    // Convert API Product to UI Product
    pendingProducts.value = response.products.map(product => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || '',
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy
    }));
    
    pendingProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages
    };
  } catch (error) {
    console.error('Error loading pending products:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách sản phẩm chờ duyệt',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

// Load reported products from API
const loadReportedProducts = async () => {
  try {
    loading.value = true;
    const params: ProductQueryParams = {
      page: reportedProductsPagination.value.page,
      size: reportedProductsPagination.value.size,
      status: "REPORTED" // Assuming there's a REPORTED status
    };
    
    const response = await getProducts(params);
    console.log('Reported Products API Response:', response);
    
    // Convert API Product to UI Product
    reportedProducts.value = response.products.map(product => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || '',
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy,
      reportCount: 0 // TODO: Get from API if available
    }));
    
    reportedProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages
    };
  } catch (error) {
    console.error('Error loading reported products:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách sản phẩm bị báo cáo',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

// Handle pending products pagination
const onPendingProductsPageChange = (event: { first: number; rows: number; page: number }) => {
  console.log('Pending products page change event:', event);
  pendingProductsPagination.value.page = event.page;
  pendingProductsPagination.value.size = event.rows;
  loadPendingProducts();
};

// Handle reported products pagination
const onReportedProductsPageChange = (event: { first: number; rows: number; page: number }) => {
  console.log('Reported products page change event:', event);
  reportedProductsPagination.value.page = event.page;
  reportedProductsPagination.value.size = event.rows;
  loadReportedProducts();
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getStatusDisplayName = (status: string) => {
  switch (status) {
    case 'IN_PROGRESS':
      return 'Chờ duyệt';
    case 'APPROVED':
      return 'Đã duyệt';
    case 'REJECTED':
      return 'Từ chối';
    case 'REPORTED':
      return 'Bị báo cáo';
    default:
      return status;
  }
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

// Load initial data
onMounted(() => {
  loadPendingProducts();
  loadReportedProducts();
});
</script>

<style scoped>
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>
