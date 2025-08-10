<template>
  <div class="bg-white rounded-lg p-4 min-h-[500px]">
    <p class="text-2xl font-bold mb-4">Quản lý người dùng</p>

    <div class="flex gap-4 mb-4">
      <InputText v-model="searchQuery" placeholder="Tìm kiếm người dùng..." class="w-64" />
      <Select v-model="selectedRole" :options="userRoles" optionLabel="name" placeholder="Vai trò" class="w-48" />
      <Select v-model="selectedStatus" :options="userStatuses" optionLabel="name" placeholder="Trạng thái" class="w-48" />
    </div>

    <Tabs value="0">
      <TabList>
        <Tab value="0">Người dùng</Tab>
        <Tab value="1">Người bán</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <DataTable
            :value="users"
            tableStyle="min-width: 50rem"
            class="bg-white rounded-lg"
            :paginator="true"
            :rows="usersPagination.size"
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :loading="usersLoading"
            :filters="filters"
            filterDisplay="menu"
            :totalRecords="usersPagination.totalElements"
            :first="usersPagination.page * usersPagination.size"
            :lazy="true"
            @page="onUserPageChange"
          >
            <Column field="userId" header="ID" sortable></Column>
            <Column field="fullName" header="Họ tên" sortable>
              <template #body="slotProps">
                {{ slotProps.data.fullName || slotProps.data.username || 'Chưa có tên' }}
              </template>
            </Column>
            <Column field="email" header="Email" sortable></Column>
            <Column field="username" header="Tên đăng nhập" sortable></Column>
            <Column field="role" header="Vai trò" sortable>
              <template #body="slotProps">
                <Tag :severity="getRoleSeverity(slotProps.data.role || 'USER')" :value="getRoleDisplayName(slotProps.data.role)" />
              </template>
            </Column>
            <Column field="type" header="Loại tài khoản" sortable>
              <template #body="slotProps">
                <Tag :severity="getTypeSeverity(slotProps.data.type)" :value="getTypeDisplayName(slotProps.data.type)" />
              </template>
            </Column>
            <Column header="Thao tác">
              <template #body="slotProps">
                <div class="flex gap-2">
                  <Button icon="pi pi-eye" severity="info" @click="viewUserDetails(slotProps.data)" />
                  <Button icon="pi pi-trash" severity="danger" @click="deleteUser(slotProps.data)" />
                </div>
              </template>
            </Column>
          </DataTable>
        </TabPanel>
        <TabPanel value="1">
          <div class="flex gap-4 mb-4">
            <InputText v-model="vendorSearchQuery" placeholder="Tìm kiếm cửa hàng..." class="w-64" />
            <Select v-model="selectedVendorStatus" :options="vendorStatusOptions" optionLabel="name" optionValue="value" placeholder="Trạng thái" class="w-48" />
            <Button 
              v-if="selectedVendors.length > 0"
              icon="pi pi-check" 
              label="Duyệt đã chọn" 
              severity="success" 
              @click="acceptSelectedVendors"
              :loading="bulkActionLoading"
            />
          </div>
          <DataTable
            :value="vendors"
            v-model:selection="selectedVendors"
            selectionMode="multiple"
            :metaKeySelection="false"
            tableStyle="min-width: 50rem"
            class="bg-white rounded-lg"
            :paginator="true"
            :rows="vendorsPagination.size"
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :loading="vendorsLoading"
            :filters="filters"
            filterDisplay="menu"
            :totalRecords="vendorsPagination.totalElements"
            :first="vendorsPagination.page * vendorsPagination.size"
            :lazy="true"
            @page="onVendorPageChange"
          >
            <Column field="id" header="ID" sortable></Column>
            <Column field="name" header="Tên cửa hàng" sortable>
              <template #body="slotProps">
                {{ slotProps.data.name || 'Chưa có tên' }}
              </template>
            </Column>
            <Column field="email" header="Email" sortable>
              <template #body="slotProps">
                {{ slotProps.data.email || 'Chưa có email' }}
              </template>
            </Column>
            <Column field="phone" header="Số điện thoại">
              <template #body="slotProps">
                {{ slotProps.data.phone || 'Chưa có số điện thoại' }}
              </template>
            </Column>
            <Column field="status" header="Trạng thái" sortable>
              <template #body="slotProps">
                <Tag :severity="getStatusSeverity(slotProps.data.status)" :value="getStatusDisplayName(slotProps.data.status)" />
              </template>
            </Column>
            <Column field="createdAt" header="Ngày tạo" sortable>
              <template #body="slotProps">
                {{ formatDate(new Date(slotProps.data.createdAt)) }}
              </template>
            </Column>
            <Column header="Thao tác">
              <template #body="slotProps">
                <div class="flex gap-2">
                  <Button icon="pi pi-eye" severity="info" @click="viewVendorDetails(slotProps.data)" />
                  <Button 
                    v-if="slotProps.data.status === 'APPROVED'" 
                    icon="pi pi-ban" 
                    severity="warning" 
                    @click="updateVendorStatus(slotProps.data, 'SUSPENDED')" 
                  />
                  <Button 
                    v-if="slotProps.data.status === 'PENDING'" 
                    icon="pi pi-check" 
                    severity="success" 
                    @click="updateVendorStatus(slotProps.data, 'APPROVED')" 
                  />
                  <Button 
                    v-if="slotProps.data.status === 'SUSPENDED'" 
                    icon="pi pi-check" 
                    severity="success" 
                    @click="updateVendorStatus(slotProps.data, 'APPROVED')" 
                  />
                  <Button icon="pi pi-times" severity="danger" @click="rejectVendor(slotProps.data)" title="Từ chối" />
                </div>
              </template>
            </Column>
          </DataTable>
        </TabPanel>
      </TabPanels>
    </Tabs>

    <Dialog v-model:visible="userDetailsVisible" modal header="Chi tiết người dùng" :style="{ width: '50vw' }">
      <div v-if="selectedUser" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin cá nhân</p>
            <p>Họ tên: {{ selectedUser.fullName || 'Chưa có tên' }}</p>
            <p>Email: {{ selectedUser.email }}</p>
            <p>Tên đăng nhập: {{ selectedUser.username }}</p>
            <p>Ngày sinh: {{ selectedUser.dob || 'Chưa có thông tin' }}</p>
            <p>Giới tính: {{ selectedUser.gender || 'Chưa có thông tin' }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin tài khoản</p>
            <p>ID: {{ selectedUser.userId }}</p>
            <p>Vai trò: <Tag :severity="getRoleSeverity(selectedUser.role || 'USER')" :value="getRoleDisplayName(selectedUser.role)" /></p>
            <p>Loại tài khoản: <Tag :severity="getTypeSeverity(selectedUser.type)" :value="getTypeDisplayName(selectedUser.type)" /></p>
            <p>Tên cửa hàng: {{ selectedUser.shopName || 'Không có' }}</p>
          </div>
        </div>
      </div>
    </Dialog>

    <Dialog v-model:visible="vendorDetailsVisible" modal header="Chi tiết người bán" :style="{ width: '50vw' }">
      <div v-if="selectedVendor" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin cửa hàng</p>
            <p>Tên cửa hàng: {{ selectedVendor.name || 'Chưa có tên' }}</p>
            <p>Mô tả: {{ selectedVendor.description || 'Chưa có mô tả' }}</p>
            <p>Địa chỉ: {{ selectedVendor.address || 'Chưa có địa chỉ' }}</p>
            <p>Email: {{ selectedVendor.email || 'Chưa có email' }}</p>
            <p>Số điện thoại: {{ selectedVendor.phone || 'Chưa có số điện thoại' }}</p>
            <p>Ngày tạo: {{ formatDate(new Date(selectedVendor.createdAt)) }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin tài khoản</p>
            <p>Trạng thái: <Tag :severity="getStatusSeverity(selectedVendor.status)" :value="getStatusDisplayName(selectedVendor.status)" /></p>
            <p>ID: {{ selectedVendor.id }}</p>
            <p>Cập nhật lần cuối: {{ formatDate(new Date(selectedVendor.updatedAt)) }}</p>
          </div>
        </div>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import Button from 'primevue/button';
import InputText from 'primevue/inputtext';
import Select from 'primevue/select';
import Tag from 'primevue/tag';
import Dialog from 'primevue/dialog';
import Divider from 'primevue/divider';
import Tabs from 'primevue/tabs';
import TabList from 'primevue/tablist';
import Tab from 'primevue/tab';
import TabPanels from 'primevue/tabpanels';
import TabPanel from 'primevue/tabpanel';
import { useToast } from 'primevue/usetoast';
import MultiSelect from 'primevue/multiselect';
import { vendorApi, type Vendor as ApiVendor } from '@/api/vendor';
import { userApi, type UserProfile as ApiUser } from '@/api/user';

const toast = useToast();
const loading = ref(false);
const searchQuery = ref('');
const selectedRole = ref(null);
const selectedStatus = ref(null);
const filters = ref({});
const userDetailsVisible = ref(false);
const vendorDetailsVisible = ref(false);
const selectedUser = ref<ApiUser | null>(null);
const selectedVendor = ref<ApiVendor | null>(null);
const activityTimeRange = ref<TimeRange | null>(null);
const activityTypes = ref<ActivityType[]>([]);
const activeTab = ref('0');

interface ActivityType {
  name: string;
  value: string;
}

interface TimeRange {
  name: string;
  value: string;
}

interface Activity {
  date: Date;
  action: string;
  details: string;
  type: string;
  ip?: string;
  device?: string;
}

interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: string;
  status: string;
  joinDate: Date;
  activityHistory: Activity[];
}

interface Vendor {
  id: string;
  name: string;
  owner: string;
  email: string;
  phone: string;
  status: string;
  joinDate: Date;
  productCount: number;
  rating: number;
  reviewCount: number;
  salesStats: {
    period: string;
    orders: number;
    revenue: number;
  }[];
}

const userRoles = [
  { name: 'Người dùng' },
  { name: 'Admin' },
  { name: 'Moderator' }
];

const userStatuses = [
  { name: 'Hoạt động' },
  { name: 'Bị khóa' }
];

// API data for users
const users = ref<ApiUser[]>([]);
const usersLoading = ref(false);
const usersPagination = ref({
  page: 0,
  size: 10,
  totalElements: 0,
  totalPages: 0
});

// Load users from API
const loadUsers = async () => {
  try {
    usersLoading.value = true;
    const params: { page: number; size: number } = {
      page: usersPagination.value.page,
      size: usersPagination.value.size
    };
    
    const response = await userApi.getUsers(params);
    console.log('Users API Response:', response);
    users.value = response.profiles;
    usersPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages
    };
    console.log('Users Pagination state:', {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      first: response.page * response.size
    });
  } catch (error) {
    console.error('Error loading users:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách người dùng',
      life: 3000
    });
  } finally {
    usersLoading.value = false;
  }
};

