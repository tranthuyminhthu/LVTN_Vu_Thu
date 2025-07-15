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
                <label for="name" class="text-[#000000b3]">Họ tên</label>
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
                    class="!rounded-r-full focus:!border-[#cbd5e1]"
                    id="name"
                  />
                </InputGroup>
              </div>
            </div>
            <div class="flex flex-col gap-2 col-4">
              <label for="phone">Số điện thoại</label>
              <InputText
                v-model="phone"
                placeholder="Nhập số điện thoại"
                class="!rounded-r-full focus:!border-[#cbd5e1] h-12"
                id="phone"
              />
            </div>
          </div>
          <div class="flex flex-col gap-2">
            <label for="email">Email</label>
            <InputText
              v-model="email"
              placeholder="Nhập email của bạn"
              class="!rounded-r-full focus:!border-[#cbd5e1] h-12"
              type="email"
              id="email"
            />
          </div>
          <div class="flex flex-col gap-2 mt-2">
            <label for="address">Địa chỉ</label>
            <InputText
              v-model="address"
              placeholder="Nhập địa chỉ của bạn"
              class="!rounded-r-full focus:!border-[#cbd5e1] h-12"
              id="address"
            />
          </div>
          <div class="flex mt-4 gap-2 justify-between">
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-r-full flex-1"
            />
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-r-full flex-1"
            />
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-r-full flex-1"
            />
          </div>
          <div class="flex flex-col gap-2 mt-3">
            <label for="note">Ghi chú</label>
            <InputText
              v-model="note"
              placeholder="Nhập ghi chú"
              class="!rounded-r-full focus:!border-[#cbd5e1] h-12"
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
        <div class="flex justify-between my-2">
          <span>Phí vận chuyển</span>
          <span>356000đ</span>
        </div>
        <div class="flex justify-between my-2 border-y py-2 border-[#e6e6e680]">
          <span>Tổng cộng</span>
          <div class="flex items-center gap-2 flex-col text-right">
            <span class="ml-auto">{{ formatVND(cartData?.totalPrice || 0) }}</span>
            <span class = "text-red-400">(Đã giảm 40.000đ trên giá gốc)</span>
          </div>
        </div>
        <div class="flex justify-between my-2">
          <span>Hoàn tiền</span>
          <span class="text-primary">{{ formatVND(cartData?.totalPrice || 0) }}</span>
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
            <p>Được hoàn <span class="border-r border-black mr-2 pr-2 font-bold">1000đ</span>Tiết kiệm <span class="font-bold">1000đ</span></p>
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
import { ref, computed, onMounted } from "vue";
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
const selectedCity = ref(null);
const cities = ref([
  { name: "Hà Nội" },
  { name: "TP. Hồ Chí Minh" },
  { name: "Đà Nẵng" },
  { name: "Hải Phòng" },
  { name: "Cần Thơ" }
]);
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
    name: "Ví momo",
    key: "momo",
    image: "https://mcdn.coolmate.me/image/October2024/mceclip1_171.png",
  },
  {
    name: "Thanh toán qua ZaloPay",
    key: "zalo",
    image: "https://mcdn.coolmate.me/image/October2024/mceclip3_6.png",
    sub: "Hỗ trợ mọi hình thức thanh toán",
    imageSub: "https://mcdn.coolmate.me/image/October2024/mceclip0_27.png",
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

const selectedTotal = computed(() =>
  selectedProducts.value.reduce(
    (sum, item) => sum + (item.price * item.quantity),
    0
  )
);

const handleAddress = (data: { address: string, name?: string, phone?: string, email?: string }) => {
  console.log('Received address data:', data);
  
  // Map thông tin vào các ô input
  if (data.address) address.value = data.address;
  if (data.name) text1.value = data.name;
  if (data.phone) phone.value = data.phone;
  if (data.email) email.value = data.email;
  
  // Đóng dialog sau khi chọn
  visible.value = false;
  
  // Hiển thị thông báo thành công
  toast.add({
    severity: 'success',
    summary: 'Thành công',
    detail: 'Đã cập nhật thông tin địa chỉ giao hàng',
    life: 2000
  });
};

const handlePayment = () => {
  router.push('/order-success');
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

onMounted(() => {
  loadCart();
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