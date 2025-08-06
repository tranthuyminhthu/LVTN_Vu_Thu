<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl p-8 max-w-xl !w-full text-center">
      <!-- Loading State -->
      <div v-if="isLoading" class="mb-6">
        <div class="w-20 h-20 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4 animate-pulse">
          <svg class="w-12 h-12 text-blue-600 animate-spin" fill="none" viewBox="0 0 24 24">
            <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
            <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
          </svg>
        </div>
        <h1 class="text-3xl font-bold text-gray-800 mb-4">Đang xử lý...</h1>
        <p class="text-gray-600 mb-6 leading-relaxed">
          Vui lòng chờ trong giây lát, chúng tôi đang xác nhận đơn hàng của bạn.
        </p>
      </div>

      <!-- Success State -->
      <div v-else-if="isSuccess" class="mb-6">
        <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-12 h-12 text-green-600" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
        </div>
        <h1 class="text-3xl font-bold text-gray-800 mb-4">Đặt hàng thành công!</h1>
        <p class="text-gray-600 mb-6 leading-relaxed">
          Cảm ơn bạn đã mua sắm! Đơn hàng của bạn đã được xác nhận và sẽ được xử lý sớm nhất có thể.
        </p>
      </div>

      <!-- Error State -->
      <div v-else class="mb-6">
        <div class="w-20 h-20 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-12 h-12 text-red-600" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7 4a1 1 0 11-2 0 1 1 0 012 0zm-1-9a1 1 0 00-1 1v4a1 1 0 102 0V6a1 1 0 00-1-1z" clip-rule="evenodd" />
          </svg>
        </div>
        <h1 class="text-3xl font-bold text-gray-800 mb-4">Đặt hàng thất bại!</h1>
        <p class="text-gray-600 mb-6 leading-relaxed">
          Rất tiếc, có lỗi xảy ra trong quá trình xử lý đơn hàng. Vui lòng thử lại hoặc liên hệ hỗ trợ để được giúp đỡ.
        </p>
      </div>

      <!-- Order Details -->
      <div v-if="isSuccess && !isLoading" class="bg-gray-50 rounded-lg p-4 mb-6">
        <div class="flex items-center justify-center space-x-2 mb-2">
          <span class="text-sm font-medium text-gray-700">Mã đơn hàng: #{{ orderId }}</span>
        </div>
        <p class="text-xs text-gray-500">Bạn sẽ nhận được email xác nhận trong vài phút</p>
      </div>

      <!-- Action Buttons -->
      <div class="space-y-3">
        <!-- Success State Buttons -->
        <div v-if="isSuccess && !isLoading">
          <button 
            @click="goToOrders"
            class="w-full bg-blue-600 text-white py-3 px-6 mb-2 rounded-lg font-medium hover:bg-blue-700 transition-colors duration-200 flex items-center justify-center space-x-2"
          >
            <span>Xem đơn hàng của tôi</span>
          </button>
          
          <button 
            @click="goToHome"
            class="w-full bg-gray-100 text-gray-700 py-3 px-6 rounded-lg font-medium hover:bg-gray-200 transition-colors duration-200"
          >
            Tiếp tục mua sắm
          </button>
        </div>

        <!-- Error State Buttons -->
        <div v-else-if="!isSuccess && !isLoading">
          <button 
            @click="goToHome"
            class="w-full bg-blue-600 text-white py-3 px-6 mb-2 rounded-lg font-medium hover:bg-blue-700 transition-colors duration-200"
          >
            Về trang chủ
          </button>
          
          <button 
            @click="retryPayment"
            class="w-full bg-orange-500 text-white py-3 px-6 rounded-lg font-medium hover:bg-orange-600 transition-colors duration-200"
          >
            Thử lại thanh toán
          </button>
        </div>

        <!-- Loading State - No buttons -->
      </div>

      <!-- Footer -->
      <div class="mt-8 pt-6 border-t border-gray-100">
        <p v-if="isSuccess && !isLoading" class="text-xs text-gray-400 mt-2">Chúc bạn một ngày tuyệt vời!</p>
        <p v-else-if="!isSuccess && !isLoading" class="text-xs text-gray-400 mt-2">Nếu bạn cần hỗ trợ, vui lòng liên hệ chúng tôi</p>
        <p v-else class="text-xs text-gray-400 mt-2">Đang xử lý...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { vnpayReturn } from '@/api/order'
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const router = useRouter()

// Generate a random order ID
const orderId = ref('')
const params = useRoute().query
const isLoading = ref(false)
const isSuccess = ref(false)

onMounted(async () => {
  isLoading.value = true
  orderId.value = params.vnp_TxnRef  as string
  await vnpayReturn(params).then((res) => {
    isSuccess.value = true
  }).catch((err) => {
    isSuccess.value = false
  })
  isLoading.value = false
})

const goToOrders = () => {
  router.push('/account/orders')
}

const goToHome = () => {
  router.push('/')
}

const retryPayment = () => {
  // Redirect back to cart or checkout page to retry payment
  router.push('/cart')
}
</script>

<style scoped>
@keyframes bounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translate3d(0,0,0);
  }
  40%, 43% {
    transform: translate3d(0, -30px, 0);
  }
  70% {
    transform: translate3d(0, -15px, 0);
  }
  90% {
    transform: translate3d(0, -4px, 0);
  }
}

.animate-bounce {
  animation: bounce 1s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.animate-pulse {
  animation: pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}
</style>