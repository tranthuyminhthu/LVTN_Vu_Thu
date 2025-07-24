<template>
  <div class="relative p-8">
    <div class="grid">
      <div class="col-6">
        <div class="flex justify-between mb-4 items-center">
          <p class="text-[30px] font-bold">Thông tin đặt hàng</p>
          <p class="cursor-pointer text-[#2f5acf]" @click="visible = true">Chọn từ sổ địa chỉ</p>
        </div>
        <form action="" class="mt-2">
          <div class="grid items-center">
            <div class="col-8">
              <div class="flex flex-col gap-2">
                <div class="flex gap-1">
                  <label for="name" class="text-[#000000b3]">Họ tên</label>
                  <label for="name" class="text-red-500">*</label>
                </div>
                <InputGroup class="rounded-full h-12">
                  <InputGroupAddon class="!rounded-l-full !bg-[#f1f1f1]">
                    <Select
                      v-model="selectedGender"
                      :options="genders"
                      optionLabel="name"
                      placeholder="Select a Gender"
                      class="border-none !w-32 shadow-none !bg-inherit"
                    />
                  </InputGroupAddon>
                  <InputText
                    v-model="text1"
                    placeholder="Nhập họ tên của bạn"
                    :class="['!rounded-r-full focus:!border-[#cbd5e1]', errors.name ? 'border border-red-500' : 'border']"
                    id="name"
                    @blur="validateField('name')"
                  />
                </InputGroup>
                <p class="text-xs min-h-[16px] mt-1" :class="errors.name ? 'text-red-500' : 'text-transparent'">{{ errors.name || ' ' }}</p>
              </div>
            </div>
            <div class="flex flex-col gap-2 col-4">
              <div class="flex gap-1">
                <label for="phone">Số điện thoại</label>
                <label for="phone" class="text-red-500">*</label>
              </div>
              <InputText
                v-model="phone"
                placeholder="Nhập số điện thoại"
                :class="['!rounded-full focus:!border-[#cbd5e1] h-12', errors.phone ? 'border border-red-500' : 'border']"
                id="phone"
                @blur="validateField('phone')"
              />
              <p class="text-xs min-h-[16px] mt-1" :class="errors.phone ? 'text-red-500' : 'text-transparent'">{{ errors.phone || ' ' }}</p>
            </div>
          </div>
          <div class="flex flex-col gap-1">
            <div class="flex gap-1">
              <label for="email">Email</label>
              <label for="email" class="text-red-500">*</label>
            </div>
            <InputText
              v-model="email"
              placeholder="Nhập email của bạn"
              :class="['!rounded-full focus:!border-[#cbd5e1] h-12', errors.email ? 'border border-red-500' : 'border']"
              type="email"
              id="email"
              @blur="validateField('email')"
            />
            <p class="text-xs min-h-[16px] mt-1" :class="errors.email ? 'text-red-500' : 'text-transparent'">{{ errors.email || ' ' }}</p>
          </div>
          <div class="flex flex-col gap-1">
            <div class="flex justify-between">
              <div class="flex gap-1">
              <label for="address">Địa chỉ</label>
              <label for="address" class="text-red-500">*</label>
            </div>
              <div class="flex items-center gap-2 mb-2">
            <InputSwitch v-model="useNewProvinceApi" />
            <span>Địa chỉ mới</span>
          </div>
            </div>
            <InputText
              v-model="address"
              placeholder="Nhập địa chỉ của bạn"
              :class="['!rounded-full focus:!border-[#cbd5e1] h-12', errors.address ? 'border border-red-500' : 'border']"
              id="address"
              @blur="validateField('address')"
            />
            <p class="text-xs min-h-[16px] mt-1" :class="errors.address ? 'text-red-500' : 'text-transparent'">{{ errors.address || ' ' }}</p>
          </div>
          
          <div class="flex gap-2">
            <div class="flex flex-col gap-1 flex-1">
              <div class="flex gap-1">
              <label for="city">Tỉnh/Thành phố</label>
              <label for="city" class="text-red-500">*</label>
            </div>
            <Select v-model="selectedCity" :options="cities" optionLabel="name" placeholder="Chọn tỉnh/thành" :class="['!rounded-full flex-1', errors.city ? 'border border-red-500' : 'border']" 
            id="city" @blur="validateField('city')">
            <template #option="slotProps">
              <div class="flex items-center">
                <span>{{ slotProps.option.name }}</span>
              </div>
            </template>
            </Select>
            <p class="text-xs min-h-[16px] mt-1" :class="errors.city ? 'text-red-500' : 'text-transparent'">{{ errors.city || ' ' }}</p>
            </div>
            <div class="flex flex-col gap-1 flex-1" v-if="!useNewProvinceApi">
              <div class="flex gap-1">
              <label for="district">Quận/Huyện</label>
              <label for="district" class="text-red-500">*</label>
            </div>
              <Select
              v-model="selectedDistrict"
              :options="districts"
              optionLabel="name"
              placeholder="Chọn quận/huyện"
              :class="['!rounded-full flex-1', errors.district ? 'border border-red-500' : 'border']"
              id="district" @blur="validateField('district')">
            <template #option="slotProps">
              <div class="flex items-center">
                <span>{{ slotProps.option.name }}</span>
              </div>
            </template>
            </Select>
            <p class="text-xs min-h-[16px] mt-1" :class="errors.district ? 'text-red-500' : 'text-transparent'">{{ errors.district || ' ' }}</p>
            </div>
            <div class="flex flex-col gap-1 flex-1">
              <div class="flex gap-1">
                <label for="ward">Phường/Xã</label>
                <label for="ward" class="text-red-500">*</label>
              </div>
              <Select
              v-model="selectedWard"
              :options="wards"
              optionLabel="name"
              placeholder="Chọn phường/xã"
              :class="['!rounded-full flex-1', errors.ward ? 'border border-red-500' : 'border']"
              id="ward" @blur="validateField('ward')">
            <template #option="slotProps">
              <div class="flex items-center">
                <span>{{ slotProps.option.name }}</span>
              </div>
            </template>
            </Select>
            <p class="text-xs min-h-[16px] mt-1" :class="errors.ward ? 'text-red-500' : 'text-transparent'">{{ errors.ward || ' ' }}</p>
            </div>
          </div>
          <div class="flex flex-col gap-2">
            <label for="note">Ghi chú</label>
            <InputText
              v-model="note"
              placeholder="Nhập ghi chú"
              class="!rounded-full focus:!border-[#cbd5e1] h-12"
              id="note"
            />
          </div>
          <Divider class="my-4" />
          <p>Hình thức thanh toán</p>
          <div
            v-for="category in categories"
            :key="category.key"
            class="flex items-center gap-2"
          >
            <label
              :for="category.key"
              class="border border-[#e6e6e680] flex gap-2 w-full my-2 px-4 py-2 rounded-lg items-center cursor-pointer transition-colors duration-200"
              :class="{ 'bg-[#e6e6e680]': selectedCategory === category.name }"
            >
              <RadioButton
                v-model="selectedCategory"
                :inputId="category.key"
                name="dynamic"
                :value="category.name"
              />
              <img
                :src="category.image"
                alt=""
                class="max-w-[55px] max-h-[44px] min-w-[44px]"
              />
              <div class="w-full cursor-pointer flex gap-1 flex-col">
                <span>{{ category.name }}</span>
                <span v-if="category.sub" class="flex items-center gap-1">
                  <span class="text-xs">{{ category.sub }}</span>
                  <img :src="category.imageSub" alt="" class="w-4 h-4" v-if="category.imageSub">
                </span>
              </div>
            </label>
          </div>
        </form>
      </div>
      <div class="col-6 max-h-screen overflow-y-auto hide-scrollbar">
        <div class="flex items-center justify-between">
          <div class="text-[30px] font-bold">Giỏ hàng</div>
          <Button icon="pi pi-trash" class="p-button-sm bg-white !text-black !border-black cursor-pointer hover:!bg-black hover:!text-white transition-all duration-300" @click="handleBulkDelete" label="Xóa" :disabled="cartItems.length === 0" />
        </div>
        <div v-if="loading" class="flex justify-center items-center py-8">
          <div class="loading-spinner"></div>
          <span class="ml-2">Đang tải giỏ hàng...</span>
        </div>
        <div v-else-if="cartItems.length === 0" class="text-center py-8">
          <p class="text-gray-500">Giỏ hàng trống</p>
        </div>
        <DataTable
          v-else
          v-model:selection="selectedProducts"
          :value="cartItems"
          dataKey="id"
          tableStyle="min-width: 50rem"
        >
          <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>

          <Column field="image" header="Hình ảnh" headerStyle="width: 100px" bodyStyle="width: 100px" >
            <template #body="slotProps">
              <Image
                :src="getProductImage(slotProps.data.productDetails?.images)"
                alt="Product image"
                width="120"
                height="120"
                class="rounded square-image"
                preview
                :pt="{
                  image: { class: 'object-cover w-full h-full' },
                  root: { class: 'w-[120px] h-[120px]' }
                }"
              />
            </template>
          </Column>
          <Column field="name" header="Tên sản phẩm" headerStyle="width: 220px" bodyStyle="width: 220px">
            <template #body="slotProps">
              <div class="flex flex-col">
                <router-link
                  :to="{ name: 'product', params: { id: slotProps.data.productDetails.id } }"
                  class="font-semibold text-blue-600 hover:underline"
                >
                  {{ slotProps.data.productDetails?.name || 'Đang tải...' }}
                </router-link>
                <span class="text-sm text-gray-500">
                  {{ getVariantInfo(slotProps.data.sku) }}
                </span>
              </div>
            </template>
          </Column>
          <Column field="price" header="Giá" headerStyle="width: 100px" bodyStyle="width: 100px">
            <template #body="slotProps">
              <span class="font-bold">{{ formatVND(slotProps.data.price) }}</span>
            </template>
          </Column>
          <Column field="quantity" header="Số lượng" headerStyle="width: 160px" bodyStyle="width: 160px">
            <template #body="slotProps">
              <InputNumber
                v-model="slotProps.data.quantity"
                showButtons
                buttonLayout="horizontal"
                :min="1"
                :max="99"
                size="small"
                input-class="w-[42px]"
                @change="updateQuantity(slotProps.data)"
              >
                <template #incrementbuttonicon>
                  <span class="pi pi-plus" />
                </template>
                <template #decrementbuttonicon>
                  <span class="pi pi-minus" />
                </template>
              </InputNumber>
            </template>
          </Column>
          <Column field="total" header="Tổng" headerStyle="width: 220px" bodyStyle="width: 220px">
            <template #body="slotProps">
              <span class="font-bold">{{ formatVND(slotProps.data.price * slotProps.data.quantity) }}</span>
            </template>
          </Column>
        </DataTable>
        <div class="my-2 flex gap-4 overflow-x-auto w-full whitespace-nowrap pb-2">
          <CouponCard />
          <CouponCard />
          <CouponCard />
          <CouponCard />
        </div>
        <div class="my-2 flex gap-2">
          <input type="text" class="w-full rounded-full border border-[#e6e6e680] p-2 bg-[#eeeeee]" placeholder="Nhập mã giảm giá">
          <button class="bg-black text-white px-6 py-1 rounded-full cursor-pointer text-sm w-[30%]">Áp dụng Voucher</button>
        </div>
        <div class="flex justify-between my-4">
          <span>Tạm tính</span>
          <span>{{ formatVND(selectedTotal) }}</span>
        </div>
        <div class="flex justify-between my-2">
          <span>Giảm giá</span>
          <span>35000đ</span>
        </div>
        <div v-if="shippingFee !== null" class="my-2 flex justify-between">
          <span>Phí vận chuyển: </span>
          <span class="font-bold text-blue-600">{{ formatVND(shippingFee) }}</span>
        </div>
        <div class="flex justify-between my-2 border-y py-2 border-[#e6e6e680]">
          <span>Tổng cộng</span>
          <div class="flex items-center gap-2 flex-col text-right">
            <span class="ml-auto">{{ formatVND(cartData?.totalPrice || 0) }}</span>
          </div>
        </div>
      </div>
    </div>
    <div
      class="fixed bottom-0 left-0 right-0 bg-white shadow-lg z-10 border-t border-[#e6e6e680]"
    >
      <div class="flex justify-between">
        <div class="flex-1 flex items-center bg-[#2f5acf1a] p-4">
          <div class="flex-1 flex items-center justify-center">
            <img
              :src="selectedPaymentImage"
              alt=""
              class="max-w-[55px] max-h-[44px] min-w-[44px]"
            />
          </div>
          <span class="border-l border-black pl-4 flex-1 text-center"
            >Chưa dùng voucher</span
          >
        </div>
        <div class="flex-1 text-right flex items-center justify-end gap-4">
          <div class="flex flex-col">
            <p>Thành tiền <span class="text-[20px] font-bold text-[#2f5acf]">{{ formatVND(selectedTotal) }}</span></p>
          </div>
          <button class="bg-black text-white px-8 py-2 rounded-full cursor-pointer pay-btn" @click="handlePayment" :disabled="selectedProducts.length === 0">Thanh toán</button>
        </div>
      </div>
    </div>
  </div>
  <Dialog v-model:visible="visible" modal header="Edit Profile" :style="{ width: '900px', maxWidth: '95vw' }">
    <MapPicker @address-selected="handleAddress" />
  </Dialog>
  <ConfirmDialog />
  <Toast />
