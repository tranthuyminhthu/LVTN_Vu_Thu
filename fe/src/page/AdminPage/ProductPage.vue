<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <p class="text-2xl font-bold">Quản lý sản phẩm</p>
    <Tabs value="0">
      <TabList>
        <Tab value="0">Sản phẩm chờ duyệt</Tab>
        <Tab value="1">Sản phẩm bị báo cáo</Tab>
        <Tab value="2">Sản phẩm đã bị từ chối</Tab>
        <Tab value="3">Sản phẩm đã được duyệt</Tab>
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
              :first="
                pendingProductsPagination.page * pendingProductsPagination.size
              "
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onPendingProductsPageChange"
            >
              <Column header="Ảnh" style="width: 80px">
                <template #body="slotProps">
                  <Image
                    :src="
                      slotProps.data.images && slotProps.data.images.length > 0
                        ? slotProps.data.images[0]
                        : 'https://via.placeholder.com/80x80?text=No+Image'
                    "
                    alt="Ảnh sản phẩm"
                    class="!w-14 !h-14 rounded-lg !object-cover"
                    preview
                    :pt="{ image: '!w-14 !h-14 rounded-lg !object-cover' }"
                  />
                </template>
              </Column>
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50)
                  }}{{ slotProps.data.description?.length > 50 ? "..." : "" }}
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
                  <Tag
                    :value="mapStatusName(slotProps.data.status)"
                    :severity="mapStatusSeverity(slotProps.data.status)"
                  />
                </template>
              </Column>
              <Column header="Thao tác">
                <template #body="slotProps">
                  <div class="flex gap-2">
                    <Button
                      icon="pi pi-check"
                      severity="success"
                      @click="approveProduct(slotProps.data)"
                    />
                    <Button
                      icon="pi pi-times"
                      severity="danger"
                      @click="rejectProduct(slotProps.data)"
                    />
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
              :first="
                reportedProductsPagination.page *
                reportedProductsPagination.size
              "
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onReportedProductsPageChange"
            >
              <Column header="Ảnh" style="width: 80px">
                <template #body="slotProps">
                  <Image
                    :src="
                      slotProps.data.images && slotProps.data.images.length > 0
                        ? slotProps.data.images[0]
                        : 'https://via.placeholder.com/80x80?text=No+Image'
                    "
                    alt="Ảnh sản phẩm"
                    preview
                    :pt="{ image: '!w-14 !h-14 rounded-lg !object-cover' }"
                  />
                </template>
              </Column>
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50)
                  }}{{ slotProps.data.description?.length > 50 ? "..." : "" }}
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
                  <span
                    class="px-2 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800"
                  >
                    {{ slotProps.data.reportCount || 0 }}
                  </span>
                </template>
              </Column>
              <Column header="Thao tác">
                <template #body="slotProps">
                  <div class="flex gap-2">
                    <Button
                      icon="pi pi-eye"
                      severity="info"
                      @click="viewReportDetails(slotProps.data)"
                    />
                    <Button
                      icon="pi pi-trash"
                      severity="danger"
                      @click="removeProduct(slotProps.data)"
                    />
                  </div>
                </template>
              </Column>
            </DataTable>
          </div>
        </TabPanel>
        <TabPanel value="2">
          <div class="flex flex-col gap-4">
            <div v-if="loading" class="flex justify-center items-center py-8">
              <div class="loading-spinner"></div>
              <span class="ml-2">Đang tải sản phẩm...</span>
            </div>
            <DataTable
              v-else
              :value="rejectedProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="rejectedProductsPagination.size"
              :totalRecords="rejectedProductsPagination.totalElements"
              :first="
                rejectedProductsPagination.page *
                rejectedProductsPagination.size
              "
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onRejectedProductsPageChange"
            >
              <Column header="Ảnh" style="width: 100px">
                <template #body="slotProps">
                  <Image
                    :src="
                      slotProps.data.images && slotProps.data.images.length > 0
                        ? slotProps.data.images[0]
                        : 'https://via.placeholder.com/80x80?text=No+Image'
                    "
                    alt="Ảnh sản phẩm"
                    class="!w-16 !h-16 object-cover rounded"
                    preview
                  />
                </template>
              </Column>
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50)
                  }}{{ slotProps.data.description?.length > 50 ? "..." : "" }}
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
                  <Tag
                    :value="mapStatusName(slotProps.data.status)"
                    :severity="mapStatusSeverity(slotProps.data.status)"
                  />
                </template>
              </Column>
            </DataTable>
          </div>
        </TabPanel>
        <TabPanel value="3">
          <div class="flex flex-col gap-4">
            <div v-if="loading" class="flex justify-center items-center py-8">
              <div class="loading-spinner"></div>
              <span class="ml-2">Đang tải sản phẩm...</span>
            </div>
            <DataTable
              v-else
              :value="approvedProducts"
              tableStyle="min-width: 50rem"
              class="bg-white rounded-lg"
              :paginator="true"
              :rows="approvedProductsPagination.size"
              :totalRecords="approvedProductsPagination.totalElements"
              :first="
                approvedProductsPagination.page *
                approvedProductsPagination.size
              "
              :rowsPerPageOptions="[5, 10, 20, 50]"
              :lazy="true"
              @page="onApprovedProductsPageChange"
            >
              <Column header="Ảnh" style="width: 80px">
                <template #body="slotProps">
                  <Image
                    :src="
                      slotProps.data.images && slotProps.data.images.length > 0
                        ? slotProps.data.images[0]
                        : 'https://via.placeholder.com/80x80?text=No+Image'
                    "
                    alt="Ảnh sản phẩm"
                    imageClass="w-16 h-16 object-cover rounded"
                    preview
                  />
                </template>
              </Column>
              <Column field="id" header="Mã sản phẩm"></Column>
              <Column field="name" header="Tên sản phẩm"></Column>
              <Column field="description" header="Mô tả">
                <template #body="slotProps">
                  {{ slotProps.data.description?.substring(0, 50)
                  }}{{ slotProps.data.description?.length > 50 ? "..." : "" }}
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
                  <Tag
                    :value="mapStatusName(slotProps.data.status)"
                    :severity="mapStatusSeverity(slotProps.data.status)"
                  />
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
import Tag from "primevue/tag"; // Added Tag import
import Image from "primevue/image";
import { ref, onMounted } from "vue";
import {
  getProducts,
  type ProductResponse,
  type ProductQueryParams,
  acceptProducts,
  rejectProducts,
} from "@/api/product";
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
  totalPages: 0,
});

