import { ref, onMounted, onUnmounted } from 'vue'
import { io, Socket } from 'socket.io-client'

export function useSocket() {
  const socket = ref<Socket | null>(null)
  const isConnected = ref(false)
  const connectionError = ref<string | null>(null)

  const connect = () => {
    try {
      // Kết nối đến socket server trên port 8099
      socket.value = io('http://localhost:8099', {
        transports: ['websocket', 'polling'],
        timeout: 20000,
        reconnection: true,
        reconnectionAttempts: 5,
        reconnectionDelay: 1000
      })

      // Xử lý sự kiện kết nối
      socket.value.on('connect', () => {
        console.log('Socket connected:', socket.value?.id)
        isConnected.value = true
        connectionError.value = null
      })

      // Xử lý sự kiện ngắt kết nối
      socket.value.on('disconnect', (reason) => {
        console.log('Socket disconnected:', reason)
        isConnected.value = false
      })

      // Xử lý lỗi kết nối
      socket.value.on('connect_error', (error) => {
        console.error('Socket connection error:', error)
        connectionError.value = error.message
        isConnected.value = false
      })

      // Xử lý sự kiện reconnect
      socket.value.on('reconnect', (attemptNumber) => {
        console.log('Socket reconnected after', attemptNumber, 'attempts')
        isConnected.value = true
        connectionError.value = null
      })

    } catch (error) {
      console.error('Failed to initialize socket:', error)
      connectionError.value = 'Failed to initialize socket connection'
    }
  }

  const disconnect = () => {
    if (socket.value) {
      socket.value.disconnect()
      socket.value = null
      isConnected.value = false
    }
  }

  const emit = (event: string, data: any) => {
    if (socket.value && isConnected.value) {
      socket.value.emit(event, data)
    } else {
      console.warn('Socket not connected, cannot emit event:', event)
    }
  }

  const on = (event: string, callback: (...args: any[]) => void) => {
    if (socket.value) {
      socket.value.on(event, callback)
    }
  }

  const off = (event: string, callback?: (...args: any[]) => void) => {
    if (socket.value) {
      if (callback) {
        socket.value.off(event, callback)
      } else {
        socket.value.off(event)
      }
    }
  }

  onMounted(() => {
    connect()
  })

  onUnmounted(() => {
    disconnect()
  })

  return {
    socket: socket.value,
    isConnected,
    connectionError,
    connect,
    disconnect,
    emit,
    on,
    off
  }
} 