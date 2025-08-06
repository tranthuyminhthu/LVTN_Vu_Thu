<script setup lang="ts">
import AppHeader from "../components/AppHeader.vue";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import Checkbox from "primevue/checkbox";
import Button from "primevue/button";
import Divider from "primevue/divider";
import Toast from "primevue/toast";
import Password from "primevue/password";
import Dialog from "primevue/dialog";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login, forgotPassword } from "../api/auth";
import { useToast } from "primevue/usetoast";
import { useAuth } from "../composables/useAuth";

const username = ref("");
const password = ref("");
const rememberMe = ref(false);
const forgotPasswordEmail = ref("");
const showForgotPasswordDialog = ref(false);
const isLoading = ref(false);
const router = useRouter();
const toast = useToast();
const { setAuth } = useAuth();

const handleLogin = async () => {
  if (isLoading.value) return; // Prevent multiple clicks
  
  isLoading.value = true;
  try {
    const response = await login({
      username: username.value,
      password: password.value,
    });
    
    // Chuyển đổi userProfile thành format UserInfo
    const userInfo = {
      id: response.userProfile.userId,
      username: response.userProfile.username,
      email: response.userProfile.email,
      role: 'user' // Default role, có thể cập nhật sau
    };
    
    // Lưu tokens và user data
    setAuth(
      {
        accessToken: response.accessToken,
        refreshToken: response.refreshToken,
      },
      userInfo
    );
    
    // Hiển thị toast thành công
    toast.add({
      severity: 'success',
      summary: 'Đăng nhập thành công',
      detail: 'Chào mừng bạn quay trở lại!',
      life: 3000
    });
    
    // Chuyển hướng sau khi đăng nhập thành công
    router.push({ name: 'home' });
  } catch (error) {
    // Hiển thị toast lỗi
    toast.add({
      severity: 'error',
      summary: 'Đăng nhập thất bại',
      detail: 'Tên đăng nhập hoặc mật khẩu không đúng. Vui lòng thử lại!',
      life: 5000
    });
  } finally {
    isLoading.value = false;
  }
};

const navigateToForgotPassword = () => {
  showForgotPasswordDialog.value = true;
};

const handleForgotPassword = async () => {
  if (!forgotPasswordEmail.value.trim()) {
    toast.add({
      severity: 'warn',
      summary: 'Cảnh báo',
      detail: 'Vui lòng nhập email của bạn',
      life: 3000
    });
    return;
  }

  // Validate email format
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(forgotPasswordEmail.value)) {
    toast.add({
      severity: 'warn',
      summary: 'Cảnh báo',
      detail: 'Vui lòng nhập email hợp lệ',
      life: 3000
    });
    return;
  }

  try {
    await forgotPassword(forgotPasswordEmail.value);
    
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Email đặt lại mật khẩu đã được gửi. Vui lòng kiểm tra hộp thư của bạn.',
      life: 5000
    });
    
    showForgotPasswordDialog.value = false;
    forgotPasswordEmail.value = "";
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể gửi email đặt lại mật khẩu. Vui lòng thử lại sau.',
      life: 5000
    });
  }
};

const closeForgotPasswordDialog = () => {
  showForgotPasswordDialog.value = false;
  forgotPasswordEmail.value = "";
};
</script>
<template>
  <AppHeader title="Login" />
  <Toast />
  <div class="!mt-header max-w-primary mx-auto pt-12">
    <div class="grid">
      <div class="col !pr-12 pt-8">
        <h1 class="font-bold text-2xl mb-4 text-center">Đăng nhập</h1>
        <FloatLabel variant="in" class="mb-2 w-full">
          <InputText
            id="username"
            v-model="username"
            variant="filled"
            class="w-full"
          />
          <label for="username">Username</label>
        </FloatLabel>
        <FloatLabel variant="in" class="mb-2 w-full">
          <Password
            id="password"
            v-model="password"
            variant="filled"
            class="w-full"
            :feedback="false"
            toggleMask
          />
          <label for="password">Password</label>
        </FloatLabel>
        <!-- <div class="flex items-center justify-between my-4">
          <div class="flex items-center gap-2">
            <Checkbox
              v-model="rememberMe"
              inputId="remember"
              name="remember"
              value="remember"
            />
            <label for="remember" class="text-sm">Nhớ tôi</label>
          </div>
          <router-link to="/forgot-password" class="text-primary text-sm underline">
            Forgot password?
          </router-link>
        </div> -->
        <Button
          label="Đăng nhập"
          icon="pi pi-sign-in"
          class="w-full"
          @click="handleLogin"
          :loading="isLoading"
        />
        <div class="text-end text-sm text-gray-500 mt-2 cursor-pointer" @click="navigateToForgotPassword">Quên mật khẩu?</div>
        <!-- <Divider align="center">
          <b>Hoặc đăng nhập với</b>
        </Divider> -->
        <!-- <div class="flex items-center justify-center gap-4 mt-4">
          <Button variant="outlined" class="!border-2">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
        </div> -->
        <p class="bg-gray-100 p-4 text-center rounded mt-2">
          Chưa có tài khoản?
          <router-link to="/signup" class="text-primary font-bold underline">Đăng ký</router-link>
        </p>
      </div>

      <div class="col">
        <img
          src="https://frontends.udemycdn.com/components/auth/desktop-illustration-x1.webp"
          alt=""
        />
      </div>
    </div>
  </div>

  <!-- Forgot Password Dialog -->
  <Dialog
    v-model:visible="showForgotPasswordDialog"
    modal
    header="Quên mật khẩu"
    :style="{ width: '450px' }"
    :closable="true"
    @hide="closeForgotPasswordDialog"
  >
    <div class="flex flex-col gap-4">
      <p class="text-gray-600 mb-4">
        Nhập email của bạn để nhận link đặt lại mật khẩu
      </p>
      
      <FloatLabel variant="in" class="w-full">
        <InputText
          id="forgot-email"
          v-model="forgotPasswordEmail"
          variant="filled"
          class="w-full"
          type="email"
        />
        <label for="forgot-email">Email</label>
      </FloatLabel>
    </div>
    
    <template #footer>
      <div class="flex justify-end gap-2">
        <Button
          label="Hủy"
          severity="secondary"
          outlined
          @click="closeForgotPasswordDialog"
        />
        <Button
          label="Gửi email"
          @click="handleForgotPassword"
        />
      </div>
    </template>
  </Dialog>
</template>

<style scoped>
:deep(.p-password) {
  width: 100% !important;
}
:deep(.p-password .p-inputtext) {
  width: 100% !important;
}
</style>
