<template>
  <div v-if="isAuthenticated" class="flex items-center gap-4 cursor-pointer">
    <div class="flex items-center gap-2 "  @click="handleUserMenu">
      <div class="!w-8 !h-8 bg-primary rounded-full flex items-center justify-center text-white font-bold">
        {{ userInitials }}
      </div>
      <div class="hidden md:block">
        <p class="text-sm font-medium">{{ user?.username }}</p>
        <p class="text-xs text-gray-500">{{ user?.email }}</p>
      </div>
    </div>
    
    <Button
      icon="pi pi-sign-out"
      severity="secondary"
      text
      @click="handleLogout"
    />
  </div>
  
  <div v-else class="flex items-center gap-2">
    <Button
      label="Đăng nhập"
      severity="secondary"
      text
      @click="$router.push('/login')"
    />
    <Button
      label="Đăng ký"
      @click="$router.push('/signup')"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Button from 'primevue/button'
import { useAuth } from '../composables/useAuth'
import { useToast } from 'primevue/usetoast'
import { useRouter } from 'vue-router'

const { user, isAuthenticated, logout } = useAuth()
const toast = useToast()
const router = useRouter()

const userInitials = computed(() => {
  if (!user.value?.username) return 'U'
  return user.value.username.charAt(0).toUpperCase()
})

const handleLogout = async () => {
  try {
    logout()
    toast.add({
      severity: 'success',
      summary: 'Đăng xuất thành công',
      detail: 'Bạn đã đăng xuất khỏi hệ thống',
      life: 3000
    })
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi đăng xuất',
      detail: 'Có lỗi xảy ra khi đăng xuất',
      life: 3000
    })
  }
}

const handleUserMenu = () => {
  router.push({ name: 'info' })
}
</script> 