// Handle user pagination
const onUserPageChange = (event: { first: number; rows: number; page: number }) => {
  console.log('User page change event:', event);
  usersPagination.value.page = event.page;
  usersPagination.value.size = event.rows;
  loadUsers();
};

// API data
const vendors = ref<ApiVendor[]>([]);
const vendorsLoading = ref(false);
const vendorsPagination = ref({
  page: 0,
  size: 5, // Match with API default size
  totalElements: 0,
  totalPages: 0
});



// Filter states
const selectedVendorStatus = ref<'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' | null>(null);
const vendorSearchQuery = ref('');

// Selection states
const selectedVendors = ref<ApiVendor[]>([]);
const bulkActionLoading = ref(false);

// Vendor status options
const vendorStatusOptions = [
  { name: 'Chờ duyệt', value: 'PENDING' as const },
  { name: 'Đã duyệt', value: 'APPROVED' as const },
  { name: 'Từ chối', value: 'REJECTED' as const },
  { name: 'Tạm khóa', value: 'SUSPENDED' as const }
];

// Load vendors from API
const loadVendors = async () => {
  try {
    vendorsLoading.value = true;
    const params: { page: number; size: number; status?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED' } = {
      page: vendorsPagination.value.page,
      size: vendorsPagination.value.size
    };
    
    if (selectedVendorStatus.value) {
      params.status = selectedVendorStatus.value;
    }
    
    const response = await vendorApi.getVendors(params);
    console.log('API Response:', response);
    vendors.value = response.vendors;
    vendorsPagination.value = {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages
    };
    console.log('Pagination state:', {
      page: response.page,
      size: response.size,
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      first: response.page * response.size
    });
  } catch (error) {
    console.error('Error loading vendors:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách người bán',
      life: 3000
    });
  } finally {
    vendorsLoading.value = false;
  }
};