// Pagination state for reported products
const reportedProductsPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
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
      status: "IN_PROGRESS",
    };

    const response = await getProducts(params);
    console.log("Pending Products API Response:", response);

    // Convert API Product to UI Product
    pendingProducts.value = response.products.map((product) => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || "",
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy,
    }));

    pendingProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
    };
  } catch (error) {
    console.error("Error loading pending products:", error);
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Không thể tải danh sách sản phẩm chờ duyệt",
      life: 3000,
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
      status: "REPORTED", // Assuming there's a REPORTED status
    };

    const response = await getProducts(params);
    console.log("Reported Products API Response:", response);

    // Convert API Product to UI Product
    reportedProducts.value = response.products.map((product) => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || "",
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy,
      reportCount: 0, // TODO: Get from API if available
    }));

    reportedProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
    };
  } catch (error) {
    console.error("Error loading reported products:", error);
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Không thể tải danh sách sản phẩm bị báo cáo",
      life: 3000,
    });
  } finally {
    loading.value = false;
  }
};

// Handle pending products pagination
const onPendingProductsPageChange = (event: {
  first: number;
  rows: number;
  page: number;
}) => {
  console.log("Pending products page change event:", event);
  pendingProductsPagination.value.page = event.page;
  pendingProductsPagination.value.size = event.rows;
  loadPendingProducts();
};

