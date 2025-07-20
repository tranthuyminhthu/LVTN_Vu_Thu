<template>
  <div class="bg-gray-100 rounded-lg">
    <Splitter style="height: calc(100vh - 60px);" class="overflow-y-hidden">
      <SplitterPanel :size="25" :minSize="15">
        <!-- Chat List -->
        <section class="chat-list bg-white border-r flex flex-col">
          <div class="flex items-center justify-between px-4 py-3 border-b border-gray-200">
            <span class="font-semibold text-lg">Chats</span>
          </div>
          <div class="flex-1 overflow-y-auto">
            <button
              v-for="chat in chatList"
              :key="chat.id"
              @click="selectChat(chat)"
              :class="['chat-list-item flex items-center gap-3 w-full px-4 py-3 border-b hover:bg-gray-50 transition', selectedChat?.id === chat.id ? 'bg-gray-100' : 'bg-white']"
            >
              <img :src="getAvatar(chat)" class="!w-12 !h-12 rounded-full object-cover border" />
              <div class="flex-1 text-left">
                <div class="font-semibold text-base">{{ chat.conversationName }}</div>
                <div class="text-xs text-gray-500 truncate max-w-[10rem]">
                  {{ (allMessages[chat.id]?.length > 0) ? allMessages[chat.id][allMessages[chat.id].length-1].text : 'Bắt đầu cuộc trò chuyện' }}
                </div>
              </div>
              <div class="text-xs text-gray-400 ml-2 min-w-[3.5rem] text-right">
                {{ formatDate(chat.updatedAt) }}
              </div>
            </button>
          </div>
        </section>
      </SplitterPanel>
      <SplitterPanel :size="75" :minSize="25">
        <!-- Chat Main -->
        <main class="flex flex-col h-full overflow-y-hidden">
          <div class="flex items-center gap-3 px-6 py-4 border-b bg-white border-gray-200">
            <img v-if="selectedChat" :src="getAvatar(selectedChat)" class="!w-10 !h-10 rounded-full object-cover border" />
            <span class="font-semibold text-lg">{{ selectedChat?.conversationName || '' }}</span>
            <div class="ml-auto flex items-center gap-2">
              <!-- Socket.IO Status -->
              <div class="flex items-center gap-1">
                <div 
                  :class="[
                    'w-2 h-2 rounded-full',
                    isConnected ? 'bg-green-500' : isConnecting ? 'bg-yellow-500' : 'bg-red-500'
                  ]"
                ></div>
                <span class="text-xs text-gray-500">
                  {{ isConnected ? 'Socket.IO đã kết nối' : isConnecting ? 'Đang kết nối Socket.IO...' : 'Socket.IO mất kết nối' }}
                </span>
              </div>
              <Button 
                v-if="!isConnected && !isConnecting"
                icon="pi pi-refresh" 
                @click="connectSocketIO(8099)"
                class="p-button-sm p-button-text"
                title="Kết nối lại"
              />
            </div>
          </div>
          <div class="flex-1 overflow-y-auto px-6 py-4 bg-gray-50 flex flex-col gap-3" ref="messagesContainer">
            <div v-if="!selectedChat" class="flex-1 flex items-center justify-center text-gray-400 text-lg">
              Chọn một cuộc trò chuyện để bắt đầu
            </div>
            <template v-else>
              <div
                v-for="message in currentMessages"
                :key="message.id"
                :class="['flex gap-2', message.sender === 'user' ? 'flex-row-reverse' : 'flex-row']"
              >
                <img :src="getAvatar(selectedChat)" v-if="message.sender !== 'user'" class="!w-8 !h-8 rounded-full object-cover border self-end" />
                <div
                  :class="[
                    'p-3 border rounded-lg max-w-[60%]',
                    message.sender === 'user' ? 'bg-primary text-white ml-auto' : 'bg-white text-black'
                  ]"
                >
                  <div class="text-xs text-gray-400 mb-1" v-if="message.timestamp">{{ formatTime(message.timestamp) }}</div>
                  <div v-html="message.text" class="chat-message-content"></div>
                </div>
              </div>
            </template>
          </div>
          <div class="border-t bg-white px-6 py-3 border-gray-200">
            <div class="flex gap-2 items-end">
              <Editor
                v-model="newMessage"
                placeholder="Nhập tin nhắn..."
                editorStyle="height: 80px; width: 100%;"
                class="flex-1"
                :disabled="!selectedChat"
                @keydown.enter.exact.prevent="sendMessage"
              />
              <Button
                icon="pi pi-send"
                @click="sendMessage"
                :disabled="!selectedChat || !newMessage.trim()"
                class="p-button-primary"
              />
            </div>
          </div>
        </main>
      </SplitterPanel>
    </Splitter>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, watch, onMounted, onUnmounted } from 'vue'