// Watch for filter changes
watch([selectedVendorStatus], () => {
  vendorsPagination.value.page = 0; // Reset to first page
  loadVendors();
});

// Load data on mount
onMounted(() => {
  loadUsers();
  loadVendors();
});

// Handle vendor pagination
const onVendorPageChange = (event: { first: number; rows: number; page: number }) => {
  console.log('Page change event:', event);
  vendorsPagination.value.page = event.page;
  vendorsPagination.value.size = event.rows;
  loadVendors();
};

const formatDate = (date: Date) => {
  return new Intl.DateTimeFormat('vi-VN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(date);
};

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const getRoleSeverity = (role: string): 'info' | 'success' | 'warning' | 'danger' | 'contrast' => {
  switch (role) {
    case 'Admin':
      return 'danger';
    case 'Moderator':
      return 'warning';
    case 'Người dùng':
      return 'info';
    default:
      return 'info';
  }
};

const getStatusSeverity = (status: string): 'info' | 'success' | 'danger' | 'warning' | 'contrast' => {
  switch (status) {
    case 'APPROVED':
      return 'success';
    case 'PENDING':
      return 'warning';
    case 'REJECTED':
      return 'danger';
    case 'SUSPENDED':
      return 'danger';
    case 'Hoạt động':
      return 'success';
    case 'Bị khóa':
      return 'danger';
    default:
      return 'info';
  }
};

const getStatusDisplayName = (status: string): string => {
  switch (status) {
    case 'APPROVED':
      return 'Đã duyệt';
    case 'PENDING':
      return 'Chờ duyệt';
    case 'REJECTED':
      return 'Từ chối';
    case 'SUSPENDED':
      return 'Tạm khóa';
    case 'Hoạt động':
      return 'Hoạt động';
    case 'Bị khóa':
      return 'Bị khóa';
    default:
      return status;
  }
};

const getRoleDisplayName = (role: string | null): string => {
  if (!role) return 'Người dùng';
  switch (role) {
    case 'ADMIN':
      return 'Admin';
    case 'VENDOR':
      return 'Người bán';
    case 'USER':
      return 'Người dùng';
    default:
      return role;
  }
};

const getTypeDisplayName = (type: string | null): string => {
  if (!type) return 'Chưa xác định';
  switch (type) {
    case 'CUSTOMER':
      return 'Khách hàng';
    case 'VENDOR':
      return 'Người bán';
    case 'ADMIN':
      return 'Quản trị viên';
    default:
      return type;
  }
};

const getTypeSeverity = (type: string | null): 'info' | 'success' | 'warning' | 'danger' | 'contrast' => {
  if (!type) return 'info';
  switch (type) {
    case 'ADMIN':
      return 'danger';
    case 'VENDOR':
      return 'warning';
    case 'CUSTOMER':
      return 'success';
    default:
      return 'info';
  }
};

const activityTypeOptions: ActivityType[] = [
  { name: 'Đăng nhập', value: 'login' },
  { name: 'Đăng xuất', value: 'logout' },
  { name: 'Đặt hàng', value: 'order' },
  { name: 'Thanh toán', value: 'payment' },
  { name: 'Cập nhật thông tin', value: 'update_profile' },
  { name: 'Đánh giá sản phẩm', value: 'review' },
  { name: 'Bình luận', value: 'comment' },
  { name: 'Theo dõi cửa hàng', value: 'follow_vendor' },
  { name: 'Quản lý đơn hàng', value: 'order_management' },
  { name: 'Quản lý người dùng', value: 'user_management' }
];

const timeRangeOptions: TimeRange[] = [
  { name: 'Tất cả', value: 'all' },
  { name: 'Hôm nay', value: 'today' },
  { name: 'Tuần này', value: 'this_week' },
  { name: 'Tháng này', value: 'this_month' },
  { name: '3 tháng gần nhất', value: 'last_3_months' }
];

const viewUserDetails = (user: ApiUser) => {
  selectedUser.value = user;
  activityTimeRange.value = timeRangeOptions[0];
  activityTypes.value = [];
  userDetailsVisible.value = true;
};

const getFilteredActivityHistory = (user: User) => {
  if (!user) return [];
  
  let filtered = [...user.activityHistory];
  
  // Filter by time range
  if (activityTimeRange.value?.value !== 'all') {
    const now = new Date();
    const startDate = new Date();
    
    switch (activityTimeRange.value?.value) {
      case 'today':
        startDate.setHours(0, 0, 0, 0);
        break;
      case 'this_week':
        startDate.setDate(now.getDate() - now.getDay());
        break;
      case 'this_month':
        startDate.setDate(1);
        break;
      case 'last_3_months':
        startDate.setMonth(now.getMonth() - 3);
        break;
    }
    
    filtered = filtered.filter(activity => activity.date >= startDate);
  }
  
  // Filter by activity types
  if (activityTypes.value.length > 0) {
    filtered = filtered.filter(activity => 
      activityTypes.value.some(type => activity.type === type.value)
    );
  }
  
  return filtered.sort((a, b) => b.date.getTime() - a.date.getTime());
};

const viewVendorDetails = (vendor: ApiVendor) => {
  selectedVendor.value = vendor;
  vendorDetailsVisible.value = true;
};

const updateUserStatus = async (user: ApiUser, newStatus: string) => {
  try {
    await userApi.updateUserStatus(user.userId, newStatus);
    // Reload users to get updated data
    await loadUsers();
    toast.add({
      severity: 'success',
      summary: 'Cập nhật thành công',
      detail: `Người dùng ${user.fullName || user.username} đã được cập nhật trạng thái thành ${newStatus}`,
      life: 3000
    });
  } catch (error) {
    console.error('Error updating user status:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể cập nhật trạng thái người dùng',
      life: 3000
    });
  }
};