// Handle reported products pagination
const onReportedProductsPageChange = (event: {
  first: number;
  rows: number;
  page: number;
}) => {
  console.log("Reported products page change event:", event);
  reportedProductsPagination.value.page = event.page;
  reportedProductsPagination.value.size = event.rows;
  loadReportedProducts();
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const getStatusDisplayName = (status: string) => {
  switch (status) {
    case "IN_PROGRESS":
      return "Chờ duyệt";
    case "APPROVED":
      return "Đã duyệt";
    case "REJECTED":
      return "Từ chối";
    case "REPORTED":
      return "Bị báo cáo";
    default:
      return status;
  }
};

// Map status to Vietnamese name
const mapStatusName = (status: string | null) => {
  switch (status) {
    case "IN_PROGRESS":
      return "Đang chờ xác nhận";
    case "ACCEPTED":
      return "Đã duyệt";
    case "REJECTED":
      return "Bị từ chối";
    case "REPORTED":
      return "Bị báo cáo";
    default:
      return "Không xác định";
  }
};

// Map status to severity color for Tag
const mapStatusSeverity = (status: string | null) => {
  switch (status) {
    case "IN_PROGRESS":
      return "warning";
    case "ACCEPTED":
      return "success";
    case "REJECTED":
      return "danger";
    case "REPORTED":
      return "info";
    default:
      return "secondary";
  }
};

const approveProduct = async (product: Product) => {
  try {
    await acceptProducts([product.id]);
    toast.add({
      severity: "success",
      summary: "Thành công",
      detail: "Duyệt sản phẩm thành công",
      life: 2000,
    });
    loadPendingProducts();
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Duyệt sản phẩm thất bại",
      life: 3000,
    });
  }
};

const rejectProduct = async (product: Product) => {
  try {
    await rejectProducts([product.id]);
    toast.add({
      severity: "success",
      summary: "Thành công",
      detail: "Từ chối sản phẩm thành công",
      life: 2000,
    });
    loadPendingProducts();
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Từ chối sản phẩm thất bại",
      life: 3000,
    });
  }
};

const viewReportDetails = (product: Product) => {
  // TODO: Implement view report details logic
  console.log("Viewing report details for product:", product);
};

const removeProduct = (product: Product) => {
  // TODO: Implement remove product logic
  console.log("Removing product:", product);
};

const rejectedProductsPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
});
const approvedProductsPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0,
});
const rejectedProducts = ref<Product[]>([]);
const approvedProducts = ref<Product[]>([]);

const loadRejectedProducts = async () => {
  try {
    loading.value = true;
    const params: ProductQueryParams = {
      page: rejectedProductsPagination.value.page,
      size: rejectedProductsPagination.value.size,
      status: "REJECTED",
    };
    const response = await getProducts(params);
    rejectedProducts.value = response.products.map((product) => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || "",
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy,
    }));
    rejectedProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
    };
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Không thể tải danh sách sản phẩm bị từ chối",
      life: 3000,
    });
  } finally {
    loading.value = false;
  }
};
const loadApprovedProducts = async () => {
  try {
    loading.value = true;
    const params: ProductQueryParams = {
      page: approvedProductsPagination.value.page,
      size: approvedProductsPagination.value.size,
      status: "ACCEPTED",
    };
    const response = await getProducts(params);
    approvedProducts.value = response.products.map((product) => ({
      id: product.id.toString(),
      name: product.name,
      description: product.description,
      price: product.price,
      rating: product.rating,
      status: product.status || "",
      variants: product.variants || [],
      images: product.images || [],
      createdBy: product.createdBy,
    }));
    approvedProductsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
    };
  } catch (error) {
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Không thể tải danh sách sản phẩm đã được duyệt",
      life: 3000,
    });
  } finally {
    loading.value = false;
  }
};
const onRejectedProductsPageChange = (event: {
  first: number;
  rows: number;
  page: number;
}) => {
  rejectedProductsPagination.value.page = event.page;
  rejectedProductsPagination.value.size = event.rows;
  loadRejectedProducts();
};
const onApprovedProductsPageChange = (event: {
  first: number;
  rows: number;
  page: number;
}) => {
  approvedProductsPagination.value.page = event.page;
  approvedProductsPagination.value.size = event.rows;
  loadApprovedProducts();
};

// Load initial data
onMounted(() => {
  loadPendingProducts();
  loadReportedProducts();
  loadRejectedProducts();
  loadApprovedProducts();
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
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