import InputText from 'primevue/inputtext'
import Button from 'primevue/button'
import Splitter from 'primevue/splitter'
import SplitterPanel from 'primevue/splitterpanel'
import Editor from 'primevue/editor'
import { getMyConversations, sendMessageToConversation, getMessagesByConversationId } from '@/api/chat'
import type { Conversation, Message, ApiMessageResponse } from '@/types'
import { useSocketIO } from '@/composables/useSocketIO'

const chatList = ref<Conversation[]>([])
const loading = ref(false)
const selectedChat = ref<Conversation | null>(null)
const newMessage = ref('')
const messagesContainer = ref<HTMLDivElement | null>(null)

// Mock messages per chat
const allMessages = ref<Record<string, Message[]>>({})

// Socket.IO connection
const { 
  socket, 
  isConnected, 
  isConnecting, 
  error: wsError, 
  connect: connectSocketIO, 
  disconnect: disconnectSocketIO, 
  sendMessage: sendSocketIOMessage 
} = useSocketIO()

// Xử lý tin nhắn Socket.IO
const handleSocketIOMessage = (message: Record<string, unknown>) => {
  if (message.type === 'NEW_MESSAGE' && typeof message.conversationId === 'string') {
    // Thêm tin nhắn mới vào conversation tương ứng
    const newMsg: Message = {
      id: Date.now(),
      text: (message.message as string) || '',
      sender: (message.sender as string) === 'user' ? 'user' : 'other',
      timestamp: new Date((message.timestamp as string) || Date.now())
    }
    
    if (!allMessages.value[message.conversationId]) {
      allMessages.value[message.conversationId] = []
    }
    allMessages.value[message.conversationId].push(newMsg)
    
    // Scroll to bottom nếu đang ở conversation này
    if (selectedChat.value?.id === message.conversationId) {
      scrollToBottom()
    }
  }
}

// Lắng nghe tin nhắn từ Socket.IO
const setupSocketIOListeners = () => {
  if (socket.value) {
    socket.value.on('new_message', handleSocketIOMessage)
    socket.value.on('message', handleSocketIOMessage)
  }
}

const currentMessages = computed(() => {
  if (!selectedChat.value) return []
  return allMessages.value[selectedChat.value.id] || []
})

const selectChat = async (chat: Conversation) => {
  selectedChat.value = chat
  // Lấy danh sách tin nhắn thật từ API
  try {
    const messages = await getMessagesByConversationId(chat.id)
    // Map dữ liệu API về Message[]
    allMessages.value[chat.id] = messages.map((m: ApiMessageResponse) => ({
      id: m.id,
      text: m.message,
      sender: m.isMe ? 'user' : 'other',
      timestamp: new Date(m.createdDate)
    }))
  } catch (e) {
    allMessages.value[chat.id] = []
  }
  scrollToBottom()
}

const formatTime = (timestamp: string | Date): string => {
  const date = typeof timestamp === 'string' ? new Date(timestamp) : timestamp
  return new Intl.DateTimeFormat('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: '2-digit',
  }).format(date)
}

const formatDate = (timestamp: string | Date): string => {
  const date = typeof timestamp === 'string' ? new Date(timestamp) : timestamp
  return new Intl.DateTimeFormat('vi-VN', {
    hour: '2-digit',
    minute: '2-digit',
    day: '2-digit',
    month: '2-digit',
    year: '2-digit',
  }).format(date)
}

