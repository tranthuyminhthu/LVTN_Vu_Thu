<template>
  <div class="p-6">
    <!-- Active Campaigns Summary -->
    <div class="!grid grid-cols-1 md:grid-cols-3 gap-6 mb-6">
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Chiến dịch đang chạy</p>
            <h3 class="text-2xl font-semibold mt-1">{{ summary.activeCampaigns }}</h3>
          </div>
          <div class="!w-12 !h-12 bg-blue-100 rounded-full flex items-center justify-center">
            <i class="pi pi-chart-line text-blue-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Tổng lượt xem</p>
            <h3 class="text-2xl font-semibold mt-1">{{ formatNumber(summary.totalViews) }}</h3>
          </div>
          <div class="!w-12 !h-12 bg-green-100 rounded-full flex items-center justify-center">
            <i class="pi pi-eye text-green-600 text-xl"></i>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-lg p-6 shadow-sm">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-sm text-gray-500">Tổng chi phí</p>
            <h3 class="text-2xl font-semibold mt-1">{{ formatPrice(summary.totalSpent) }}</h3>
          </div>
          <div class="!w-12 !h-12 bg-purple-100 rounded-full flex items-center justify-center">
            <i class="pi pi-wallet text-purple-600 text-xl"></i>
          </div>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-xl p-8 shadow-lg mb-8 border border-gray-100">
      <div class="flex justify-between items-center mb-8">
        <div class="flex items-center !space-x-3">
          <div class="!w-10 !h-10 bg-gradient-to-r from-blue-500 to-purple-600 rounded-lg flex items-center justify-center">
            <i class="pi pi-megaphone text-white text-lg"></i>
          </div>
          <div>
            <h2 class="text-2xl font-bold text-gray-800">Gói quảng cáo</h2>
            <p class="text-gray-500 text-sm">Quản lý và theo dõi các gói quảng cáo của bạn</p>
          </div>
        </div>
        <button
          @click="showPackageModal = true"
          class="px-6 py-3 bg-gradient-to-r from-blue-600 to-blue-700 text-white rounded-xl hover:from-blue-700 hover:to-blue-800 focus:outline-none focus:ring-4 focus:ring-blue-200 transition-all duration-200 flex items-center space-x-2 shadow-lg hover:shadow-xl transform hover:-translate-y-0.5"
        >
          <i class="pi pi-plus text-sm mr-2"></i>
          <span class="font-medium">Đăng ký gói mới</span>
        </button>
      </div>

      <div class="!grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div
          v-for="adPackage in adPackages"
          :key="adPackage.id"
          class="group relative bg-gradient-to-br from-white to-gray-50 border border-gray-200 rounded-2xl p-6 hover:shadow-2xl hover:border-blue-200 transition-all duration-300 transform hover:-translate-y-2"
        >
          <!-- Status Badge -->
          <div class="absolute top-4 right-4">
            <span
              :class="[
                'px-3 py-2 text-xs font-semibold rounded-full shadow-sm',
                getStatusClass(adPackage.status)
              ]"
            >
              <i :class="getStatusIcon(adPackage.status)" class="mr-1"></i>
              {{ getStatusLabel(adPackage.status) }}
            </span>
          </div>

          <!-- Package Icon -->
          <div class="flex items-center space-x-3 mb-4">
            <div>
              <h3 class="text-xl font-bold text-gray-800 group-hover:text-blue-600 transition-colors">
                {{ adPackage.name }}
              </h3>
              <p class="text-sm text-gray-500">{{ adPackage.description }}</p>
            </div>
          </div>

          <!-- Package Details -->
          <div class="!space-y-4 mb-6">
            <!-- Duration -->
            <div class="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
              <div class="flex items-center space-x-2"> 
                <i class="pi pi-calendar text-blue-600 mr-2"></i>
                <span class="text-gray-600 font-medium">Thời hạn</span>
              </div>
              <span class="font-bold text-blue-600">{{ adPackage.duration }} ngày</span>
            </div>

            <!-- Price -->
            <div class="flex items-center justify-between p-3 bg-green-50 rounded-lg">
              <div class="flex items-center space-x-2">
                <i class="pi pi-dollar text-green-600 mr-2"></i>
                <span class="text-gray-600 font-medium">Giá</span>
              </div>
              <span class="font-bold text-green-600">{{ formatPrice(adPackage.price) }}</span>
            </div>

            <!-- Views -->
            <div class="flex items-center justify-between p-3 bg-purple-50 rounded-lg">
              <div class="flex items-center space-x-2">
                <i class="pi pi-eye text-purple-600 mr-2  "></i>
                <span class="text-gray-600 font-medium">Lượt xem tối thiểu</span>
              </div>
              <span class="font-bold text-purple-600">{{ formatNumber(adPackage.minViews) }}</span>
            </div>
          </div>

          <!-- Progress Bar (if active) -->
          <div v-if="adPackage.status === 'active'" class="mb-4">
            <div class="flex justify-between text-sm text-gray-600 mb-2">
              <span>Tiến độ</span>
              <span>{{ Math.floor(Math.random() * 100) }}%</span>
            </div>
            <div class="w-full bg-gray-200 rounded-full h-2">
              <div 
                class="bg-gradient-to-r from-blue-500 to-purple-600 h-2 rounded-full transition-all duration-500"
                :style="{ width: Math.floor(Math.random() * 100) + '%' }"
              ></div>
            </div>
          </div>

          <!-- Action Buttons -->
          <div class="flex space-x-3">
            <button
              v-if="adPackage.status === 'active'"
              @click="viewCampaignDetails(adPackage)"
              class="flex-1 bg-gradient-to-r from-blue-500 to-blue-600 text-white py-2 px-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 flex items-center justify-center space-x-2 font-medium shadow-md hover:shadow-lg"
            >
              <i class="pi pi-chart-line text-sm"></i>
              <span>Xem chi tiết</span>
            </button>
            <button
              v-if="adPackage.status === 'expired'"
              @click="renewPackage(adPackage)"
              class="flex-1 bg-gradient-to-r from-green-500 to-green-600 text-white py-2 px-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 flex items-center justify-center space-x-2 font-medium shadow-md hover:shadow-lg"
            >
              <i class="pi pi-refresh text-sm"></i>
              <span>Gia hạn</span>
            </button>
          </div>

          <!-- Hover Effect Overlay -->
          <div class="absolute inset-0 bg-gradient-to-r from-blue-500/5 to-purple-500/5 rounded-2xl opacity-0 group-hover:opacity-100 transition-opacity duration-300 pointer-events-none"></div>
        </div>
      </div>

      <!-- Empty State -->
      <div v-if="adPackages.length === 0" class="text-center py-12">
        <div class="w-24 h-24 bg-gray-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <i class="pi pi-megaphone text-gray-400 text-3xl"></i>
        </div>
        <h3 class="text-xl font-semibold text-gray-600 mb-2">Chưa có gói quảng cáo nào</h3>
        <p class="text-gray-500 mb-6">Bắt đầu với gói quảng cáo đầu tiên để tăng doanh số bán hàng</p>
        <button
          @click="showPackageModal = true"
          class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <i class="pi pi-plus mr-2"></i>Đăng ký gói đầu tiên
        </button>
      </div>
    </div>

    <!-- Campaign Performance -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b">
        <h2 class="text-lg font-semibold">Hiệu quả chiến dịch</h2>
      </div>
      <div class="p-4">
        <div class="mb-4">
          <select
            v-model="selectedCampaign"
            class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="">Chọn chiến dịch</option>
            <option
              v-for="campaign in activeCampaigns"
              :key="campaign.id"
              :value="campaign.id"
            >
              {{ campaign.name }}
            </option>
          </select>
        </div>

        <div v-if="selectedCampaign" class="space-y-6">
          <!-- Performance Metrics -->
          <div class="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Lượt xem</p>
              <h4 class="text-xl font-semibold mt-1">
                {{ formatNumber(campaignStats.views) }}
              </h4>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Lượt click</p>
              <h4 class="text-xl font-semibold mt-1">
                {{ formatNumber(campaignStats.clicks) }}
              </h4>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Tỷ lệ click</p>
              <h4 class="text-xl font-semibold mt-1">
                {{ campaignStats.ctr }}%
              </h4>
            </div>
            <div class="bg-gray-50 p-4 rounded-lg">
              <p class="text-sm text-gray-500">Chi phí trung bình</p>
              <h4 class="text-xl font-semibold mt-1">
                {{ formatPrice(campaignStats.avgCost) }}
              </h4>
            </div>
          </div>

          <!-- Performance Chart -->
          <div class="h-80">
            <Chart type="line" :data="campaignChartData" :options="campaignChartOptions" style="height: 100%" />
          </div>
        </div>
      </div>
    </div>

    <!-- Package Registration Modal -->
    <Dialog v-model:visible="showPackageModal" :modal="true" :closable="true" :style="{ width: '600px' }" header="Đăng ký gói quảng cáo mới">
      <form @submit.prevent="registerPackage" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700">Chọn gói quảng cáo</label>
          <select
            v-model="newPackage.type"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          >
            <option value="basic">Gói Cơ bản</option>
            <option value="premium">Gói Premium</option>
            <option value="vip">Gói VIP</option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Sản phẩm quảng cáo</label>
          <select
            v-model="newPackage.productId"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          >
            <option value="">Chọn sản phẩm</option>
            <option
              v-for="product in products"
              :key="product.id"
              :value="product.id"
            >
              {{ product.name }}
            </option>
          </select>
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Thời hạn (ngày)</label>
          <input
            v-model="newPackage.duration"
            type="number"
            min="1"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <div>
          <label class="block text-sm font-medium text-gray-700">Ngân sách</label>
          <input
            v-model="newPackage.budget"
            type="number"
            min="0"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>

        <div class="flex justify-end gap-3 mt-4">
          <button
            type="button"
            @click="closePackageModal"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            Hủy
          </button>
          <button
            type="submit"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            Đăng ký
          </button>
        </div>
      </form>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import Dialog from 'primevue/dialog';
