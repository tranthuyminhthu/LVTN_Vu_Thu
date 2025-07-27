import { ref, computed } from 'vue'
import type { Router } from 'vue-router'

interface AuthTokens {
  accessToken: string
  refreshToken: string
}

interface User {
  id: string
  username: string
  email: string
  role: string
}

const accessToken = ref<string | null>(null)
const refreshToken = ref<string | null>(null)
const user = ref<User | null>(null)
const isAuthenticated = computed(() => !!accessToken.value)

export function useAuth() {
  // Lấy tokens từ localStorage khi khởi tạo
  const initializeAuth = () => {
    const storedAccessToken = localStorage.getItem('accessToken')
    const storedRefreshToken = localStorage.getItem('refreshToken')
    const storedUser = localStorage.getItem('user')

    if (storedAccessToken) {
      accessToken.value = storedAccessToken
    }
    if (storedRefreshToken) {
      refreshToken.value = storedRefreshToken
    }
    if (storedUser) {
      try {
        user.value = JSON.parse(storedUser)
      } catch (error) {
        // Nếu JSON không hợp lệ, xóa dữ liệu cũ
        console.warn('Invalid user data in localStorage, clearing...')
        localStorage.removeItem('user')
        localStorage.removeItem('accessToken')
        localStorage.removeItem('refreshToken')
        user.value = null
        accessToken.value = null
        refreshToken.value = null
      }
    }

  }

  // Lưu tokens và user info
  const setAuth = (tokens: AuthTokens, userData: User) => {
    // Validate tokens
    if (!tokens.accessToken || !tokens.refreshToken) {
      throw new Error('Invalid tokens provided')
    }

    // Validate user data
    if (!userData || !userData.id || !userData.username) {
      throw new Error('Invalid user data provided')
    }

    accessToken.value = tokens.accessToken
    refreshToken.value = tokens.refreshToken
    user.value = userData

    // Lưu vào localStorage
    localStorage.setItem('accessToken', tokens.accessToken)
    localStorage.setItem('refreshToken', tokens.refreshToken)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  // Xóa tokens và user info
  const clearAuth = () => {
    accessToken.value = null
    refreshToken.value = null
    user.value = null

    // Xóa khỏi localStorage
    try {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
      console.log('LocalStorage cleared successfully');
    } catch (error) {
      console.warn('Error clearing localStorage:', error)
    }
  }

  // Clear localStorage an toàn
  const clearLocalStorage = () => {
    try {
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
      console.log('LocalStorage cleared successfully');
    } catch (error) {
      console.warn('Error clearing localStorage:', error)
    }
  }

  // Logout an toàn - chỉ clear auth, không redirect
  const logout = () => {
    console.log('Logout called - clearing auth state');
    clearAuth()
    
    // Sử dụng window.location để redirect
    console.log('Redirecting to login page');
    window.location.href = '/login'
  }

  // Logout với router (sử dụng trong components)
  const logoutWithRouter = (router: Router) => {
    console.log('Logout with router called - clearing auth state');
    clearAuth()
    
    if (router && typeof router.push === 'function') {
      console.log('Redirecting to login page using router');
      router.push('/login')
    } else {
      console.log('Router not available, using window.location');
      window.location.href = '/login'
    }
  }

  // Logout chỉ clear auth, không redirect (cho interceptor)
  const logoutWithoutRedirect = () => {
    console.log('Logout without redirect - clearing auth state only');
    clearAuth()
  }

  const updateAccessToken = (newAccessToken: string) => {
    console.log('Updating access token:', !!newAccessToken);
    accessToken.value = newAccessToken
    localStorage.setItem('accessToken', newAccessToken)
  }

  return {
    // State
    accessToken: computed(() => accessToken.value),
    refreshToken: computed(() => refreshToken.value),
    user: computed(() => user.value),
    isAuthenticated,

    // Methods
    initializeAuth,
    setAuth,
    clearAuth,
    clearLocalStorage,
    logout,
    logoutWithRouter,
    logoutWithoutRedirect,
    updateAccessToken
  }
} 