</template>
<script lang="ts" setup>
import InputText from "primevue/inputtext";
import InputGroup from "primevue/inputgroup";
import InputGroupAddon from "primevue/inputgroupaddon";
import Select from "primevue/select";
import Divider from "primevue/divider";
import RadioButton from "primevue/radiobutton";
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputNumber from "primevue/inputnumber";
import Dialog from 'primevue/dialog';
import Toast from 'primevue/toast';
import Image from 'primevue/image';
import { ref, computed, onMounted, watch } from "vue";
import MapPicker from "@/components/MapPicker.vue";
import { useRouter } from "vue-router";
import CouponCard from "@/components/CouponCard.vue";
import { getCart, removeCartItem, updateCartItemQuantity, removeCartItems } from "@/api/cart";
import { useToast } from "primevue/usetoast";
import { useConfirm } from "primevue/useconfirm";
import type { Cart, CartItemWithProduct, CartItem } from "@/types";
import { formatVND } from "@/common";
import Button from "primevue/button";
import ConfirmDialog from "primevue/confirmdialog";
import { createOrder } from "@/api/order";
import type { CreateOrderPayload } from "@/api/order";
import InputSwitch from 'primevue/inputswitch';
import { getProvince, getProvinceNew, getDistrict, getWard, getWardNew, getService } from '@/api/ghn';
import AddressModal from "./CartPage/AddressModal.vue";
import { calculateFee } from "@/api/ghn";
import type { CalculateFeePayload } from "@/types";

