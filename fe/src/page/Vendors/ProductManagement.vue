<template>
  <div class="p-6">
    <!-- Toast Component -->
    <Toast />

    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-semibold text-white">Quản lý sản phẩm</h1>
      <button
        @click="openAddProductModal"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <i class="pi pi-plus mr-2"></i>Thêm sản phẩm mới
      </button>
    </div>

    <!-- Product List -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b">
        <div class="flex items-center gap-4">
          <div class="flex-1">
            <span class="p-input-icon-left w-full">
              <i class="pi pi-search" />
              <InputText
                v-model="searchQuery"
                placeholder="Tìm kiếm sản phẩm..."
                class="w-full"
              />
            </span>
          </div>
          <Dropdown
            v-model="sortBy"
            :options="sortOptions"
            optionLabel="label"
            optionValue="value"
            placeholder="Sắp xếp theo"
            class="w-48"
          />
        </div>
      </div>

      <!-- Product DataTable -->
      <div>
        <DataTable
          :value="filteredProducts"
          :paginator="true"
          :rows="itemsPerPage"
          :rowsPerPageOptions="[5, 10, 20, 50]"
          :loading="loading"
          :globalFilterFields="['name', 'description']"
          :sortField="sortField"
          :sortOrder="sortOrder"
          @sort="onSort"
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          currentPageReportTemplate="Hiển thị {first} đến {last} trong tổng số {totalRecords} sản phẩm"
          responsiveLayout="scroll"
          class="p-datatable-sm"
        >
          <!-- Product Column -->
          <Column
            field="name"
            header="Sản phẩm"
            :sortable="true"
            style="min-width: 200px"
          >
            <template #body="{ data }">
              <div class="flex items-center">
                <Image
                  :src="getProductImage(data)"
                  :alt="data.name"
                  class="!w-14 !h-14 rounded-lg !object-cover mr-3"
                  preview
                  :pt="{
                    image: '!w-14 !h-14 rounded-lg !object-cover',
                  }"
                />
                <div>
                  <div class="font-medium text-gray-900">{{ data.name }}</div>
                  <div class="text-sm text-gray-500">
                    {{ data.category || "Chưa phân loại" }}
                  </div>
                </div>
              </div>
            </template>
          </Column>

          <!-- Price Column -->
          <Column
            field="price"
            header="Giá"
            :sortable="true"
            style="min-width: 120px"
          >
            <template #body="{ data }">
              <span class="font-medium">{{ formatPrice(data.price) }}</span>
            </template>
          </Column>

          <!-- Stock Column -->
          <Column
            field="stock"
            header="Tồn kho"
            :sortable="true"
            style="min-width: 100px"
          >
            <template #body="{ data }">
              <span class="font-medium">{{ getTotalStock(data) }}</span>
            </template>
          </Column>

          <!-- Rating Column -->
          <Column
            field="rating"
            header="Đánh giá"
            :sortable="true"
            style="min-width: 100px"
          >
            <template #body="{ data }">
              <div class="flex items-center">
                <span class="font-medium mr-1">{{ data.rating }}</span>
                <i class="pi pi-star-fill text-yellow-400"></i>
              </div>
            </template>
          </Column>

          <!-- Status Column -->
          <Column
            field="status"
            header="Trạng thái"
            :sortable="true"
            style="min-width: 120px"
          >
            <template #body="{ data }">
              <Tag
                :value="data.status || 'Chưa xác định'"
                :severity="getStatusSeverity(data.status)"
              />
            </template>
          </Column>

          <!-- Actions Column -->
          <Column header="Thao tác" style="min-width: 120px">
            <template #body="{ data }">
              <div class="flex gap-2">
                <Button
                  icon="pi pi-pencil"
                  class="p-button-text p-button-sm p-button-info"
                  @click="editProduct(data)"
                  v-tooltip.top="'Chỉnh sửa'"
                />
                <Button
                  icon="pi pi-trash"
                  class="p-button-text p-button-sm p-button-danger"
                  @click="deleteProduct(data)"
                  v-tooltip.top="'Xóa'"
                />
              </div>
            </template>
          </Column>
        </DataTable>
      </div>
    </div>

    <!-- Add/Edit Product Modal -->
    <Dialog
      v-model:visible="showModal"
      :modal="true"
      :closable="true"
      :style="{ width: '800px' }"
      :header="isEditing ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'"
    >
      <LoadingGlobal :isLoading="isSaving">
        <template #default>
          <form @submit.prevent="saveProduct" class="space-y-6">
            <!-- Basic Product Info -->
            <div class="!grid grid-cols-2 gap-4 my-2">
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Tên sản phẩm *</label
                >
                <input
                  v-model="currentProduct.name"
                  type="text"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
              <div>
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >Giá cơ bản *</label
                >
                <input
                  v-model="currentProduct.price"
                  type="number"
                  class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                  required
                />
              </div>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Mô tả *</label
              >
              <textarea
                v-model="currentProduct.description"
                rows="3"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                required
              ></textarea>
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1"
                >Hình ảnh</label
              >
              <input
                type="file"
                @change="handleImageUpload"
                accept="image/*"
                multiple
                class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              />
              <!-- Display selected images -->
              <div v-if="imagesPreview.length > 0" class="mt-3">
                <p class="text-sm text-gray-600 mb-2">
                  Ảnh đã chọn ({{ imagesPreview.length }} ảnh):
                </p>
                <div class="grid grid-cols-3 gap-3">
                  <div
                    v-for="(image, index) in imagesPreview"
                    :key="index"
                    class="relative border border-gray-200 rounded-lg overflow-hidden hover:shadow-md transition-shadow"
                  >
                    <Image
                      :src="image"
                      :alt="`Product image ${index + 1}`"
                      class="w-full h-24 object-cover"
                      preview
                      :pt="{
                        image: 'w-full h-24 object-cover',
                      }"
                    />
                    <button
                      type="button"
                      @click="removeImage(index)"
                      class="absolute top-1 right-1 cursor-pointer bg-red-500 hover:bg-red-600 text-white rounded-full !w-6 !h-6 flex items-center justify-center text-xs shadow-lg transition-all duration-200 z-10"
                      title="Xóa ảnh này"
                    >
                      <i class="pi pi-times"></i>
                    </button>
                    <div
                      class="absolute bottom-1 left-1 bg-black bg-opacity-50 text-white text-xs px-2 py-1 rounded z-10"
                    >
                      {{ index + 1 }}
                    </div>
                  </div>
                </div>
                <p class="text-xs text-gray-500 mt-2">Click nút X để xóa ảnh</p>
              </div>
            </div>

            <!-- Variants Section -->
            <div class="pt-6">
              <div class="flex justify-between items-center mb-4">
                <h3 class="text-lg font-medium text-gray-900">
                  Biến thể sản phẩm
                </h3>
                <button
                  type="button"
                  @click="addVariant"
                  class="px-3 py-1 bg-green-600 text-white text-sm rounded-md hover:bg-green-700 focus:outline-none focus:ring-2 focus:ring-green-500"
                >
                  <i class="pi pi-plus mr-1"></i>Thêm biến thể
                </button>
              </div>

              <div
                v-if="
                  currentProduct.variants && currentProduct.variants.length > 0
                "
                class="!space-y-4"
              >
                <div
                  v-for="(variant, index) in currentProduct.variants"
                  :key="index"
                  class="border border-gray-200 rounded-lg p-4 bg-gray-50"
                >
                  <div class="flex justify-between items-center mb-3">
                    <h4 class="font-medium text-gray-900">
                      Biến thể {{ index + 1 }}
                    </h4>
                    <button
                      type="button"
                      @click="removeVariant(index)"
                      class="text-red-600 hover:text-red-800"
                    >
                      <i class="pi pi-trash"></i>
                    </button>
                  </div>

                  <div class="!grid grid-cols-2 gap-4 my-2">
                    <div>
                      <label
                        class="block text-sm font-medium text-gray-700 mb-1"
                        >Kích thước *</label
                      >
                      <select
                        v-model="variant.size"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      >
                        <option value="">Chọn kích thước</option>
                        <option value="XS">XS</option>
                        <option value="S">S</option>
                        <option value="M">M</option>
                        <option value="L">L</option>
                        <option value="XL">XL</option>
                        <option value="XXL">XXL</option>
                      </select>
                    </div>

                    <div>
                      <label
                        class="block text-sm font-medium text-gray-700 mb-1"
                        >Tên màu *</label
                      >
                      <input
                        v-model="variant.colorName"
                        type="text"
                        placeholder="VD: Red, Blue, Black"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      />
                    </div>

                    <div>
                      <label
                        class="block text-sm font-medium text-gray-700 mb-1"
                        >Mã màu *</label
                      >
                      <div class="flex items-center gap-2">
                        <input
                          v-model="variant.colorHex"
                          type="color"
                          class="w-12 h-10 border border-gray-300 rounded-md"
                        />
                        <input
                          v-model="variant.colorHex"
                          type="text"
                          placeholder="#FF0000"
                          class="flex-1 px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                          required
                        />
                      </div>
                    </div>

                    <div>
                      <label
                        class="block text-sm font-medium text-gray-700 mb-1"
                        >Giá biến thể</label
                      >
                      <input
                        v-model="variant.price"
                        type="number"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                    </div>

                    <div class="col-span-2">
                      <label
                        class="block text-sm font-medium text-gray-700 mb-1"
                        >Số lượng tồn kho *</label
                      >
                      <input
                        v-model="variant.stockQuantity"
                        type="number"
                        min="0"
                        class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        required
                      />
                    </div>
                  </div>
                </div>
              </div>

              <div v-else class="text-center py-8 text-gray-500">
                <i class="pi pi-box text-4xl mb-2"></i>
                <p>Chưa có biến thể nào. Hãy thêm biến thể đầu tiên.</p>
              </div>
            </div>

            <div class="flex justify-end gap-3 pt-4 border-t">
              <button
                type="button"
                @click="closeModal"
                class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
              >
                Hủy
              </button>
              <button
                type="submit"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                {{ isEditing ? "Cập nhật" : "Thêm mới" }}
              </button>
            </div>
          </form>
        </template>
      </LoadingGlobal>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import Dialog from "primevue/dialog";