import Chart from 'primevue/chart';

interface Summary {
  activeCampaigns: number;
  campaignGrowth: number;
  totalViews: number;
  viewGrowth: number;
  totalSpent: number;
  remainingBudget: number;
}

interface AdPackage {
  id: number;
  name: string;
  description: string;
  status: 'active' | 'expired' | 'pending';
  duration: number;
  price: number;
  minViews: number;
}

interface Campaign {
  id: number;
  name: string;
  packageId: number;
  productId: number;
  startDate: string;
  endDate: string;
  status: string;
}

interface CampaignStats {
  views: number;
  clicks: number;
  ctr: number;
  avgCost: number;
}

interface Product {
  id: number;
  name: string;
  price: number;
  image: string;
}

const summary = ref<Summary>({
  activeCampaigns: 3,
  campaignGrowth: 50,
  totalViews: 15000,
  viewGrowth: 25,
  totalSpent: 5000000,
  remainingBudget: 2000000,
});

const adPackages = ref<AdPackage[]>([
  {
    id: 1,
    name: 'Gói Cơ bản',
    description: 'Hiển thị sản phẩm ở vị trí cơ bản',
    status: 'active',
    duration: 30,
    price: 1000000,
    minViews: 1000,
  },
  {
    id: 2,
    name: 'Gói Premium',
    description: 'Hiển thị sản phẩm ở vị trí nổi bật',
    status: 'expired',
    duration: 30,
    price: 2000000,
    minViews: 3000,
  },
  {
    id: 3,
    name: 'Gói VIP',
    description: 'Hiển thị sản phẩm ở vị trí đặc biệt',
    status: 'pending',
    duration: 30,
    price: 5000000,
    minViews: 10000,
  },
]);