const updateVendorStatus = async (vendor: ApiVendor, newStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | 'SUSPENDED') => {
  try {
    await vendorApi.updateVendorStatus(vendor.id, newStatus);
    // Reload vendors to get updated data
    await loadVendors();
    toast.add({
      severity: 'success',
      summary: 'Cập nhật thành công',
      detail: `Cửa hàng ${vendor.name || 'N/A'} đã được cập nhật trạng thái thành ${newStatus}`,
      life: 3000
    });
  } catch (error) {
    console.error('Error updating vendor status:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể cập nhật trạng thái cửa hàng',
      life: 3000
    });
  }
};

const deleteUser = async (user: ApiUser) => {
  try {
    await userApi.deleteUser(user.userId);
    // Reload users to get updated data
    await loadUsers();
    toast.add({
      severity: 'success',
      summary: 'Xóa thành công',
      detail: `Người dùng ${user.fullName || user.username} đã được xóa`,
      life: 3000
    });
  } catch (error) {
    console.error('Error deleting user:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể xóa người dùng',
      life: 3000
    });
  }
};

const rejectVendor = async (vendor: ApiVendor) => {
  try {
    await vendorApi.updateVendorStatus(vendor.id, 'REJECTED');
    // Reload vendors to get updated data
    await loadVendors();
    toast.add({
      severity: 'success',
      summary: 'Từ chối thành công',
      detail: `Cửa hàng ${vendor.name || 'N/A'} đã được từ chối`,
      life: 3000
    });
  } catch (error) {
    console.error('Error rejecting vendor:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể từ chối cửa hàng',
      life: 3000
    });
  }
};

const acceptSelectedVendors = async () => {
  if (selectedVendors.value.length === 0) {
    toast.add({
      severity: 'warning',
      summary: 'Cảnh báo',
      detail: 'Vui lòng chọn ít nhất một cửa hàng để duyệt',
      life: 3000
    });
    return;
  }

  try {
    bulkActionLoading.value = true;
    const vendorIds = selectedVendors.value.map(vendor => vendor.id);
    
    await vendorApi.acceptVendors(vendorIds);
    
    // Clear selection and reload data
    selectedVendors.value = [];
    await loadVendors();
    
    toast.add({
      severity: 'success',
      summary: 'Duyệt thành công',
      detail: `Đã duyệt ${vendorIds.length} cửa hàng`,
      life: 3000
    });
  } catch (error) {
    console.error('Error accepting vendors:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể duyệt các cửa hàng đã chọn',
      life: 3000
    });
  } finally {
    bulkActionLoading.value = false;
  }
};
</script>

<style scoped>
.p-datatable .p-datatable-tbody > tr > td {
  padding: 1rem;
}
</style>
