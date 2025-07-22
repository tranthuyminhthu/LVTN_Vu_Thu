<template>
  <p class="text-2xl font-bold">Thông tin tài khoản</p>
  <div v-if="loading" class="flex items-center gap-2 my-4">
    <div class="loading-spinner"></div>
    <span>Đang tải thông tin...</span>
  </div>
  <div v-else-if="userProfile">
    <div class="grid my-2">
      <span class="col-4">Họ và Tên</span>
      <span class="col-8">{{ userProfile.fullName || userProfile.username }}</span>
    </div>
    <div class="grid my-2">
      <span class="col-4">Số điện thoại</span>
      <span class="col-8">{{ userProfile.phone || 'Chưa cập nhật' }}</span>
    </div>
    <div class="grid my-2">
      <span class="col-4">Giới tính</span>
      <span class="col-8">{{ genderDisplay(userProfile.gender) }}</span>
    </div>
    <div class="grid my-2">
      <span class="col-4">Ngày sinh</span>
      <span class="col-8">{{ formatDob(userProfile.dob) }}</span>
    </div>
    <div class="grid my-2">
      <span class="col-4">Chiều cao</span>
      <span class="col-8">{{ userProfile.height ? userProfile.height + 'cm' : 'Chưa cập nhật' }}</span>
    </div>
    <div class="grid my-2">
      <span class="col-4">Cân nặng</span>
      <span class="col-8">{{ userProfile.weight ? userProfile.weight + 'kg' : 'Chưa cập nhật' }}</span>
    </div>
    <button
      class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer hover:!bg-gray-200 transition-all duration-300"
      @click="openEditInfoDialog"
    >
      Cập nhật
    </button>
    <p class="text-2xl font-bold mt-4">Thông tin đăng nhập</p>
    <div class="grid my-2">
      <span class="col-4">Email</span>
      <span class="col-8">{{ userProfile.email }}</span>
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
  </div>
  <div v-else class="text-gray-500 my-4">Không thể tải thông tin tài khoản</div>

  <!-- Dialogs -->
  <Dialog
    v-model:visible="isShowDialogEditInfo"
    modal
    header="Chỉnh sửa thông tin tài khoản"
    style="width: 50vw"
  >
    <!-- Email -->
    <InputGroup class="md:w-80 !rounded-full mb-4">
      <InputGroupAddon class="!rounded-l-full">
        <i class="pi pi-envelope"></i>
      </InputGroupAddon>
      <IftaLabel>
        <InputText
          v-model="editEmail"
          inputId="email"
          class="!rounded-r-full focus:!border-gray-300"
          style="border-left: none"
        />
        <label for="email">Email</label>
      </IftaLabel>
    </InputGroup>

    <!-- Phone -->
    <InputGroup class="md:w-80 !rounded-full mb-4">
      <InputGroupAddon class="!rounded-l-full">
        <i class="pi pi-phone"></i>
      </InputGroupAddon>
      <IftaLabel>
        <InputText
          v-model="editPhone"
          inputId="phone"
          class="!rounded-r-full focus:!border-gray-300"
          style="border-left: none"
        />
        <label for="phone">Số điện thoại</label>
      </IftaLabel>
    </InputGroup>

    <!-- Full Name -->
    <InputGroup class="md:w-80 !rounded-full mb-4">
      <InputGroupAddon class="!rounded-l-full">
        <i class="pi pi-user"></i>
      </InputGroupAddon>
      <IftaLabel>
        <InputText
          v-model="editName"
          inputId="name"
          class="!rounded-r-full focus:!border-gray-300"
          style="border-left: none"
        />
        <label for="name">Họ và tên</label>
      </IftaLabel>
    </InputGroup>

    <!-- Gender -->
    <div class="flex flex-wrap gap-4 my-4">
      <div class="flex items-center gap-2">
        <RadioButton 
          v-model="editGender" 
          inputId="male" 
          name="gender" 
          value="male"
          :class="{'border-2 border-black rounded-full': editGender === 'male'}"
        />
        <label for="male" class="cursor-pointer">Nam</label>
      </div>
      <div class="flex items-center gap-2">
        <RadioButton
          v-model="editGender"
          inputId="female"
          name="gender"
          value="female"
          :class="{'border-2 border-black rounded-full': editGender === 'female'}"
        />
        <label for="female" class="cursor-pointer">Nữ</label>
      </div>
      <div class="flex items-center gap-2">
        <RadioButton 
          v-model="editGender" 
          inputId="lgbt" 
          name="gender" 
          value="lgbt"
          :class="{'border-2 border-black rounded-full': editGender === 'lgbt'}"
        />
        <label for="lgbt" class="cursor-pointer">LGBT</label>
      </div>
    </div>

    <!-- Date of Birth -->
    <div class="mb-4">
      <label class="block mb-2 font-semibold">Ngày sinh</label>
      <Calendar
        v-model="editDob"
        class="w-full"
        placeholder="Chọn ngày sinh"
        dateFormat="dd/mm/yy"
      />
    </div>

    <!-- Height -->
    <div class="my-4 flex items-center gap-2 justify-between">
      <span>Chiều cao</span>
      <div class="flex items-center gap-4">
        <Slider v-model="editHeight" class="w-56" :min="0" :max="250" />
        <span class="text-sm text-primary w-[60px] text-right"
          >{{ editHeight }}cm</span
        >
      </div>
    </div>

    <!-- Weight -->
    <div class="my-4 flex items-center gap-2 justify-between">
      <span>Cân nặng</span>
      <div class="flex items-center gap-4">
        <Slider v-model="editWeight" class="w-56" :min="0" :max="250" />
        <span class="text-sm text-primary w-[60px] text-right"
          >{{ editWeight }}kg</span
        >
      </div>
    </div>

    <button
      class="px-4 py-2 rounded-full mt-2 border font-bold cursor-pointer w-full bg-black text-white hover:!bg-gray-600 transition-all duration-300"
      @click="updateProfile"
      :disabled="updating"
    >
      {{ updating ? 'Đang cập nhật...' : 'Cập nhật' }}
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
import { ref, watch, onMounted } from "vue";
import { userApi } from "@/api/user";
import type { UserInfo } from "@/types";
import Calendar from "primevue/calendar";

