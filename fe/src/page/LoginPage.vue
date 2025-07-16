<script setup lang="ts">
import AppHeader from "../components/AppHeader.vue";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import Checkbox from "primevue/checkbox";
import Button from "primevue/button";
import Divider from "primevue/divider";
import Toast from "primevue/toast";
import Password from "primevue/password";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "../api/auth";
import { useToast } from "primevue/usetoast";
import { useAuth } from "../composables/useAuth";

const username = ref("");
const password = ref("");
const rememberMe = ref(false);
const router = useRouter();
const toast = useToast();
const { setAuth } = useAuth();

const handleLogin = async () => {
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
  }
};
</script>
<template>
  <AppHeader title="Login" />
  <Toast />
  <div class="!mt-header max-w-primary mx-auto pt-12">
    <div class="grid">
      <div class="col !pr-12 pt-8">
        <h1 class="font-bold text-2xl mb-4 text-center">Log in to your account</h1>
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
        <div class="flex items-center justify-between my-4">
          <div class="flex items-center gap-2">
            <Checkbox
              v-model="rememberMe"
              inputId="remember"
              name="remember"
              value="remember"
            />
            <label for="remember" class="text-sm">Remember me</label>
          </div>
          <router-link to="/forgot-password" class="text-primary text-sm underline">
            Forgot password?
          </router-link>
        </div>
        <Button
          label="Log in"
          icon="pi pi-sign-in"
          class="w-full"
          @click="handleLogin"
        />
        <Divider align="center">
          <b>Or log in with</b>
        </Divider>
        <div class="flex items-center justify-center gap-4 mt-4">
          <Button variant="outlined" class="!border-2">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
          <Button variant="outlined" class="!border-2">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Facebook_Logo_%282019%29.png/1200px-Facebook_Logo_%282019%29.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
          <Button variant="outlined" class="!border-2">
            <img
              src="https://www.iwarebatik.org/wp-content/uploads/2019/07/1200px-Apple_logo_black.svg.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
        </div>
        <p class="bg-gray-100 p-4 text-center rounded mt-4">
          Don't have an account?
          <router-link to="/signup" class="text-primary font-bold underline">Sign up</router-link>
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
</template>
