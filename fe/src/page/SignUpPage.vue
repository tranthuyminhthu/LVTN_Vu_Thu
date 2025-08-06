<template>
  <AppHeader title="Product List" />
  <Toast />
  <div class="!mt-header max-w-primary mx-auto pt-12">
    <div class="grid">
      <div class="col !pr-12 pt-8">
        <h1 class="font-bold text-2xl mb-4 text-center">Đăng ký</h1>
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
          <InputText
            id="email"
            v-model="email"
            variant="filled"
            class="w-full"
            type="email"
          />
          <label for="email">Email</label>
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
        <!-- Thay select role thành checkbox -->
        <div class="mb-2 w-full flex items-center gap-2">
          <Checkbox v-model="isVendor" inputId="isVendor" :binary="true" />
          <label for="isVendor">Đăng ký làm người bán</label>
        </div>
        <Button
          label="Đăng ký"
          icon="pi pi-envelope"
          class="w-full"
          @click="handleSignUp"
        />
        <Divider align="center">
          <b>Hoặc đăng nhập với</b>
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
        <p class="text-center text-xs my-4">
          Bằng cách đăng ký, bạn đồng ý với
          <a href="/" class="text-primary underline">Điều khoản sử dụng</a> và
          <a href="/" class="text-primary underline">Chính sách bảo mật</a>
        </p>
        <p class="bg-gray-100 p-4 text-center rounded">
          Đã có tài khoản?
          <router-link to="/login" class="text-primary font-bold underline">Đăng nhập</router-link>
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
<script setup lang="ts">
import AppHeader from "../components/AppHeader.vue";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import Checkbox from "primevue/checkbox";
import Button from "primevue/button";
import Divider from "primevue/divider";
import Select from "primevue/select";
import Toast from "primevue/toast";
import Password from "primevue/password";
import { ref } from "vue";
import { useRouter } from "vue-router";
import { register } from "../api/auth";
import { useToast } from "primevue/usetoast";

const username = ref("");
const email = ref("");
const password = ref("");
const checked = ref(false);
const isVendor = ref(false); // Mặc định là user, nếu check thì là vendor
const router = useRouter();
const toast = useToast();

const handleSignUp = async () => {
  try {
    await register({
      username: username.value,
      email: email.value,
      password: password.value,
      isVendor: isVendor.value, // Truyền trực tiếp giá trị boolean
    });
    
    // Hiển thị toast thành công
    toast.add({
      severity: 'success',
      summary: 'Đăng ký thành công',
      detail: 'Tài khoản của bạn đã được tạo thành công!',
      life: 3000
    });
    
    // Chuyển hướng sau khi đăng ký thành công
    router.push({ name: 'login' });
  } catch (error) {
    // Hiển thị toast lỗi
    toast.add({
      severity: 'error',
      summary: 'Đăng ký thất bại',
      detail: 'Có lỗi xảy ra khi đăng ký. Vui lòng thử lại!',
      life: 5000
    });
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