const sendMessage = async () => {
  if (newMessage.value.trim() && selectedChat.value) {
    try {
      // Gửi message qua API
      const res = await sendMessageToConversation({
        conversationId: selectedChat.value.id,
        message: newMessage.value
      })
      
      // Gửi message qua Socket.IO nếu đã kết nối
      if (isConnected.value) {
        sendSocketIOMessage('NEW_MESSAGE', {
          type: 'NEW_MESSAGE',
          data: {
            conversationId: selectedChat.value.id,
            message: newMessage.value,
            sender: 'user'
          },
          conversationId: selectedChat.value.id,
          message: newMessage.value,
          sender: 'user',
          timestamp: new Date().toISOString()
        })
      }
      
      // Thêm message vào allMessages
      const msg: Message = {
        id: Date.now(),
        text: newMessage.value,
        sender: 'user',
        timestamp: new Date(),
      }
      if (!allMessages.value[selectedChat.value.id]) {
        allMessages.value[selectedChat.value.id] = []
      }
      allMessages.value[selectedChat.value.id].push(msg)
      newMessage.value = ''
      scrollToBottom()
    } catch (error) {
      // Xử lý lỗi gửi tin nhắn (có thể show toast hoặc log)
      console.error('Gửi tin nhắn thất bại:', error)
    }
  }
}

const scrollToBottom = async () => {
  await nextTick()
  if (messagesContainer.value) {
    messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
  }
}

watch(currentMessages, () => {
  scrollToBottom()
})

const fetchConversations = async () => {
  loading.value = true
  try {
    const data = await getMyConversations()
    chatList.value = data
    // Khởi tạo allMessages cho mỗi conversation
    data.forEach((conv: Conversation) => {
      if (!allMessages.value[conv.id]) {
        allMessages.value[conv.id] = []
      }
    })
    // Tự động chọn cuộc trò chuyện đầu tiên nếu có
    if (data.length > 0) {
      await selectChat(data[0])
    }
  } catch (e) {
    // handle error nếu cần
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchConversations()
  // Kết nối Socket.IO
  connectSocketIO(8099)
  
  // Thiết lập listeners sau khi kết nối
  watch(isConnected, (connected) => {
    if (connected) {
      setupSocketIOListeners()
    }
  })
})

onUnmounted(() => {
  // Disconnect Socket.IO khi thoát component
  console.log('Disconnecting Socket.IO...')
  disconnectSocketIO()
})

const getAvatar = (conv: Conversation) => {
  // Ưu tiên conversationAvatar, nếu không có thì lấy avatar của participant khác mình, nếu không có thì avatar mặc định
  if (conv.conversationAvatar) return conv.conversationAvatar
  const other = conv.participants.find(p => p.avatar)
  return other?.avatar || 'https://ui-avatars.com/api/?name=' + encodeURIComponent(conv.conversationName)
}

</script>

<style scoped>
.sidebar {
  min-width: 5rem;
  background: #fff;
  border-right: 1px solid #e5e7eb;
}
.sidebar-btn {
  width: 3rem;
  height: 3rem;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 9999px;
  background: none;
  border: none;
  color: #6b7280;
  transition: background 0.2s, color 0.2s;
  cursor: pointer;
}
.sidebar-btn-active, .sidebar-btn:hover {
  background: #f3f4f6;
  color: #2563eb;
}
.chat-list {
  border-right: 1px solid #e5e7eb;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.chat-list-item {
  outline: none;
  border: none;
  background: none;
  cursor: pointer;
}
main {
  display: flex;
  flex-direction: column;
  background: #f9fafb;
}
.chat-input-bar {
  flex-shrink: 0;
}
/* Handle large images in comments */
:deep(.p-editor-content img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

/* Specific styling for comment content images */
.chat-message-content :deep(img) {
  max-width: 300px;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  margin: 8px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Responsive image sizing */
@media (max-width: 768px) {
  .chat-message-content :deep(img) {
    max-width: 200px;
    max-height: 200px;
  }
}

@media (max-width: 480px) {
  .chat-message-content :deep(img) {
    max-width: 150px;
    max-height: 150px;
  }
}
</style> 