const toast = useToast();
const confirm = useConfirm();
const router = useRouter();

const loading = ref(false);
const cartData = ref<Cart | null>(null);
const cartItems = ref<CartItemWithProduct[]>([]);

const text1 = ref("");
const phone = ref("");
const email = ref("");
const selectedGender = ref({ name: "Anh/Chị"});
const genders = ref([
  { name: "Anh"},
  { name: "Chị"},
  { name: "Anh/Chị"},
]);
interface GHNProvince {
  ProvinceID: number;
  ProvinceName: string;
  [key: string]: any;
}
interface GHNDistrict {
  DistrictID: number;
  DistrictName: string;
  [key: string]: any;
}
interface GHNWard {
  WardCode: string;
  WardName: string;
  [key: string]: any;
}
interface GHNProvinceNew {
  _id: number;
  name: string;
  [key: string]: any;
}
type ProvinceOption = { name: string; id: number; raw: GHNProvince };
type DistrictOption = { name: string; id: number; raw: GHNDistrict };
type WardOption = { name: string; id: string; raw: GHNWard };
const cities = ref<ProvinceOption[]>([]);
const districts = ref<DistrictOption[]>([]);
const wards = ref<WardOption[]>([]);
const selectedCity = ref<ProvinceOption | null>(null);
const selectedDistrict = ref<DistrictOption | null>(null);
const selectedWard = ref<WardOption | null>(null);
const address = ref("");
const note = ref("");
const selectedCategory = ref("Thanh toán khi nhận hàng");
const categories = ref([
  {
    name: "Thanh toán khi nhận hàng",
    key: "cash",
    image: "https://mcdn.coolmate.me/image/October2024/mceclip2_42.png",
  },
  {
    name: "Ví điện tử VNPAY",
    key: "vnpay",
    image: "https://mcdn.coolmate.me/image/October2024/mceclip0_81.png",
    sub: "Quét Qr để thanh toán",
  },
]);

