<template>
  <div class="p-6">
    <div class="grid grid-cols-1 gap-6">
      <!-- Store Information Section -->
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <h2 class="text-xl font-semibold mb-4">Thông tin cửa hàng</h2>
        <form @submit.prevent="saveStoreInfo" class="space-y-4">
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Tên cửa hàng</label>
              <input
                v-model="storeInfo.name"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Nhập tên cửa hàng"
              />
            </div>
            <div>
              <label class="block text-sm font-medium text-gray-700 mb-1">Địa chỉ</label>
              <input
                v-model="storeInfo.address"
                type="text"
                class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Nhập địa chỉ cửa hàng"
              />
            </div>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Mô tả</label>
            <textarea
              v-model="storeInfo.description"
              rows="4"
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
              placeholder="Nhập mô tả cửa hàng"
            ></textarea>
          </div>

          <div>
            <label class="block text-sm font-medium text-gray-700 mb-1">Logo cửa hàng</label>
            <div class="flex items-center gap-4">
              <img
                v-if="storeInfo.logo"
                :src="storeInfo.logo"
                alt="Store Logo"
                class="w-20 h-20 object-cover rounded-lg"
              />
              <input
                type="file"
                @change="handleLogoUpload"
                accept="image/*"
                class="block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
              />
            </div>
          </div>

          <div class="flex justify-end">
            <button
              type="submit"
              class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              Lưu thông tin
            </button>
          </div>
        </form>
      </div>

      <!-- Customer Reviews Section -->
      <div class="bg-white rounded-lg p-6 shadow-sm">
        <h2 class="text-xl font-semibold mb-4">Đánh giá từ khách hàng</h2>
        <div class="space-y-4">
          <div v-for="review in reviews" :key="review.id" class="border-b pb-4">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-2">
                <img
                  :src="review.customerAvatar"
                  :alt="review.customerName"
                  class="w-10 h-10 rounded-full"
                />
                <div>
                  <h3 class="font-medium">{{ review.customerName }}</h3>
                  <div class="flex items-center">
                    <i
                      v-for="star in 5"
                      :key="star"
                      :class="[
                        'pi',
                        star <= review.rating ? 'pi-star-fill text-yellow-400' : 'pi-star text-gray-300',
                      ]"
                    ></i>
                  </div>
                </div>
              </div>
              <span class="text-sm text-gray-500">{{ review.date }}</span>
            </div>
            <p class="text-gray-600">{{ review.comment }}</p>
            <div class="mt-2">
              <button
                @click="replyToReview(review)"
                class="text-sm text-blue-600 hover:text-blue-800"
              >
                Phản hồi
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

interface StoreInfo {
  name: string;
  address: string;
  description: string;
  logo: string;
}

interface Review {
  id: number;
  customerName: string;
  customerAvatar: string;
  rating: number;
  comment: string;
  date: string;
}

const storeInfo = ref<StoreInfo>({
  name: '',
  address: '',
  description: '',
  logo: '',
});

// Mock data for reviews
const reviews = ref<Review[]>([
  {
    id: 1,
    customerName: 'Nguyễn Văn A',
    customerAvatar: 'https://i.pravatar.cc/150?img=1',
    rating: 5,
    comment: 'Sản phẩm chất lượng, giao hàng nhanh chóng!',
    date: '2024-03-15',
  },
  {
    id: 2,
    customerName: 'Trần Thị B',
    customerAvatar: 'https://i.pravatar.cc/150?img=2',
    rating: 4,
    comment: 'Dịch vụ tốt, nhân viên nhiệt tình.',
    date: '2024-03-14',
  },
]);

const handleLogoUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const reader = new FileReader();
    reader.onload = (e) => {
      storeInfo.value.logo = e.target?.result as string;
    };
    reader.readAsDataURL(target.files[0]);
  }
};

const saveStoreInfo = () => {
  // TODO: Implement API call to save store information
  console.log('Saving store info:', storeInfo.value);
};

const replyToReview = (review: Review) => {
  // TODO: Implement reply functionality
  console.log('Replying to review:', review);
};
</script> 