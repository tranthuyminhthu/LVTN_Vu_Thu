import { ref, onUnmounted } from 'vue'
import { io, Socket } from 'socket.io-client'
import type { SocketChatMessageResponse } from '@/types'

export interface SocketIOMessage {
  conversationId?: string
  message?: string
}

export function useSocketIO() {
  const socket = ref<Socket | null>(null)
  const isConnected = ref(false)
  const isConnecting = ref(false)
  const error = ref<string | null>(null)

  const connect = (port: number = 8099) => {
    if (socket.value?.connected) {
      return
    }

    isConnecting.value = true
    error.value = null

    try {
      const socketUrl = `http://localhost:${port}?token=${localStorage.getItem('accessToken')}`
      socket.value = io(socketUrl, {
        transports: ['websocket', 'polling'],
        timeout: 20000,
        reconnection: true,
        reconnectionAttempts: 5,
        reconnectionDelay: 1000
      })

      socket.value.on('connect', () => {
        isConnected.value = true
        isConnecting.value = false
        error.value = null
      })

      socket.value.on('disconnect', (reason) => {
        isConnected.value = false
        isConnecting.value = false
      })

      socket.value.on('connect_error', (err) => {
        error.value = 'Lỗi kết nối Socket.IO'
        isConnected.value = false
        isConnecting.value = false
      })

      socket.value.on('reconnect', (attemptNumber) => {
        isConnected.value = true
        error.value = null
      })

    } catch (e) {
      error.value = 'Không thể tạo kết nối Socket.IO'
      isConnecting.value = false
    }
  }

  const disconnect = () => {
    if (socket.value) {
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
      return false
    }
  }

  const emit = (event: string, data: unknown) => {
    if (socket.value?.connected) {
      socket.value.emit(event, data)
      return true
    } else {
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