import Toast from "primevue/toast";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputText from "primevue/inputtext";
import Dropdown from "primevue/dropdown";
import Button from "primevue/button";
import Tag from "primevue/tag";
import Tooltip from "primevue/tooltip";
import Image from "primevue/image";
import { useToast } from "primevue/usetoast";
import {
  createProduct,
  getMyProducts,
  type CreateProductPayload,
  type ProductVariant,
  type Product,
} from "@/api/product";
import LoadingGlobal from "@/components/LoadingGlobal.vue";

// Toast setup
const toast = useToast();

// Loading state
const loading = ref(false);
const isSaving = ref(false);

// Pagination state
const first = ref(0);
const itemsPerPage = ref(10);

// Sorting state
const sortField = ref("name");
const sortOrder = ref(1);

const products = ref<Product[]>([]);

const searchQuery = ref("");
const sortBy = ref("name");
const showModal = ref(false);
const isEditing = ref(false);
const currentProduct = ref<Partial<Product>>({});
const imagesPreview = ref<string[]>([]);

// Sort options for dropdown
const sortOptions = ref([
  { label: "Tên sản phẩm", value: "name" },
  { label: "Giá", value: "price" },
  { label: "Tồn kho", value: "stock" },
  { label: "Đánh giá", value: "rating" },
  { label: "Trạng thái", value: "status" },
]);