const selectedPaymentImage = computed(() => {
  const selected = categories.value.find(
    (cat) => cat.name === selectedCategory.value
  );
  return selected ? selected.image : "";
});

const selectedProducts = ref<CartItemWithProduct[]>([]);
const visible = ref(false);
const autoSelect = ref<{ wardName?: string, districtName?: string, cityName?: string } | null>(null);

const selectedTotal = computed(() =>
  selectedProducts.value.reduce(
    (sum, item) => sum + (item.price * item.quantity),
    0
  )
);

const errors = ref<{ [key: string]: string }>({});

function validateField(field: string) {
  switch (field) {
    case "name":
      errors.value.name = text1.value ? "" : "Vui lòng nhập họ tên";
      break;
    case "phone":
      errors.value.phone = phone.value ? "" : "Vui lòng nhập số điện thoại";
      break;
    case "email":
      errors.value.email = email.value ? "" : "Vui lòng nhập email";
      break;
    case "address":
      errors.value.address = address.value ? "" : "Vui lòng nhập địa chỉ";
      break;
    case "city":
      errors.value.city = selectedCity.value ? "" : "Vui lòng chọn tỉnh/thành";
      break;
    case "district":
      errors.value.district = useNewProvinceApi.value || selectedDistrict.value ? "" : "Vui lòng chọn quận/huyện";
      break;
    case "ward":
      errors.value.ward = selectedWard.value ? "" : "Vui lòng chọn phường/xã";
      break;
  }
}

