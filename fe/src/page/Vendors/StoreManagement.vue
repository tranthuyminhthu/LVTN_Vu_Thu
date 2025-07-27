<template>
  <div class="rounded-lg">
    <Toast />
    
    <div
      class="bg-white rounded-xl shadow flex items-center justify-between p-6 mb-6"
    >
      <div class="flex items-center gap-4">
        <div class="relative group">
          <img
            :src="storeImage || 'https://randomuser.me/api/portraits/men/32.jpg'"
            alt="Store Avatar"
            class="w-16 h-16 rounded-full border-4 border-blue-100 object-cover cursor-pointer transition-all duration-200 group-hover:opacity-80"
            @click="triggerFileInput"
          />
          <div 
            class="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity duration-200 pointer-events-none"
          >
            <div class="bg-black bg-opacity-50 rounded-full p-2">
              <i class="pi pi-camera text-white text-sm"></i>
            </div>
          </div>
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            class="hidden"
            @change="handleImageUpload"
          />
        </div>
        <div>
          <div class="text-2xl font-bold text-gray-800">{{ storeName }}</div>
          <div class="text-gray-500">{{ storeDescription }}</div>
          <div class="flex items-center gap-2 mt-1">
            <span class="text-xs text-gray-400">Click vào ảnh để thay đổi</span>
            <button 
              @click="triggerFileInput"
              class="text-xs text-blue-500 hover:text-blue-700 underline"
            >
              Upload Ảnh
            </button>
            <button 
              @click="testClick"
              class="text-xs text-blue-500 hover:text-blue-700 underline"
            >
              Test Click
            </button>
          </div>
        </div>
      </div>
      <div class="flex items-center gap-3">
        <button 
          @click="editStoreInfo"
          class="text-gray-500 hover:text-blue-600 text-xl transition-colors"
          title="Chỉnh sửa thông tin cửa hàng"
        >
          <i class="pi pi-pencil"></i>
        </button>
        <button 
          @click="viewStore"
          class="text-gray-500 hover:text-blue-600 text-2xl transition-colors"
          title="Xem cửa hàng"
        >
          <i class="pi pi-external-link"></i>
        </button>
      </div>
    </div>

    <!-- Edit Store Modal -->
    <Dialog
      v-model:visible="showEditModal"
      modal
      header="Chỉnh sửa thông tin cửa hàng"
      :style="{ width: '500px' }"
      class="p-4"
    >
      <div class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Tên cửa hàng</label>
          <InputText
            v-model="editingStoreName"
            placeholder="Nhập tên cửa hàng"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Mô tả cửa hàng</label>
          <Textarea
            v-model="editingStoreDescription"
            placeholder="Nhập mô tả cửa hàng"
            rows="3"
            class="w-full"
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-2">Ảnh cửa hàng</label>
          <div class="flex items-center gap-4">
            <img
              :src="editingStoreImage || storeImage || 'https://randomuser.me/api/portraits/men/32.jpg'"
              alt="Preview"
              class="w-16 h-16 rounded-full border-2 border-gray-200 object-cover"
            />
            <button
              @click="triggerEditFileInput"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <i class="pi pi-upload mr-2"></i>Chọn ảnh
            </button>
            <button
              @click="testClick"
              class="px-2 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600 transition-colors text-xs"
            >
              Test
            </button>
            <input
              ref="editFileInput"
              type="file"
              accept="image/*"
              class="hidden"
              @change="handleEditImageUpload"
            />
          </div>
        </div>
      </div>
      <template #footer>
        <div class="flex justify-end gap-3">
          <Button
            label="Hủy"
            severity="secondary"
            @click="showEditModal = false"
          />
          <Button
            label="Lưu thay đổi"
            @click="saveStoreInfo"
            :loading="saving"
          />
        </div>
      </template>
    </Dialog>

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
        class="bg-white rounded-xl shadow p-6 flex items-center justify-center"
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
import { ref } from 'vue';
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import Dialog from "primevue/dialog";
import Button from "primevue/button";
import InputText from "primevue/inputtext";
import Textarea from "primevue/textarea";
import Toast from "primevue/toast";
import { useToast } from "primevue/usetoast";

const toast = useToast();

interface Order {
  id: string;
  customer: string;
  date: string;
  amount: string;
  status: string;
}

// Store info
const storeName = ref('Vendor Store');
const storeDescription = ref('Premium Electronics Store');
const storeImage = ref<string | null>(null);