const products = ref<Product[]>([
  {
    id: 1,
    name: 'Sản phẩm 1',
    price: 100000,
    image: 'https://via.placeholder.com/150',
  },
  {
    id: 2,
    name: 'Sản phẩm 2',
    price: 200000,
    image: 'https://via.placeholder.com/150',
  },
]);

const activeCampaigns = ref<Campaign[]>([
  {
    id: 1,
    name: 'Chiến dịch 1',
    packageId: 1,
    productId: 1,
    startDate: '2024-03-01',
    endDate: '2024-03-31',
    status: 'active',
  },
]);

const selectedCampaign = ref('');
const campaignStats = ref<CampaignStats>({
  views: 5000,
  clicks: 250,
  ctr: 5,
  avgCost: 2000,
});

const showPackageModal = ref(false);
const newPackage = ref({
  type: '',
  productId: '',
  duration: 30,
  budget: 0,
});

const campaignChartData = ref({
  labels: ['Ngày 1', 'Ngày 2', 'Ngày 3', 'Ngày 4', 'Ngày 5', 'Ngày 6', 'Ngày 7'],
  datasets: [
    {
      label: 'Lượt xem',
      borderColor: '#3b82f6',
      backgroundColor: 'rgba(59, 130, 246, 0.2)',
      data: [500, 700, 800, 600, 900, 1000, 1200],
      fill: true,
      tension: 0.4
    },
    {
      label: 'Lượt click',
      borderColor: '#10b981',
      backgroundColor: 'rgba(16, 185, 129, 0.2)',
      data: [50, 70, 60, 80, 90, 100, 110],
      fill: true,
      tension: 0.4
    }
  ]
});

const campaignChartOptions = ref({
  responsive: true,
  plugins: {
    legend: {
      display: true,
      position: 'top'
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        callback: function(value: number) {
          return value.toLocaleString('vi-VN');
        }
      }
    }
  }
});

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const formatNumber = (num: number) => {
  return new Intl.NumberFormat('vi-VN').format(num);
};

const getStatusLabel = (status: string) => {
  const labels = {
    active: 'Đang hoạt động',
    expired: 'Đã hết hạn',
    pending: 'Đang chờ duyệt',
  };
  return labels[status as keyof typeof labels] || status;
};

const getStatusClass = (status: string) => {
  const classes = {
    active: 'bg-green-100 text-green-800',
    expired: 'bg-red-100 text-red-800',
    pending: 'bg-yellow-100 text-yellow-800',
  };
  return classes[status as keyof typeof classes] || 'bg-gray-100 text-gray-800';
};

const getStatusIcon = (status: string) => {
  const icons = {
    active: 'pi pi-check-circle',
    expired: 'pi pi-times-circle',
    pending: 'pi pi-clock',
  };
  return icons[status as keyof typeof icons] || 'pi pi-info-circle';
};

const viewCampaignDetails = (adPackage: AdPackage) => {
  // TODO: Implement view campaign details
  console.log('View campaign details:', adPackage);
};

const renewPackage = (adPackage: AdPackage) => {
  // TODO: Implement package renewal
  console.log('Renew package:', adPackage);
};

const closePackageModal = () => {
  showPackageModal.value = false;
  newPackage.value = {
    type: '',
    productId: '',
    duration: 30,
    budget: 0,
  };
};

const registerPackage = () => {
  // TODO: Implement package registration
  console.log('Register new package:', newPackage.value);
  closePackageModal();
};
</script> 