function validateForm() {
  errors.value = {};
  if (!text1.value) errors.value.name = "Vui lòng nhập họ tên";
  if (!phone.value) errors.value.phone = "Vui lòng nhập số điện thoại";
  if (!email.value) errors.value.email = "Vui lòng nhập email";
  if (!address.value) errors.value.address = "Vui lòng nhập địa chỉ";
  if (!selectedCity.value) errors.value.city = "Vui lòng chọn tỉnh/thành";
  if (!useNewProvinceApi.value && !selectedDistrict.value) errors.value.district = "Vui lòng chọn quận/huyện";
  if (!selectedWard.value) errors.value.ward = "Vui lòng chọn phường/xã";
  if (selectedProducts.value.length === 0) errors.value.products = "Vui lòng chọn sản phẩm";
  return Object.keys(errors.value).length === 0;
}

const handleAddress = (data: { address: string, wardName?: string, districtName?: string, cityName?: string }) => {
  if (data.address) address.value = data.address;
  autoSelect.value = data;
  console.log(data);
  if (data.cityName) {
    let cityNameMap:string = "";
    if (data.cityName === "Ho Chi Minh City") cityNameMap = "Hồ Chí Minh";
    const city = cities.value.find(c => c.name.includes(cityNameMap));
    if (city) selectedCity.value = city;
  }
  visible.value = false;
  toast.add({
    severity: 'success',
    summary: 'Thành công',
    detail: 'Đã cập nhật thông tin địa chỉ giao hàng',
    life: 2000
  });
};

watch([selectedCity, districts], ([city, districtList]) => {
  if (autoSelect.value && autoSelect.value.districtName && districtList.length > 0) {
    const district = districtList.find(d => d.name.toLowerCase().includes(autoSelect.value!.districtName!.toLowerCase()));
    if (district) selectedDistrict.value = district;
  }
});
watch([selectedDistrict, wards], ([district, wardList]) => {
  if (autoSelect.value && autoSelect.value.wardName && wardList.length > 0) {
    const ward = wardList.find(w => w.name.toLowerCase().includes(autoSelect.value!.wardName!.toLowerCase()));
    if (ward) selectedWard.value = ward;
    autoSelect.value = null; // reset sau khi chọn xong
  }
});

const shippingFee = ref<number | null>(null);

async function fetchShippingFee() {
  if (!selectedCity.value || !selectedDistrict.value || !selectedWard.value) return;

  const items = selectedProducts.value.map(item => ({
    name: item.productDetails?.name || '',
    quantity: item.quantity,
    height: 20,
    weight: 200,
    length: 20,
    width: 20,
  }));

  const payload: CalculateFeePayload = {
    from_district_id: 1455,
    from_ward_code: "21402",
    service_id: 53322,
    to_district_id: selectedDistrict.value.id,
    to_ward_code: selectedWard.value.id,
    weight: items.reduce((sum, i) => sum + i.weight, 0) || 200,
    length: 20,
    width: 20,
    height: 50,
    insurance_value: 10000,
    cod_failed_amount: 2000,
    coupon: null,
    items
  };

  try {
    const service = await getService({
      from_district: 1455,
      to_district: selectedDistrict.value.id,
      shop_id: 197170
    });
    const serviceId = service.data.find((item: any) => item.short_name === "Hàng nhẹ")?.service_id;
    payload.service_id = serviceId;
    const res = await calculateFee(payload);
    shippingFee.value = res.data.total || res.data.fee;
  } catch (e) {
    shippingFee.value = 22000;
  }
}