// Edit modal
const showEditModal = ref(false);
const editingStoreName = ref('');
const editingStoreDescription = ref('');
const editingStoreImage = ref<string | null>(null);
const saving = ref(false);

// File inputs
const fileInput = ref<HTMLInputElement>();
const editFileInput = ref<HTMLInputElement>();

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
    return `<span class='bg-green-100 text-green-700 px-3 py-1 rounded-full text-xs font-medium'>Delivered</span>`;
  } else if (row.status === "Processing") {
    return `<span class='bg-blue-100 text-blue-600 px-3 py-1 rounded-full text-xs font-medium'>Processing</span>`;
  } else {
    return `<span class='bg-blue-50 text-blue-500 px-3 py-1 rounded-full text-xs font-medium'>Pending</span>`;
  }
};

// Image upload functions
const triggerFileInput = () => {
  console.log('Triggering file input...');
  if (fileInput.value) {
    fileInput.value.click();
  } else {
    console.error('File input not found');
  }
};

const triggerEditFileInput = () => {
  console.log('Triggering edit file input...');
  if (editFileInput.value) {
    editFileInput.value.click();
  } else {
    console.error('Edit file input not found');
  }
};

const handleImageUpload = (event: Event) => {
  console.log('Handling image upload...');
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  
  if (file) {
    console.log('File selected:', file.name, file.size);
    
    if (file.size > 5 * 1024 * 1024) { // 5MB limit
      toast.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Kích thước ảnh không được vượt quá 5MB',
        life: 3000
      });
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      console.log('File read successfully');
      storeImage.value = e.target?.result as string;
      toast.add({
        severity: 'success',
        summary: 'Thành công',
        detail: 'Đã cập nhật ảnh cửa hàng',
        life: 2000
      });
    };
    reader.onerror = () => {
      console.error('Error reading file');
      toast.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Không thể đọc file ảnh',
        life: 3000
      });
    };
    reader.readAsDataURL(file);
  } else {
    console.log('No file selected');
  }
};

const handleEditImageUpload = (event: Event) => {
  console.log('Handling edit image upload...');
  const target = event.target as HTMLInputElement;
  const file = target.files?.[0];
  
  if (file) {
    console.log('Edit file selected:', file.name, file.size);
    
    if (file.size > 5 * 1024 * 1024) { // 5MB limit
      toast.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Kích thước ảnh không được vượt quá 5MB',
        life: 3000
      });
      return;
    }

    const reader = new FileReader();
    reader.onload = (e) => {
      console.log('Edit file read successfully');
      editingStoreImage.value = e.target?.result as string;
      toast.add({
        severity: 'success',
        summary: 'Thành công',
        detail: 'Đã chọn ảnh mới',
        life: 2000
      });
    };
    reader.onerror = () => {
      console.error('Error reading edit file');
      toast.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Không thể đọc file ảnh',
        life: 3000
      });
    };
    reader.readAsDataURL(file);
  } else {
    console.log('No edit file selected');
  }
};

// Store management functions
const editStoreInfo = () => {
  editingStoreName.value = storeName.value;
  editingStoreDescription.value = storeDescription.value;
  editingStoreImage.value = storeImage.value;
  showEditModal.value = true;
};

const saveStoreInfo = async () => {
  saving.value = true;
  
  try {
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000));
    
    storeName.value = editingStoreName.value;
    storeDescription.value = editingStoreDescription.value;
    if (editingStoreImage.value) {
      storeImage.value = editingStoreImage.value;
    }
    
    showEditModal.value = false;
    
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Đã cập nhật thông tin cửa hàng',
      life: 3000
    });
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể cập nhật thông tin cửa hàng',
      life: 3000
    });
  } finally {
    saving.value = false;
  }
};

const viewStore = () => {
  // TODO: Implement view store functionality
  toast.add({
    severity: 'info',
    summary: 'Thông báo',
    detail: 'Chức năng xem cửa hàng đang được phát triển',
    life: 2000
  });
};

const testClick = () => {
  console.log('Test click working!');
  toast.add({
    severity: 'info',
    summary: 'Test',
    detail: 'Click function is working!',
    life: 2000
  });
};
</script>

<style scoped>
/* Custom styles for image upload */
.group:hover .group-hover\:opacity-80 {
  opacity: 0.8;
}

.group:hover .group-hover\:opacity-100 {
  opacity: 1;
}
</style>
