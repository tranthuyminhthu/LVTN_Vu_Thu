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
                      v-model="selectedCity"
                      :options="cities"
                      optionLabel="name"
                      placeholder="Select a City"
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
                class="!rounded-full focus:!border-[#cbd5e1] h-12"
                id="phone"
              />
            </div>
          </div>
          <div class="flex flex-col gap-2">
            <label for="email">Email</label>
            <InputText
              v-model="email"
              placeholder="Nhập email của bạn"
              class="!rounded-full focus:!border-[#cbd5e1] h-12"
              type="email"
              id="email"
            />
          </div>
          <div class="flex flex-col gap-2 mt-2">
            <label for="address">Địa chỉ</label>
            <InputText
              v-model="address"
              placeholder="Nhập địa chỉ của bạn"
              class="!rounded-full focus:!border-[#cbd5e1] h-12"
              id="address"
            />
          </div>
          <div class="flex mt-4 gap-2 justify-between">
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-full flex-1"
            />
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-full flex-1"
            />
            <Select
              v-model="selectedCity"
              :options="cities"
              optionLabel="name"
              placeholder="Select a City"
              class="!rounded-full flex-1"
            />
          </div>
          <div class="flex flex-col gap-2 mt-3">
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
              <span class="w-full cursor-pointer">{{ category.name }}</span>
            </label>
          </div>
        </form>
      </div>
      <div class="col-6 max-h-screen overflow-y-auto">
        <div class="text-[30px] font-bold">Giỏ hàng</div>
        <DataTable
          v-model:selection="selectedProducts"
          :value="products"
          dataKey="id"
          tableStyle="min-width: 50rem"
        >
          <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>

          <Column field="code" header="Code">
            <template #body="slotProps">
              <img
                :src="slotProps.data.image"
                alt=""
                class="w-[120px] rounded"
              />
            </template>
          </Column>
          <Column field="name" header="Name"></Column>
          <Column field="category" header="Category"></Column>
          <Column field="quantity" header="Quantity">
            <template #body="slotProps">
              <InputNumber
                v-model="slotProps.data.quantity"
                showButtons
                buttonLayout="horizontal"
                :min="0"
                :max="99"
                size="small"
                input-class="w-[42px]"
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
        </DataTable>
        <div class="flex justify-between my-4">
          <span>Tạm tính</span>
          <span>356000đ</span>
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
            <span class="ml-auto">356000đ</span>
            <span class = "text-red-400">(Đã giảm 40.000đ trên giá gốc)</span>
          </div>
        </div>
        <div class="flex justify-between my-2">
          <span>Hoàn tiền</span>
          <span class="text-primary">356000đ</span>
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
            <p>Thành tiền <span class="text-[20px] font-bold text-[#2f5acf]">356000đ</span></p>
            <p>Được hoàn <span class="border-r border-black mr-2 pr-2 font-bold">1000đ</span>Tiết kiệm <span class="font-bold">1000đ</span></p>
          </div>
          <button class="bg-black text-white px-8 py-2 rounded-full">Thanh toán</button>
        </div>
      </div>
    </div>
  </div>
  <Dialog v-model:visible="visible" modal header="Edit Profile" :style="{ width: '25rem' }">
    <MapPicker @address-selected="handleAddress" />
</Dialog>
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
import { ref, computed } from "vue";
import MapPicker from "@/components/MapPicker.vue";
const text1 = ref("");
const phone = ref("");
const email = ref("");
const selectedCity = ref({ name: "Anh/Chị", code: "LDN" });
const cities = ref([
  { name: "Anh", code: "NY" },
  { name: "Chị", code: "RM" },
  { name: "Anh/Chị", code: "LDN" },
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
  },
  {
    name: "Ví điện tử VNPAY",
    key: "vnpay",
    image: "https://mcdn.coolmate.me/image/October2024/mceclip0_81.png",
  },
]);

const selectedPaymentImage = computed(() => {
  const selected = categories.value.find(
    (cat) => cat.name === selectedCategory.value
  );
  return selected ? selected.image : "";
});
const products = ref([
  {
    id: "1000",
    code: "f230fh0g3",
    name: "Bamboo Watch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/May2025/ao-ba-lo-mac-trong-excool-261-trang_89.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1002",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1003",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1004",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1005",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1006",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  },
  {
    id: "1007",
    code: "f2ád3",
    name: "Bamboo Wádasdatch",
    description: "Product Description",
    image:
      "https://media3.coolmate.me/cdn-cgi/image/width=320,height=362,quality=80/uploads/January2024/tatnganbassic.1_54.jpg",
    price: 65,
    category: "Accessories",
    quantity: 24,
    inventoryStatus: "INSTOCK",
    rating: 5,
  }
]);
const selectedProducts = ref([]);
const visible = ref(false);
const handleAddress = (address) => {
  console.log(address);
};
</script>
