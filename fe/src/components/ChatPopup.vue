<template>
  <div>
    <!-- Chat Button -->
    <Transition name="fade">
      <button
        v-if="!isOpen"
        @click="isOpen = true"
        class="fixed z-50 bottom-4 right-4 w-4rem h-4rem border-circle bg-primary text-white border-none cursor-pointer flex align-items-center justify-content-center text-2xl shadow-3 hover:bg-primary-600 transition-colors transition-duration-300"
      >
        <i class="pi pi-comments"></i>
      </button>
    </Transition>

    <!-- Chat Window -->
    <Transition name="slide-fade">
      <div v-if="isOpen" class="fixed bottom-4 right-4 w-30rem h-30rem bg-white border-round shadow-3 flex z-5">
        <!-- User Selection Sidebar -->
        <div class="w-8rem border-right-1 border-gray-200 flex flex-column">
          <div class="p-3 bg-primary text-white border-round-top">
            <h3 class="m-0 text-lg">Support</h3>
          </div>
          <div class="flex-1 overflow-y-auto scroll-hidden">
            <button
              v-for="user in supportUsers"
              :key="user.id"
              @click="selectUser(user)"
              :class="[
                'w-full p-3 border-bottom-1 border-gray-200 flex flex-column align-items-center gap-2',
                selectedUser?.id === user.id 
                  ? 'bg-primary-50' 
                  : 'bg-white hover:bg-gray-100'
              ]"
            >
              <div class="w-3rem h-3rem border-circle flex align-items-center justify-content-center bg-gray-200">
                <i class="pi pi-user text-gray-700 text-xl"></i>
              </div>
              <span class="text-sm text-center" :class="selectedUser?.id === user.id ? 'text-primary font-semibold' : 'text-gray-700'">
                {{ user.name }}
              </span>
            </button>
          </div>
        </div>

        <!-- Chat Content -->
        <div class="flex-1 flex flex-column">
          <!-- Chat Header -->
          <div class="p-3 bg-primary text-white border-round-top flex justify-content-between align-items-center">
            <div class="flex align-items-center gap-2">
              <div class="w-2rem h-2rem border-circle bg-white flex align-items-center justify-content-center">
                <i class="pi pi-user text-primary"></i>
              </div>
              <h3 class="m-0 text-lg">{{ selectedUser?.name || 'Select a user' }}</h3>
            </div>
            <div class="flex gap-2">
              <button @click="isOpen = false" class="bg-transparent border-none text-white cursor-pointer text-lg hover:bg-primary-600 p-2 border-round">
                <i class="pi pi-minus"></i>
              </button>
              <button @click="isOpen = false" class="bg-transparent border-none text-white cursor-pointer text-lg hover:bg-primary-600 p-2 border-round">
                <i class="pi pi-times"></i>
              </button>
            </div>
          </div>

          <!-- Chat Messages -->
          <div class="flex-1 overflow-y-auto p-3 flex flex-column gap-3 scroll-hidden" ref="messagesContainer">
            <div
              v-for="message in currentMessages"
              :key="message.id"
              :class="[
                'flex gap-2',
                message.sender === 'user' ? 'flex-row-reverse' : 'flex-row'
              ]"
            >
              <!-- Avatar -->
              <div 
                :class="[
                  'w-2rem h-2rem border-circle flex align-items-center justify-content-center',
                  message.sender === 'user' ? 'bg-primary' : 'bg-gray-200'
                ]"
              >
                <i :class="[
                  'pi',
                  message.sender === 'user' ? 'pi-user text-white' : 'pi-user text-gray-700'
                ]"></i>
              </div>
              
              <!-- Message -->
              <div
                :class="[
                  'p-3 border-round max-w-70',
                  message.sender === 'user' 
                    ? 'bg-primary text-white' 
                    : 'bg-gray-200 text-black'
                ]"
              >
                {{ message.text }}
              </div>
            </div>
          </div>

          <!-- Chat Input -->
          <div class="p-3 border-top-1 border-gray-200 flex gap-2">
            <input
              v-model="newMessage"
              @keyup.enter="sendMessage"
              placeholder="Type a message..."
              type="text"
              class="flex-1 p-2 border-1 border-gray-300 border-round-xl outline-none"
            />
            <button 
              @click="sendMessage" 
              class="bg-primary text-white border-none border-circle w-3rem h-3rem cursor-pointer flex align-items-center justify-content-center hover:bg-primary-600 transition-colors transition-duration-300"
            >
              <i class="pi pi-send"></i>
            </button>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, watch, computed } from 'vue'

