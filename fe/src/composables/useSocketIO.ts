import { ref, onUnmounted } from 'vue'
import { io, Socket } from 'socket.io-client'

export interface SocketIOMessage {
  type: string
  data: Record<string, unknown>
  conversationId?: string
  sender?: string
  message?: string
  timestamp?: string
}

export function useSocketIO() {
  const socket = ref<Socket | null>(null)
  const isConnected = ref(false)
  const isConnecting = ref(false)
  const error = ref<string | null>(null)

  const connect = (port: number = 8099) => {
    if (socket.value?.connected) {
      console.log('Socket.IO đã được kết nối')
      return
    }

    isConnecting.value = true
    error.value = null

    try {
      const socketUrl = `http://localhost:${port}`
      socket.value = io(socketUrl, {
        transports: ['websocket', 'polling'],
        timeout: 20000,
        reconnection: true,
        reconnectionAttempts: 5,
        reconnectionDelay: 1000
      })

      socket.value.on('connect', () => {
        console.log('Socket.IO đã kết nối thành công:', socket.value?.id)
        isConnected.value = true
        isConnecting.value = false
        error.value = null
      })

      socket.value.on('disconnect', (reason) => {
        console.log('Socket.IO đã đóng kết nối:', reason)
        isConnected.value = false
        isConnecting.value = false
      })

      socket.value.on('connect_error', (err) => {
        console.error('Socket.IO connection error:', err)
        error.value = 'Lỗi kết nối Socket.IO'
        isConnected.value = false
        isConnecting.value = false
      })

      socket.value.on('reconnect', (attemptNumber) => {
        console.log('Socket.IO reconnected after', attemptNumber, 'attempts')
        isConnected.value = true
        error.value = null
      })

      // Lắng nghe tin nhắn từ server
      socket.value.on('message', (message: SocketIOMessage) => {
        console.log('Nhận tin nhắn Socket.IO:', message)
      })

      socket.value.on('new_message', (message: SocketIOMessage) => {
        console.log('Tin nhắn mới từ Socket.IO:', message)
      })

    } catch (e) {
      console.error('Lỗi tạo Socket.IO:', e)
      error.value = 'Không thể tạo kết nối Socket.IO'
      isConnecting.value = false
    }
  }

  const disconnect = () => {
    if (socket.value) {
      console.log('Disconnecting Socket.IO...')
      // Remove all event listeners
      socket.value.removeAllListeners()
      // Disconnect socket
      socket.value.disconnect()
      socket.value = null
    }
    isConnected.value = false
    isConnecting.value = false
    error.value = null
  }

  const sendMessage = (event: string, message: SocketIOMessage) => {
    if (socket.value?.connected) {
      socket.value.emit(event, message)
      return true
    } else {
      console.warn('Socket.IO chưa kết nối')
      return false
    }
  }

  const emit = (event: string, data: unknown) => {
    if (socket.value?.connected) {
      socket.value.emit(event, data)
      return true
    } else {
      console.warn('Socket.IO chưa kết nối, không thể emit event:', event)
      return false
    }
  }

  const on = (event: string, callback: (...args: unknown[]) => void) => {
    if (socket.value) {
      socket.value.on(event, callback)
    }
  }

  const off = (event: string, callback?: (...args: unknown[]) => void) => {
    if (socket.value) {
      if (callback) {
        socket.value.off(event, callback)
      } else {
        socket.value.off(event)
      }
    }
  }

  const reconnect = () => {
    disconnect()
    setTimeout(() => {
      connect()
    }, 1000)
  }

  // Tự động đóng kết nối khi component unmount
  onUnmounted(() => {
    console.log('useSocketIO: Cleaning up Socket.IO connection...')
    disconnect()
  })

  return {
    socket,
    isConnected,
    isConnecting,
    error,
    connect,
    disconnect,
    sendMessage,
    emit,
    on,
    off,
    reconnect
  }
} 