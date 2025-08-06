<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-50 ">
    <div class="max-w-xl w-full !space-y-8 p-8">
      <div class="text-center">
        <h2 class="text-3xl font-bold text-gray-900">Đặt lại mật khẩu</h2>
        <p class="mt-2 text-sm text-gray-600">
          Nhập mật khẩu mới của bạn
        </p>
      </div>

      <div class="bg-white p-6 rounded-lg shadow-md">
        <form @submit.prevent="handleResetPassword" class="space-y-4">
          <div>
            <label for="newPassword" class="block text-sm font-medium text-gray-700 mb-1">
              Mật khẩu mới
            </label>
            <Password
              id="newPassword"
              v-model="newPassword"
              :feedback="true"
              toggleMask
              class="w-full"
              :class="{ 'border-red-500': errors.newPassword }"
              @blur="validatePassword"
            />
            <p v-if="errors.newPassword" class="text-red-500 text-xs mt-1">
              {{ errors.newPassword }}
            </p>
          </div>

          <div>
            <label for="confirmPassword" class="block text-sm font-medium text-gray-700 mb-1">
              Xác nhận mật khẩu
            </label>
            <Password
              id="confirmPassword"
              v-model="confirmPassword"
              :feedback="false"
              toggleMask
              class="w-full"
              :class="{ 'border-red-500': errors.confirmPassword }"
              @blur="validateConfirmPassword"
            />
            <p v-if="errors.confirmPassword" class="text-red-500 text-xs mt-1">
              {{ errors.confirmPassword }}
            </p>
          </div>

          <Button
            type="submit"
            label="Đặt lại mật khẩu"
            class="w-full mt-2"
            :loading="loading"
            :disabled="loading"
          />
        </form>
      </div>
    </div>
  </div>

  <Toast />
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import Password from 'primevue/password';
import Button from 'primevue/button';
import Toast from 'primevue/toast';
import { resetPassword } from '@/api/auth';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const newPassword = ref('');
const confirmPassword = ref('');
const loading = ref(false);
const token = ref('');

const errors = ref<{
  newPassword?: string;
  confirmPassword?: string;
}>({});

onMounted(() => {
  // Lấy token từ URL query parameter
  const urlToken = route.query.token as string;
  if (!urlToken) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Token không hợp lệ hoặc đã hết hạn',
      life: 5000
    });
    router.push('/login');
    return;
  }
  token.value = urlToken;
});

const validatePassword = () => {
  errors.value.newPassword = '';
  
  if (!newPassword.value) {
    errors.value.newPassword = 'Vui lòng nhập mật khẩu mới';
    return false;
  }
  
  if (newPassword.value.length < 6) {
    errors.value.newPassword = 'Mật khẩu phải có ít nhất 6 ký tự';
    return false;
  }
  
  return true;
};

const validateConfirmPassword = () => {
  errors.value.confirmPassword = '';
  
  if (!confirmPassword.value) {
    errors.value.confirmPassword = 'Vui lòng xác nhận mật khẩu';
    return false;
  }
  
  if (newPassword.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Mật khẩu xác nhận không khớp';
    return false;
  }
  
  return true;
};

const handleResetPassword = async () => {
  // Validate all fields
  const isPasswordValid = validatePassword();
  const isConfirmValid = validateConfirmPassword();
  
  if (!isPasswordValid || !isConfirmValid) {
    return;
  }

  loading.value = true;
  
  try {
    await resetPassword({
      token: token.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value
    });
    
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Mật khẩu đã được đặt lại thành công! Vui lòng đăng nhập với mật khẩu mới.',
      life: 5000
    });
    
    // Redirect to login page after successful reset
    setTimeout(() => {
      router.push('/login');
    }, 2000);
    
  } catch (error: any) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: error.response?.data?.message || 'Không thể đặt lại mật khẩu. Vui lòng thử lại.',
      life: 5000
    });
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
:deep(.p-password) {
  width: 100% !important;
}
:deep(.p-password .p-inputtext) {
  width: 100% !important;
}
</style> 