interface Message {
  id: number;
  text: string;
  sender: 'user' | 'other';
  userId: number;
}

interface SupportUser {
  id: number;
  name: string;
}

const isOpen = ref(false)
const messages = ref<Message[]>([])
const newMessage = ref('')
const messagesContainer = ref<HTMLDivElement | null>(null)
const selectedUser = ref<SupportUser | null>(null)

// Fake support users
const supportUsers = ref<SupportUser[]>([
  { id: 1, name: 'Support 1' },
  { id: 2, name: 'Support 2' },
  { id: 3, name: 'Support 3' },
])

// Get messages for current user
const currentMessages = computed(() => {
  if (!selectedUser.value) return []
  return messages.value.filter(msg => msg.userId === selectedUser.value?.id)
})

// Select user to chat with
const selectUser = (user: SupportUser) => {
  selectedUser.value = user
  scrollToBottom()
}

// Fake responses based on keywords
const getFakeResponse = (message: string): string => {
  const lowerMessage = message.toLowerCase()
  
  if (lowerMessage.includes('hello') || lowerMessage.includes('hi')) {
    return 'Xin chào! Tôi có thể giúp gì cho bạn?'
  }
  
  if (lowerMessage.includes('help')) {
    return 'Tôi có thể giúp bạn với các vấn đề về sản phẩm, đơn hàng hoặc dịch vụ của chúng tôi.'
  }
  
  if (lowerMessage.includes('price') || lowerMessage.includes('cost')) {
    return 'Bạn có thể xem giá sản phẩm trong trang chi tiết sản phẩm. Nếu cần thêm thông tin, hãy cho tôi biết.'
  }
  
  if (lowerMessage.includes('thank')) {
    return 'Cảm ơn bạn đã liên hệ với chúng tôi!'
  }
  
  if (lowerMessage.includes('bye') || lowerMessage.includes('goodbye')) {
    return 'Tạm biệt! Chúc bạn một ngày tốt lành.'
  }
  
  // Default response
  return 'Xin lỗi, tôi không hiểu câu hỏi của bạn. Bạn có thể diễn đạt lại không?'
}

const sendMessage = () => {
  if (newMessage.value.trim() && selectedUser.value) {
    // Add user message
    messages.value.push({
      id: Date.now(),
      text: newMessage.value,
      sender: 'user',
      userId: selectedUser.value.id
    })
    
    // Get and add bot response after a short delay
    setTimeout(() => {
      messages.value.push({
        id: Date.now(),
        text: getFakeResponse(newMessage.value),
        sender: 'other',
        userId: selectedUser.value!.id
      })
      scrollToBottom()
    }, 500)
    
    newMessage.value = ''
    scrollToBottom()
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(messages, () => {
  scrollToBottom()
})

// Select first user by default when opening chat
watch(isOpen, (newValue) => {
  if (newValue && !selectedUser.value) {
    selectedUser.value = supportUsers.value[0]
  }
})
</script>

<style scoped>
/* Custom styles that can't be handled by PrimeFlex */
:deep(.border-gray-200) {
  border-color: #e5e7eb;
}

:deep(.border-gray-300) {
  border-color: #d1d5db;
}

:deep(.bg-gray-200) {
  background-color: #f3f4f6;
}

:deep(.text-gray-700) {
  color: #374151;
}

/* Hide scrollbar but keep functionality */
.scroll-hidden {
  scrollbar-width: none; /* Firefox */
  -ms-overflow-style: none; /* IE and Edge */
}

.scroll-hidden::-webkit-scrollbar {
  display: none; /* Chrome, Safari and Opera */
}

/* Fade animation for chat button */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* Slide and fade animation for chat window */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

/* Add new styles */
:deep(.bg-primary-50) {
  background-color: #f0f7ff;
}

:deep(.text-primary) {
  color: var(--primary-color);
}

:deep(.font-semibold) {
  font-weight: 600;
}
</style>