// Reset pagination when search or sort changes
watch([searchQuery, sortBy], () => {
  first.value = 0;
});

// Load products on component mount
const loadProducts = async () => {
  loading.value = true;
  try {
    const response = await getMyProducts();
    products.value = response;
  } catch (error) {
    console.error("Error loading products:", error);
    toast.add({
      severity: "error",
      summary: "Lỗi",
      detail: "Không thể tải danh sách sản phẩm",
      life: 5000,
    });
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadProducts();
});

const filteredProducts = computed(() => {
  let filtered = [...products.value];

  // Tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(
      (product) =>
        product.name.toLowerCase().includes(query) ||
        product.description.toLowerCase().includes(query)
    );
  }

  return filtered;
});

// Sort handler for DataTable
const onSort = (event: any) => {
  sortField.value = event.sortField;
  sortOrder.value = event.sortOrder;
};

// Get status severity for Tag component
const getStatusSeverity = (status: string | null) => {
  switch (status) {
    case "active":
      return "success";
    case "inactive":
      return "danger";
    default:
      return "warning";
  }
};

const getTotalStock = (product: Product) => {
  return (
    product.variants?.reduce(
      (total, variant) => total + variant.stockQuantity,
      0
    ) || 0
  );
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const getProductImage = (product: Product) => {
  // Return first image URL from images array if available
  if (product.images && product.images.length > 0) {
    return product.images[0];
  }
  // Fallback to placeholder if no images
  return "https://via.placeholder.com/150";
};

const openAddProductModal = () => {
  isEditing.value = false;
  currentProduct.value = {
    name: "",
    description: "",
    price: 0,
    variants: [],
    images: [],
    rating: 0,
    status: null,
  };
  showModal.value = true;
};

const editProduct = (product: Product) => {
  isEditing.value = true;
  currentProduct.value = { ...product };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  currentProduct.value = {};
};

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files.length > 0) {
    // Khởi tạo mảng nếu chưa có
    if (!currentProduct.value.images) {
      currentProduct.value.images = [];
    }
    // Xóa preview cũ nếu cần
    imagesPreview.value = [];
    // Process each selected file
    Array.from(target.files).forEach((file: File) => {
      // Lưu file gốc vào mảng images để upload
      currentProduct.value.images?.push(file);
      // Tạo preview cho UI
      const reader = new FileReader();
      reader.onload = (e) => {
        const imageData = e.target?.result as string;
        imagesPreview.value.push(imageData);
      };
      reader.readAsDataURL(file);
    });
  }
};