watch(selectedWard, (ward) => {
  if (ward && selectedCity.value && selectedDistrict.value) {
    const cityCode = selectedCity.value.id || selectedCity.value.raw?.ProvinceID || selectedCity.value.raw?._id;
    const districtCode = selectedDistrict.value.id || selectedDistrict.value.raw?.DistrictID;
    const wardCode = ward.id || ward.raw?.WardCode || ward.raw?._id;
    fetchShippingFee();
  }
});

const handlePayment = async () => {
  if (!validateForm()) return;

  // Chuẩn bị dữ liệu đơn hàng
  const orderPayload: CreateOrderPayload = {
    items: selectedProducts.value.map(item => {
      let image: string | undefined = undefined;
      const img = item.productDetails?.images?.[0];
      if (typeof img === 'string') image = img;
      // Nếu là File thì bỏ qua hoặc lấy ''
      return {
        cartItemId: item.id,
        productSku: item.sku,
        productName: item.productDetails?.name || '',
        quantity: item.quantity,
        price: item.price,
        image
      };
    }),
    shippingAddress: address.value,
    paymentMethod: selectedCategory.value === 'Thanh toán khi nhận hàng' ? 'CASH' : selectedCategory.value,
    receiverName: text1.value,
    receiverPhone: phone.value,
    receiverEmail: email.value,
    note: note.value,
    fee: shippingFee.value || 0,
    vendorId: selectedProducts.value[0].productDetails?.vendorInfo.id || "",
    receiverDistrictId: selectedDistrict.value?.id || 0,
    receiverWardCode: selectedWard.value?.id || "",
  };

  try {
    await createOrder(orderPayload);
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Đơn hàng đã được tạo thành công!',
      life: 2000
    });
    router.push('/order-success');
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tạo đơn hàng. Vui lòng thử lại.',
      life: 3000
    });
  }
};

const updateQuantity = (item: CartItemWithProduct) => {
  console.log('Updated quantity for item:', item);
  updateCartItemQuantity(item.id, item.quantity)
    .then(() => {
      toast.add({
        severity: 'success',
        summary: 'Thành công',
        detail: 'Đã cập nhật số lượng sản phẩm',
        life: 2000
      });
      loadCart(); // Reload cart to update total price
    })
    .catch((error) => {
      console.error('Error updating quantity:', error);
      toast.add({
        severity: 'error',
        summary: 'Lỗi',
        detail: 'Không thể cập nhật số lượng sản phẩm',
        life: 3000
      });
    });
};

const getProductImage = (images?: (string | File)[]) => {
  if (images && images.length > 0 && typeof images[0] === 'string') {
    return images[0];
  }
  return 'https://via.placeholder.com/120';
};

const getVariantInfo = (sku: string) => {
  // Parse SKU format: "productId-colorHex-size"
  const parts = sku.split('-');
  if (parts.length >= 3) {
    const colorHex = parts[1];
    const size = parts[2];
    
    // Map color hex to color name
    const colorMap: { [key: string]: string } = {
      '#FF0000': 'Red',
      '#0000FF': 'Blue', 
      '#000000': 'Black',
      '#FFFFFF': 'White',
      '#FFFF00': 'Yellow',
      '#00FF00': 'Green'
    };
    
    const colorName = colorMap[colorHex] || colorHex;
    return `${colorName} - ${size}`;
  }
  return sku;
};