const isShowDialogEditInfo = ref(false);
const isShowDialogEditPassword = ref(false);
const password = ref("");
const newPassword = ref("");
const confirmPassword = ref("");

const userProfile = ref<UserInfo | null>(null);
const loading = ref(false);

// Editable fields for dialog
const editName = ref("");
const editGender = ref("");
const editHeight = ref(0);
const editWeight = ref(0);
const editEmail = ref("");
const editPhone = ref("");
const editImage = ref("");
const editDob = ref<Date | null>(null);
const updating = ref(false);

const loadUserProfile = async () => {
  try {
    loading.value = true;
    const profile = await userApi.getCurrentProfile();
    userProfile.value = {
      userId: profile.userId,
      phone: profile.phone,
      email: profile.email,
      username: profile.username,
      fullName: profile.fullName,
      type: profile.type,
      image: profile.image,
      height: profile.height,
      weight: profile.weight,
      gender: profile.gender,
      shopName: profile.shopName,
      role: Array.isArray(profile.role) ? profile.role : [profile.role || ""],
      dob: profile.dob
    };
    editDob.value = userProfile.value.dob ? new Date(userProfile.value.dob) : null;
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadUserProfile();
});

function genderDisplay(gender: string | null) {
  if (!gender) return "Chưa cập nhật";
  if (gender === "male" || gender === "Nam") return "Nam";
  if (gender === "female" || gender === "Nữ") return "Nữ";
  if (gender === "lgbt") return "LGBT";
  return gender;
}

function formatDob(dob: string | null) {
  if (!dob) return "Chưa cập nhật";
  // Nếu dob là yyyy-mm-dd hoặc yyyy-mm-ddTHH:mm:ss thì chỉ lấy yyyy-mm-dd
  const date = dob.split("T")[0];
  const [y, m, d] = date.split("-");
  if (y && m && d) return `${d}/${m}/${y}`;
  return dob;
}

function formatDateToYMD(date: Date | string | null | undefined): string | undefined {
  if (!date) return undefined;
  if (typeof date === 'string') return date;
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

function openEditInfoDialog() {
  if (userProfile.value) {
    editName.value = userProfile.value.fullName || userProfile.value.username;
    editGender.value = userProfile.value.gender || "";
    editHeight.value = userProfile.value.height || 0;
    editWeight.value = userProfile.value.weight || 0;
    editEmail.value = userProfile.value.email || "";
    editPhone.value = userProfile.value.phone || "";
    editImage.value = userProfile.value.image || "";
    editDob.value = userProfile.value.dob ? new Date(userProfile.value.dob) : null;
    isShowDialogEditInfo.value = true;
  }
}

const updateProfile = async () => {
  if (!userProfile.value) return;

  const profileData = {
    email: editEmail.value || undefined,
    phone: editPhone.value || undefined,
    fullName: editName.value || undefined,
    image: editImage.value || undefined,
    gender: editGender.value || undefined,
    dob: formatDateToYMD(editDob.value),
    height: editHeight.value || undefined,
    weight: editWeight.value || undefined,
  };

  try {
    updating.value = true;
    await userApi.updateProfile(profileData);
    // Update local state
    if (userProfile.value) {
      userProfile.value = {
        ...userProfile.value,
        email: editEmail.value || userProfile.value.email,
        phone: editPhone.value || userProfile.value.phone,
        fullName: editName.value || userProfile.value.fullName,
        image: editImage.value || userProfile.value.image,
        gender: editGender.value || userProfile.value.gender,
        dob: formatDateToYMD(editDob.value) || userProfile.value.dob,
        height: editHeight.value || userProfile.value.height,
        weight: editWeight.value || userProfile.value.weight,
      };
    }
    isShowDialogEditInfo.value = false;
    alert("Cập nhật thông tin thành công!");
  } catch (error) {
    console.error("Error updating profile:", error);
    alert("Cập nhật thông tin thất bại.");
  } finally {
    updating.value = false;
  }
};
</script>

<style scoped>
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
:deep(.p-password input) {
  width: 100% !important;
}
</style>