const removeImage = (index: number) => {
  if (
    currentProduct.value.images &&
    Array.isArray(currentProduct.value.images)
  ) {
    currentProduct.value.images.splice(index, 1);
  }
  if (imagesPreview.value && Array.isArray(imagesPreview.value)) {
    imagesPreview.value.splice(index, 1);
  }
};

const addVariant = () => {
  if (!currentProduct.value.variants) {
    currentProduct.value.variants = [];
  }

  currentProduct.value.variants.push({
    size: "",
    colorName: "",
    colorHex: "#000000",
    price: currentProduct.value.price || 0,
    stockQuantity: 0,
  });
};

const removeVariant = (index: number) => {
  if (currentProduct.value.variants) {
    currentProduct.value.variants.splice(index, 1);
  }
};

const saveProduct = async () => {
  // Validate variants
  if (
    !currentProduct.value.variants ||
    currentProduct.value.variants.length === 0
  ) {
    toast.add({
      severity: "warn",
      summary: "Cảnh báo",
      detail: "Vui lòng thêm ít nhất một biến thể sản phẩm",
      life: 3000,
    });
    return;
  }

  // Validate each variant
  for (const variant of currentProduct.value.variants) {
    if (
      !variant.size ||
      !variant.colorName ||
      !variant.colorHex ||
      variant.stockQuantity < 0
    ) {
      toast.add({
        severity: "error",
        summary: "Lỗi",
        detail: "Vui lòng điền đầy đủ thông tin cho tất cả biến thể",
        life: 3000,
      });
      return;
    }
  }

  isSaving.value = true;

  if (isEditing.value) {
    // TODO: Implement API call to update product
    const index = products.value.findIndex(
      (p) => p.id === currentProduct.value.id
    );
    if (index !== -1) {
      products.value[index] = {
        ...products.value[index],
        ...currentProduct.value,
      };
    }
    toast.add({
      severity: "success",
      summary: "Thành công",
      detail: "Sản phẩm đã được cập nhật",
      life: 3000,
    });
    closeModal();
  } else {
    // Prepare payload for API
    const productPayload: CreateProductPayload = {
      name: currentProduct.value.name || "",
      description: currentProduct.value.description || "",
      price: currentProduct.value.price || 0,
      variants: currentProduct.value.variants || [],
      images: currentProduct.value.images || [],
    };

    // Call API to create product
    createProduct(productPayload)
      .then((response) => {
        console.log("Product created successfully:", response);

        toast.add({
          severity: "success",
          summary: "Thành công",
          detail: "Sản phẩm đã được tạo thành công",
          life: 3000,
        });

        closeModal();
        // Reload products to get the updated list
        loadProducts();
      })
      .catch((error) => {
        console.error("Error creating product:", error);

        toast.add({
          severity: "error",
          summary: "Lỗi",
          detail: "Có lỗi xảy ra khi tạo sản phẩm. Vui lòng thử lại.",
          life: 5000,
        });
      })
      .finally(() => {
        isSaving.value = false;
      });
  }
};

const deleteProduct = (product: Product) => {
  if (confirm("Bạn có chắc chắn muốn xóa sản phẩm này?")) {
    // TODO: Implement API call to delete product
    products.value = products.value.filter((p) => p.id !== product.id);

    toast.add({
      severity: "success",
      summary: "Thành công",
      detail: "Sản phẩm đã được xóa",
      life: 3000,
    });
  }
};
</script>