const loadCart = async () => {
  loading.value = true;
  try {
    const response = await getCart();
    cartData.value = response;
    cartItems.value = response.items;
  } catch (error) {
    console.log(error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải giỏ hàng',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

const useNewProvinceApi = ref(false);

const fetchProvinces = async () => {
  try {
    let res;
    if (useNewProvinceApi.value) {
      res = await getProvinceNew();
      if (res && res.data) {
        cities.value = res.data
          .filter((item: any) => {
            const name = (item.name || '').toLowerCase();
            return !(
              name.includes('test') ||
              name.includes('ngoc test') ||
              name.includes('hà nội 02') ||
              name.includes('ha noi 02')
            );
          })
          .map((item: any) => ({
            name: item.name,
            id: item._id,
            raw: item
          }));
      }
    } else {
      res = await getProvince();
      if (res && res.data) {
        cities.value = res.data
          .filter((item: any) => {
            const name = (item.ProvinceName || '').toLowerCase();
            return !(
              name.includes('test') ||
              name.includes('ngoc test') ||
              name.includes('hà nội 02') ||
              name.includes('ha noi 02')
            );
          })
          .map((item: any) => ({
            name: item.ProvinceName,
            id: item.ProvinceID,
            raw: item
          }));
      }
    }
  } catch (e) {
    console.error('Lỗi lấy tỉnh thành:', e);
  }
};

watch(useNewProvinceApi, async () => {
  selectedCity.value = null;
  selectedDistrict.value = null;
  selectedWard.value = null;
  districts.value = [];
  wards.value = [];
  await fetchProvinces();
});

onMounted(async () => {
  loadCart();
  await fetchProvinces();
});

// Sửa watcher selectedCity:
watch(selectedCity, async (city) => {
  selectedDistrict.value = null;
  selectedWard.value = null;
  districts.value = [];
  wards.value = [];
  if (city && city.id) {
    if (useNewProvinceApi.value) {
      // API mới: lấy xã theo tỉnh
      try {
        const res = await getWardNew(city.id);
        if (res && res.data) {
          wards.value = res.data.map((item: any) => ({
            name: item.name,
            id: item._id,
            raw: item
          }));
        }
      } catch (e) {
        console.error('Lỗi lấy xã GHN mới:', e);
      }
    } else {
      // API cũ: lấy quận
      try {
        const res = await getDistrict(city.id);
        if (res && res.data) {
          districts.value = res.data.map((item: any) => ({
            name: item.DistrictName,
            id: item.DistrictID,
            raw: item
          }));
        }
      } catch (e) {
        console.error('Lỗi lấy quận huyện GHN:', e);
      }
    }
  }
});
// Sửa watcher selectedDistrict:
watch(selectedDistrict, async (district) => {
  selectedWard.value = null;
  if (!useNewProvinceApi.value && district && district.id) {
    try {
      const res = await getWard(district.id);
      if (res && res.data) {
        wards.value = res.data.map((item: any) => ({
          name: item.WardName,
          id: item.WardCode,
          raw: item
        }));
      }
    } catch (e) {
      console.error('Lỗi lấy phường xã GHN:', e);
    }
  } else if (!useNewProvinceApi.value) {
    wards.value = [];
  }
});

const handleBulkDelete = () => {
  if (selectedProducts.value.length === 0) {
    toast.add({
      severity: 'warn',
      summary: 'Cảnh báo',
      detail: 'Vui lòng chọn sản phẩm để xóa.',
      life: 3000
    });
    return;
  }

  confirm.require({
    message: `Bạn có chắc chắn muốn xóa ${selectedProducts.value.length} sản phẩm đã chọn khỏi giỏ hàng không?`,
    header: 'Xác nhận xóa nhiều sản phẩm',
    icon: 'pi pi-exclamation-triangle',
    accept: () => {
      const itemIdsToDelete = selectedProducts.value.map(item => item.id);
      removeCartItems(itemIdsToDelete)
        .then(() => {
          toast.add({
            severity: 'success',
            summary: 'Thành công',
            detail: `Đã xóa ${itemIdsToDelete.length} sản phẩm khỏi giỏ hàng.`,
            life: 3000
          });
          loadCart();
          selectedProducts.value = [];
        })
        .catch((error) => {
          console.error('Error deleting bulk cart items:', error);
          toast.add({
            severity: 'error',
            summary: 'Lỗi',
            detail: 'Không thể xóa sản phẩm khỏi giỏ hàng.',
            life: 3000
          });
        });
    },
    reject: () => {
      // User rejected the bulk delete
    }
  });
};

</script>

<style>
.hide-scrollbar {
  overflow-y: auto;
  scrollbar-width: none;
}
.hide-scrollbar::-webkit-scrollbar {
  display: none;
}

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

.square-image {
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.square-image img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
}
.pay-btn:disabled {
  background-color: #e5e7eb !important; /* Tailwind gray-200 */
  color: #a1a1aa !important;            /* Tailwind gray-400 */
  cursor: not-allowed !important;
  border: none;
  box-shadow: none;
}
.pay-btn:disabled:hover {
  background-color: #e5e7eb !important;
  color: #a1a1aa !important;
  cursor: not-allowed !important;
}
</style>