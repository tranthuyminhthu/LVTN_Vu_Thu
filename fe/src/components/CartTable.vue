<template>
  <DataTable
    :value="items"
    v-model:selection="localSelectedItems"
    dataKey="id"
    tableStyle="min-width: 50rem"
    :loading="loading"
    @row-select="onRowSelect"
    @row-unselect="onRowUnselect"
    @select-all-change="onSelectAllChange"
  >
    <Column selectionMode="multiple" headerStyle="width: 3rem"></Column>

    <Column field="image" header="Hình ảnh" headerStyle="width: 100px" bodyStyle="width: 100px">
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
</template>

<script setup lang="ts">
import DataTable from "primevue/datatable";
import Column from "primevue/column";
import InputNumber from "primevue/inputnumber";
import Image from 'primevue/image';
import { ref } from 'vue';
import type { CartItemWithProduct } from "@/types";
import { formatVND } from "@/common";

interface Props {
  items: CartItemWithProduct[];
  loading: boolean;
  selectedItems: CartItemWithProduct[];
}

interface Emits {
  (e: 'update:selectedItems', items: CartItemWithProduct[]): void;
  (e: 'updateQuantity', item: CartItemWithProduct): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// Local state for selection - initialize with props
const localSelectedItems = ref<CartItemWithProduct[]>([...props.selectedItems]);

interface SelectionEvent {
  data: CartItemWithProduct;
}

interface SelectAllEvent {
  checked: boolean;
}

const onRowSelect = (event: SelectionEvent) => {
  console.log('Row selected:', event.data);
  // Update local state first
  localSelectedItems.value = [...localSelectedItems.value, event.data];
  // Then emit to parent
  emit('update:selectedItems', localSelectedItems.value);
};

const onRowUnselect = (event: SelectionEvent) => {
  console.log('Row unselected:', event.data);
  // Update local state first
  localSelectedItems.value = localSelectedItems.value.filter(item => item.id !== event.data.id);
  // Then emit to parent
  emit('update:selectedItems', localSelectedItems.value);
};

const onSelectAllChange = (event: SelectAllEvent) => {
  console.log('Select all changed:', event.checked);
  if (event.checked) {
    localSelectedItems.value = [...props.items];
  } else {
    localSelectedItems.value = [];
  }
  emit('update:selectedItems', localSelectedItems.value);
};

const updateQuantity = (item: CartItemWithProduct) => {
  emit('updateQuantity', item);
};

const getProductImage = (images?: (string | File)[]) => {
  if (images && images.length > 0 && typeof images[0] === 'string') {
    return images[0];
  }
  return 'https://via.placeholder.com/120';
};

const getVariantInfo = (sku: string) => {
  const parts = sku.split('-');
  if (parts.length >= 3) {
    const colorHex = parts[1];
    const size = parts[2];
    
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
</script>

<style scoped>
.square-image {
  aspect-ratio: 1 / 1;
  object-fit: cover;
}

.square-image img {
  width: 100% !important;
  height: 100% !important;
  object-fit: cover !important;
}
</style> 