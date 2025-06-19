<template>
  <div class="p-6">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-semibold">Quản lý sản phẩm</h1>
      <button
        @click="openAddProductModal"
        class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <i class="pi pi-plus mr-2"></i>Thêm sản phẩm mới
      </button>
    </div>

    <!-- Product List -->
    <div class="bg-white rounded-lg shadow-sm">
      <div class="p-4 border-b">
        <div class="flex items-center gap-4">
          <div class="flex-1">
            <input
              v-model="searchQuery"
              type="text"
              placeholder="Tìm kiếm sản phẩm..."
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          <select
            v-model="sortBy"
            class="px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="name">Tên sản phẩm</option>
            <option value="price">Giá</option>
            <option value="stock">Tồn kho</option>
            <option value="views">Lượt xem</option>
            <option value="sales">Lượt mua</option>
          </select>
        </div>
      </div>

      <div class="overflow-x-auto">
        <table class="w-full">
          <thead class="bg-gray-50">
            <tr>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Sản phẩm
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Giá
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Tồn kho
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Lượt xem
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Lượt mua
              </th>
              <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                Thao tác
              </th>
            </tr>
          </thead>
          <tbody class="bg-white divide-y divide-gray-200">
            <tr v-for="product in filteredProducts" :key="product.id">
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                  <img
                    :src="product.image"
                    :alt="product.name"
                    class="w-10 h-10 rounded-lg object-cover"
                  />
                  <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">{{ product.name }}</div>
                    <div class="text-sm text-gray-500">{{ product.category }}</div>
                  </div>
                </div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ formatPrice(product.price) }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ product.stock }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ product.views }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap">
                <div class="text-sm text-gray-900">{{ product.sales }}</div>
              </td>
              <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button
                  @click="editProduct(product)"
                  class="text-blue-600 hover:text-blue-900 mr-3"
                >
                  <i class="pi pi-pencil"></i>
                </button>
                <button
                  @click="deleteProduct(product)"
                  class="text-red-600 hover:text-red-900"
                >
                  <i class="pi pi-trash"></i>
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Add/Edit Product Modal -->
    <Dialog v-model:visible="showModal" :modal="true" :closable="true" :style="{ width: '500px' }" :header="isEditing ? 'Chỉnh sửa sản phẩm' : 'Thêm sản phẩm mới'">
      <form @submit.prevent="saveProduct" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-gray-700">Tên sản phẩm</label>
          <input
            v-model="currentProduct.name"
            type="text"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">Giá</label>
          <input
            v-model="currentProduct.price"
            type="number"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">Số lượng</label>
          <input
            v-model="currentProduct.stock"
            type="number"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            required
          />
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">Mô tả</label>
          <textarea
            v-model="currentProduct.description"
            rows="3"
            class="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
          ></textarea>
        </div>
        <div>
          <label class="block text-sm font-medium text-gray-700">Hình ảnh</label>
          <input
            type="file"
            @change="handleImageUpload"
            accept="image/*"
            class="mt-1 block w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-md file:border-0 file:text-sm file:font-semibold file:bg-blue-50 file:text-blue-700 hover:file:bg-blue-100"
          />
        </div>
        <div class="flex justify-end gap-3 mt-4">
          <button
            type="button"
            @click="closeModal"
            class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
          >
            Hủy
          </button>
          <button
            type="submit"
            class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            {{ isEditing ? 'Cập nhật' : 'Thêm mới' }}
          </button>
        </div>
      </form>
    </Dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import Dialog from 'primevue/dialog';

interface Product {
  id: number;
  name: string;
  price: number;
  stock: number;
  description: string;
  image: string;
  category: string;
  views: number;
  sales: number;
}

const products = ref<Product[]>([
  {
    id: 1,
    name: 'Sản phẩm mẫu 1',
    price: 100000,
    stock: 50,
    description: 'Mô tả sản phẩm mẫu 1',
    image: 'https://via.placeholder.com/150',
    category: 'Danh mục 1',
    views: 1000,
    sales: 50,
  },
  // Thêm dữ liệu mẫu khác nếu cần
]);

const searchQuery = ref('');
const sortBy = ref('name');
const showModal = ref(false);
const isEditing = ref(false);
const currentProduct = ref<Partial<Product>>({});

const filteredProducts = computed(() => {
  let filtered = [...products.value];
  
  // Tìm kiếm
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    filtered = filtered.filter(product => 
      product.name.toLowerCase().includes(query) ||
      product.description.toLowerCase().includes(query)
    );
  }
  
  // Sắp xếp
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'name':
        return a.name.localeCompare(b.name);
      case 'price':
        return a.price - b.price;
      case 'stock':
        return a.stock - b.stock;
      case 'views':
        return b.views - a.views;
      case 'sales':
        return b.sales - a.sales;
      default:
        return 0;
    }
  });
  
  return filtered;
});

const formatPrice = (price: number) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND'
  }).format(price);
};

const openAddProductModal = () => {
  isEditing.value = false;
  currentProduct.value = {
    name: '',
    price: 0,
    stock: 0,
    description: '',
    image: '',
    category: '',
    views: 0,
    sales: 0,
  };
  showModal.value = true;
};

const editProduct = (product: Product) => {
  isEditing.value = true;
  currentProduct.value = { ...product };
  showModal.value = true;
};

const closeModal = () => {
  showModal.value = false;
  currentProduct.value = {};
};

const handleImageUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    const reader = new FileReader();
    reader.onload = (e) => {
      currentProduct.value.image = e.target?.result as string;
    };
    reader.readAsDataURL(target.files[0]);
  }
};

const saveProduct = () => {
  if (isEditing.value) {
    // TODO: Implement API call to update product
    const index = products.value.findIndex(p => p.id === currentProduct.value.id);
    if (index !== -1) {
      products.value[index] = { ...products.value[index], ...currentProduct.value };
    }
  } else {
    // TODO: Implement API call to create new product
    const newProduct = {
      ...currentProduct.value,
      id: products.value.length + 1,
      views: 0,
      sales: 0,
    } as Product;
    products.value.push(newProduct);
  }
  closeModal();
};

const deleteProduct = (product: Product) => {
  if (confirm('Bạn có chắc chắn muốn xóa sản phẩm này?')) {
    // TODO: Implement API call to delete product
    products.value = products.value.filter(p => p.id !== product.id);
  }
};
</script> 