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
            :rows="10"
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :loading="loading"
            :filters="filters"
            filterDisplay="menu"
          >
            <Column field="id" header="ID" sortable></Column>
            <Column field="name" header="Họ tên" sortable></Column>
            <Column field="email" header="Email" sortable></Column>
            <Column field="phone" header="Số điện thoại"></Column>
            <Column field="role" header="Vai trò" sortable>
              <template #body="slotProps">
                <Tag :severity="getRoleSeverity(slotProps.data.role)" :value="slotProps.data.role" />
              </template>
            </Column>
            <Column field="status" header="Trạng thái" sortable>
              <template #body="slotProps">
                <Tag :severity="getStatusSeverity(slotProps.data.status)" :value="slotProps.data.status" />
              </template>
            </Column>
            <Column field="joinDate" header="Ngày tham gia" sortable>
              <template #body="slotProps">
                {{ formatDate(slotProps.data.joinDate) }}
              </template>
            </Column>
            <Column header="Thao tác">
              <template #body="slotProps">
                <div class="flex gap-2">
                  <Button icon="pi pi-eye" severity="info" @click="viewUserDetails(slotProps.data)" />
                  <Button 
                    v-if="slotProps.data.status === 'Hoạt động'" 
                    icon="pi pi-ban" 
                    severity="warning" 
                    @click="updateUserStatus(slotProps.data, 'Bị khóa')" 
                  />
                  <Button 
                    v-if="slotProps.data.status === 'Bị khóa'" 
                    icon="pi pi-check" 
                    severity="success" 
                    @click="updateUserStatus(slotProps.data, 'Hoạt động')" 
                  />
                  <Button icon="pi pi-trash" severity="danger" @click="deleteUser(slotProps.data)" />
                </div>
              </template>
            </Column>
          </DataTable>
        </TabPanel>
        <TabPanel value="1">
          <DataTable
            :value="vendors"
            tableStyle="min-width: 50rem"
            class="bg-white rounded-lg"
            :paginator="true"
            :rows="10"
            :rowsPerPageOptions="[5, 10, 20, 50]"
            :loading="loading"
            :filters="filters"
            filterDisplay="menu"
          >
            <Column field="id" header="ID" sortable></Column>
            <Column field="name" header="Tên cửa hàng" sortable></Column>
            <Column field="owner" header="Chủ cửa hàng" sortable></Column>
            <Column field="email" header="Email" sortable></Column>
            <Column field="phone" header="Số điện thoại"></Column>
            <Column field="status" header="Trạng thái" sortable>
              <template #body="slotProps">
                <Tag :severity="getStatusSeverity(slotProps.data.status)" :value="slotProps.data.status" />
              </template>
            </Column>
            <Column field="joinDate" header="Ngày tham gia" sortable>
              <template #body="slotProps">
                {{ formatDate(slotProps.data.joinDate) }}
              </template>
            </Column>
            <Column header="Thao tác">
              <template #body="slotProps">
                <div class="flex gap-2">
                  <Button icon="pi pi-eye" severity="info" @click="viewVendorDetails(slotProps.data)" />
                  <Button 
                    v-if="slotProps.data.status === 'Hoạt động'" 
                    icon="pi pi-ban" 
                    severity="warning" 
                    @click="updateVendorStatus(slotProps.data, 'Bị khóa')" 
                  />
                  <Button 
                    v-if="slotProps.data.status === 'Bị khóa'" 
                    icon="pi pi-check" 
                    severity="success" 
                    @click="updateVendorStatus(slotProps.data, 'Hoạt động')" 
                  />
                  <Button icon="pi pi-trash" severity="danger" @click="deleteVendor(slotProps.data)" />
                </div>
              </template>
            </Column>
          </DataTable>
        </TabPanel>
      </TabPanels>
    </Tabs>

    <Dialog v-model:visible="userDetailsVisible" modal header="Chi tiết người dùng" :style="{ width: '70vw' }">
      <div v-if="selectedUser" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin cá nhân</p>
            <p>Họ tên: {{ selectedUser.name }}</p>
            <p>Email: {{ selectedUser.email }}</p>
            <p>Số điện thoại: {{ selectedUser.phone }}</p>
            <p>Ngày tham gia: {{ formatDate(selectedUser.joinDate) }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin tài khoản</p>
            <p>Vai trò: <Tag :severity="getRoleSeverity(selectedUser.role)" :value="selectedUser.role" /></p>
            <p>Trạng thái: <Tag :severity="getStatusSeverity(selectedUser.status)" :value="selectedUser.status" /></p>
          </div>
        </div>
        <Divider />
        <div class="flex justify-content-between align-items-center mb-3">
          <p class="font-bold m-0">Lịch sử hoạt động</p>
          <div class="flex gap-2">
            <Select v-model="activityTimeRange" :options="timeRangeOptions" optionLabel="name" placeholder="Thời gian" class="w-48" />
            <MultiSelect v-model="activityTypes" :options="activityTypeOptions" optionLabel="name" placeholder="Loại hoạt động" class="w-48" />
          </div>
        </div>
        <DataTable :value="getFilteredActivityHistory(selectedUser)" class="mt-2" :paginator="true" :rows="10">
          <Column field="date" header="Thời gian" sortable>
            <template #body="slotProps">
              {{ formatDate(slotProps.data.date) }}
            </template>
          </Column>
          <Column field="type" header="Loại hoạt động" sortable>
            <template #body="slotProps">
              <Tag :value="activityTypeOptions.find(t => t.value === slotProps.data.type)?.name || slotProps.data.type" />
            </template>
          </Column>
          <Column field="action" header="Hành động" sortable></Column>
          <Column field="details" header="Chi tiết"></Column>
          <Column field="ip" header="IP" v-if="selectedUser.role === 'Admin'"></Column>
          <Column field="device" header="Thiết bị" v-if="selectedUser.role === 'Admin'"></Column>
        </DataTable>
      </div>
    </Dialog>

    <Dialog v-model:visible="vendorDetailsVisible" modal header="Chi tiết người bán" :style="{ width: '50vw' }">
      <div v-if="selectedVendor" class="p-4">
        <div class="grid">
          <div class="col-6">
            <p class="font-bold">Thông tin cửa hàng</p>
            <p>Tên cửa hàng: {{ selectedVendor.name }}</p>
            <p>Chủ cửa hàng: {{ selectedVendor.owner }}</p>
            <p>Email: {{ selectedVendor.email }}</p>
            <p>Số điện thoại: {{ selectedVendor.phone }}</p>
            <p>Ngày tham gia: {{ formatDate(selectedVendor.joinDate) }}</p>
          </div>
          <div class="col-6">
            <p class="font-bold">Thông tin tài khoản</p>
            <p>Trạng thái: <Tag :severity="getStatusSeverity(selectedVendor.status)" :value="selectedVendor.status" /></p>
            <p>Số sản phẩm: {{ selectedVendor.productCount }}</p>
            <p>Đánh giá: {{ selectedVendor.rating }}/5 ({{ selectedVendor.reviewCount }} đánh giá)</p>
          </div>
        </div>
        <Divider />
        <p class="font-bold mb-2">Thống kê bán hàng</p>
        <DataTable :value="selectedVendor.salesStats" class="mt-2">
          <Column field="period" header="Thời gian"></Column>
          <Column field="orders" header="Số đơn hàng"></Column>
          <Column field="revenue" header="Doanh thu">
            <template #body="slotProps">
              {{ formatPrice(slotProps.data.revenue) }}
            </template>
          </Column>
        </DataTable>
      </div>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
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

const toast = useToast();
const loading = ref(false);
const searchQuery = ref('');
const selectedRole = ref(null);
const selectedStatus = ref(null);
const filters = ref({});
const userDetailsVisible = ref(false);
const vendorDetailsVisible = ref(false);
const selectedUser = ref<User | null>(null);
const selectedVendor = ref<Vendor | null>(null);
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

const users = ref<User[]>([
  {
    id: 'U001',
    name: 'Nguyễn Văn A',
    email: 'nguyenvana@example.com',
    phone: '0123456789',
    role: 'Người dùng',
    status: 'Hoạt động',
    joinDate: new Date('2024-01-15'),
    activityHistory: [
      { 
        date: new Date('2024-03-15'), 
        action: 'Đăng nhập', 
        details: 'IP: 192.168.1.1',
        type: 'login',
        ip: '192.168.1.1',
        device: 'Chrome/Windows'
      },
      { 
        date: new Date('2024-03-14'), 
        action: 'Đặt hàng', 
        details: 'Đơn hàng #ORD001',
        type: 'order'
      }
    ]
  },
  {
    id: 'U002',
    name: 'Trần Thị B',
    email: 'tranthib@example.com',
    phone: '0987654321',
    role: 'Admin',
    status: 'Hoạt động',
    joinDate: new Date('2024-02-01'),
    activityHistory: [
      { 
        date: new Date('2024-03-15'), 
        action: 'Quản lý đơn hàng', 
        details: 'Cập nhật trạng thái đơn #ORD002',
        type: 'order_management',
        ip: '192.168.1.2',
        device: 'Firefox/Windows'
      },
      { 
        date: new Date('2024-03-14'), 
        action: 'Quản lý người dùng', 
        details: 'Khóa tài khoản U003',
        type: 'user_management',
        ip: '192.168.1.2',
        device: 'Firefox/Windows'
      }
    ]
  }
]);

const vendors = ref<Vendor[]>([
  {
    id: 'V001',
    name: 'Shop Thời Trang A',
    owner: 'Lê Văn C',
    email: 'shopthoitranga@example.com',
    phone: '0123456789',
    status: 'Hoạt động',
    joinDate: new Date('2024-01-01'),
    productCount: 150,
    rating: 4.5,
    reviewCount: 120,
    salesStats: [
      { period: 'Tháng 3/2024', orders: 45, revenue: 15000000 },
      { period: 'Tháng 2/2024', orders: 38, revenue: 12000000 }
    ]
  },
  {
    id: 'V002',
    name: 'Shop Giày Dép B',
    owner: 'Phạm Thị D',
    email: 'shopgiaydepb@example.com',
    phone: '0987654321',
    status: 'Bị khóa',
    joinDate: new Date('2024-02-15'),
    productCount: 80,
    rating: 4.2,
    reviewCount: 45,
    salesStats: [
      { period: 'Tháng 3/2024', orders: 25, revenue: 8000000 },
      { period: 'Tháng 2/2024', orders: 20, revenue: 6000000 }
    ]
  }
]);

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
    case 'Hoạt động':
      return 'success';
    case 'Bị khóa':
      return 'danger';
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

const viewUserDetails = (user: User) => {
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

const viewVendorDetails = (vendor: Vendor) => {
  selectedVendor.value = vendor;
  vendorDetailsVisible.value = true;
};

const updateUserStatus = (user: User, newStatus: string) => {
  // TODO: Implement API call to update user status
  const index = users.value.findIndex(u => u.id === user.id);
  if (index !== -1) {
    users.value[index].status = newStatus;
    toast.add({
      severity: 'success',
      summary: 'Cập nhật thành công',
      detail: `Người dùng ${user.name} đã được cập nhật trạng thái thành ${newStatus}`,
      life: 3000
    });
  }
};

const updateVendorStatus = (vendor: Vendor, newStatus: string) => {
  // TODO: Implement API call to update vendor status
  const index = vendors.value.findIndex(v => v.id === vendor.id);
  if (index !== -1) {
    vendors.value[index].status = newStatus;
    toast.add({
      severity: 'success',
      summary: 'Cập nhật thành công',
      detail: `Cửa hàng ${vendor.name} đã được cập nhật trạng thái thành ${newStatus}`,
      life: 3000
    });
  }
};

const deleteUser = (user: User) => {
  // TODO: Implement API call to delete user
  const index = users.value.findIndex(u => u.id === user.id);
  if (index !== -1) {
    users.value.splice(index, 1);
    toast.add({
      severity: 'success',
      summary: 'Xóa thành công',
      detail: `Người dùng ${user.name} đã được xóa`,
      life: 3000
    });
  }
};

const deleteVendor = (vendor: Vendor) => {
  // TODO: Implement API call to delete vendor
  const index = vendors.value.findIndex(v => v.id === vendor.id);
  if (index !== -1) {
    vendors.value.splice(index, 1);
    toast.add({
      severity: 'success',
      summary: 'Xóa thành công',
      detail: `Cửa hàng ${vendor.name} đã được xóa`,
      life: 3000
    });
  }
};
</script>

<style scoped>
.p-datatable .p-datatable-tbody > tr > td {
  padding: 1rem;
}
</style>
