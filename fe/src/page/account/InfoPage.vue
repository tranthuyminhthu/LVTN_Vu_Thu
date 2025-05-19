<template>
  <p class="text-2xl font-bold">Thông tin tài khoản</p>
  <div class="grid my-2">
    <span class="col-4">Họ và Tên</span>
    <span class="col-8">Nguyễn Duy Vụ</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Số điện thoại</span>
    <span class="col-8">034201231</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Giới tính</span>
    <span class="col-8">Nam</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Ngày sinh</span>
    <span class="col-8">26/10/2003</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Chiều cao</span>
    <span class="col-8">165cm</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Cân nặng</span>
    <span class="col-8">60kg</span>
  </div>
  <button
    class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer hover:!bg-gray-200 transition-all duration-300"
    @click="isShowDialogEditInfo = true"
  >
    Cập nhật
  </button>
  <p class="text-2xl font-bold mt-4">Thông tin đăng nhập</p>
  <div class="grid my-2">
    <span class="col-4">Email</span>
    <span class="col-8">Nguyễn Duy Vụ</span>
  </div>
  <div class="grid my-2">
    <span class="col-4">Mật khẩu</span>
    <span class="col-8">*****************</span>
  </div>
  <button
    class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer hover:!bg-gray-200 transition-all duration-300"
    @click="isShowDialogEditPassword = true"
  >
    Cập nhật
  </button>
  <Dialog
    v-model:visible="isShowDialogEditInfo"
    modal
    header="Chỉnh sửa thông tin tài khoản"
    style="width: 40vw"
  >
    <InputGroup class="md:w-80 !rounded-full">
      <InputGroupAddon class="!rounded-l-full">
        <i class="pi pi-shopping-cart"></i>
      </InputGroupAddon>
      <IftaLabel>
        <InputText
          v-model="name"
          inputId="name"
          class="!rounded-r-full focus:!border-gray-300"
          style="border-left: none"
        />
        <label for="name">Price</label>
      </IftaLabel>
    </InputGroup>
    <div class="flex flex-wrap gap-4 my-4">
      <div class="flex items-center gap-2">
        <RadioButton 
          v-model="gender" 
          inputId="male" 
          name="gender" 
          value="male"
          :class="{'border-2 border-black rounded-full': gender === 'male'}"
        />
        <label for="male" class="cursor-pointer">Nam</label>
      </div>
      <div class="flex items-center gap-2">
        <RadioButton
          v-model="gender"
          inputId="female"
          name="gender"
          value="female"
          :class="{'border-2 border-black rounded-full': gender === 'female'}"
        />
        <label for="female" class="cursor-pointer">Nữ</label>
      </div>
      <div class="flex items-center gap-2">
        <RadioButton 
          v-model="gender" 
          inputId="lgbt" 
          name="gender" 
          value="lgbt"
          :class="{'border-2 border-black rounded-full': gender === 'lgbt'}"
        />
        <label for="lgbt" class="cursor-pointer">LGBT</label>
      </div>
    </div>
    <InputGroup class="md:w-80 !rounded-full">
      <InputGroupAddon class="!rounded-l-full">
        <i class="pi pi-phone"></i>
      </InputGroupAddon>
      <IftaLabel>
        <InputText
          v-model="phone"
          inputId="phone"
          class="!rounded-r-full focus:!border-gray-300"
          style="border-left: none"
        />
        <label for="phone">Số điện thoại</label>
      </IftaLabel>
    </InputGroup>
    <div class="my-4 flex items-center gap-2 justify-between">
      <span>Chiều cao</span>
      <div class="flex items-center gap-4">
        <Slider v-model="height" class="w-56" :min="0" :max="250" />
        <span class="text-sm text-primary w-[60px] text-right"
          >{{ height }}cm</span
        >
      </div>
    </div>
    <div class="my-4 flex items-center gap-2 justify-between">
      <span>Cân nặng</span>
      <div class="flex items-center gap-4">
        <Slider v-model="weight" class="w-56" :min="0" :max="250" />
        <span class="text-sm text-primary w-[60px] text-right"
          >{{ weight }}kg</span
        >
      </div>
    </div>
    <button
      class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer w-full bg-black text-white hover:!bg-gray-600 transition-all duration-300"
    >
      Cập nhật
    </button>
  </Dialog>
  <Dialog
    v-model:visible="isShowDialogEditPassword"
    modal
    header="Thay đổi mật khẩu"
    style="width: 40vw"
  >
    <div class="gap-2 flex flex-col">
      <Password
        v-model="password"
        placeholder="Mật khẩu cũ"
        toggleMask
        class="!w-full"
      />
      <Password v-model="newPassword" placeholder="Mật khẩu mới" toggleMask />
      <Password
        v-model="confirmPassword"
        placeholder="Xác nhận mật khẩu"
        toggleMask
      />
      <button
        class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer w-full bg-black text-white hover:!bg-gray-600 transition-all duration-300"
      >
        Cập nhật
      </button>
    </div>
  </Dialog>
</template>

<script setup lang="ts">
import Dialog from "primevue/dialog";
import InputGroup from "primevue/inputgroup";
import InputGroupAddon from "primevue/inputgroupaddon";
import InputText from "primevue/inputtext";
import IftaLabel from "primevue/iftalabel";
import RadioButton from "primevue/radiobutton";
import Slider from "primevue/slider";
import Password from "primevue/password";
import { ref, watch } from "vue";
import type { input } from "@primeuix/themes/aura/iftalabel";
const isShowDialogEditInfo = ref(false);
const isShowDialogEditPassword = ref(false);
const name = ref("");
const gender = ref("male");
const phone = ref("");
const height = ref(165);
const weight = ref(60);
const password = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

watch([height, weight], ([newHeight, newWeight]) => {
  console.log('Size changed:', { height: newHeight, weight: newWeight });
});
</script>

<style scoped>
:deep(.p-password input) {
  width: 100% !important;
}
</style>
