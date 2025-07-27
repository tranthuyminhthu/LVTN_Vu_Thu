<template>
  <div class="min-h-screen bg-gradient-to-br from-green-50 to-blue-50 flex items-center justify-center p-4">
    <div class="bg-white rounded-2xl shadow-xl p-8 max-w-xl !w-full text-center">
      <!-- Success Icon -->
      <div class="mb-6">
        <div class="w-20 h-20 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-12 h-12 text-green-600" fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
          </svg>
        </div>
    
      </div>

      <!-- Success Message -->
      <h1 class="text-3xl font-bold text-gray-800 mb-4">Đặt hàng thành công!</h1>
      <p class="text-gray-600 mb-6 leading-relaxed">
        Cảm ơn bạn đã mua sắm! Đơn hàng của bạn đã được xác nhận và sẽ được xử lý sớm nhất có thể.
      </p>

      <!-- Order Details -->
      <div class="bg-gray-50 rounded-lg p-4 mb-6">
        <div class="flex items-center justify-center space-x-2 mb-2">
          <span class="text-sm font-medium text-gray-700">Mã đơn hàng: #{{ orderId }}</span>
        </div>
        <p class="text-xs text-gray-500">Bạn sẽ nhận được email xác nhận trong vài phút</p>
      </div>

      <!-- Action Buttons -->
      <div class="space-y-3">
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

      <!-- Fun Footer -->
      <div class="mt-8 pt-6 border-t border-gray-100">
        <p class="text-xs text-gray-400 mt-2">Chúc bạn một ngày tuyệt vời!</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// Generate a random order ID
const orderId = ref('')

onMounted(() => {
  // Generate a random 8-character order ID
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  orderId.value = Array.from({ length: 8 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
})

const goToOrders = () => {
  router.push('/account/orders')
}

const goToHome = () => {
  router.push('/')
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