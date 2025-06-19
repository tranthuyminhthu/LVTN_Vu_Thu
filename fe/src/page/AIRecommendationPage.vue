<template>
  <div class="p-6">
    <!-- Upload Section -->
    
    <div class="bg-white rounded-lg p-6 shadow-sm mb-6">
      <h2 class="text-lg font-semibold mb-4">Tải lên hình ảnh để nhận gợi ý</h2>
      <BlockUI :blocked="blocked">
        <template #default>
            <div class="absolute inset-0 flex flex-col items-center justify-center" v-if="blocked"><i class="pi pi-spin pi-spinner" style="font-size: 2rem"></i></div>
        
        <div
          class="border-2 border-dashed border-gray-300 rounded-lg p-8 text-center"
        >
          
            <div v-if="!selectedImage" class="space-y-4">
              <i class="pi pi-image text-4xl text-gray-400"></i>
              <p class="text-gray-500">Kéo thả hình ảnh vào đây hoặc</p>
              <input
                type="file"
                ref="fileInput"
                @change="handleFileUpload"
                accept="image/*"
                class="hidden"
              />
              <button
                @click="fileInput?.click()"
                class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
              >
                Chọn hình ảnh
              </button>
            </div>
            <div v-else class="space-y-4">
              <img
                :src="selectedImage"
                alt="Selected image"
                class="max-h-64 mx-auto rounded-lg"
              />
              <div class="flex justify-center gap-4">
                <button
                  @click="analyzeImage"
                  class="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500"
                  :disabled="isAnalyzing"
                >
                  <i class="pi pi-search mr-2"></i>
                  {{ isAnalyzing ? "Đang phân tích..." : "Phân tích hình ảnh" }}
                </button>
                <button
                  @click="removeImage"
                  class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-500"
                >
                  <i class="pi pi-times mr-2"></i>
                  Xóa hình ảnh
                </button>
              </div>
            </div>
        </div>
    </template>
      </BlockUI>
    </div>

    <!-- Analysis Results -->
    <div v-if="analysisResults" class="bg-white rounded-lg p-6 shadow-sm mb-6">
      <h2 class="text-lg font-semibold mb-4">Kết quả phân tích</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
        <!-- Style Analysis -->
        <div class="space-y-4">
          <h3 class="font-medium">Phong cách</h3>
          <div class="flex flex-wrap gap-2">
            <span
              v-for="style in analysisResults.styles"
              :key="style"
              class="px-3 py-1 bg-blue-100 text-blue-800 rounded-full text-sm"
            >
              {{ style }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- Recommended Products -->
    <div
      v-if="recommendedProducts.length > 0"
      class="bg-white rounded-lg p-6 shadow-sm"
    >
      <div class="!grid grid-cols-1 md:grid-cols-5 gap-6">
        <ProductCard v-for="product in sortedProducts" :key="product.id">
        </ProductCard>
      </div>
    </div>

    <!-- Loading State -->
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import ProductCard from "@/components/ProductCard.vue";
import { useLoading } from "@/composables/useLoading";
import BlockUI from "primevue/blockui";
const { show, hide } = useLoading();
const blocked = ref(false);

interface Color {
  hex: string;
  name: string;
}

interface AnalysisResults {
  styles: string[];
  colors: Color[];
}

interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  image: string;
  relevance: number;
}

const fileInput = ref<HTMLInputElement | null>(null);
const selectedImage = ref<string | null>(null);
const isAnalyzing = ref(false);
const analysisResults = ref<AnalysisResults | null>(null);
const recommendedProducts = ref<Product[]>([]);
const sortBy = ref("relevance");

const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    const reader = new FileReader();
    reader.onload = (e) => {
      selectedImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const removeImage = () => {
  selectedImage.value = null;
  analysisResults.value = null;
  recommendedProducts.value = [];
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

const analyzeImage = async () => {
  if (!selectedImage.value) return;
  blocked.value = true;
  isAnalyzing.value = true;
  try {
    // TODO: Implement actual AI analysis
    // This is mock data for demonstration
    await new Promise((resolve) => setTimeout(resolve, 2000));

    analysisResults.value = {
      styles: ["Casual", "Streetwear", "Minimalist"],
      colors: [
        { hex: "#3b82f6", name: "Xanh dương" },
        { hex: "#1f2937", name: "Xám đậm" },
        { hex: "#ffffff", name: "Trắng" },
      ],
    };

    recommendedProducts.value = [
      {
        id: 1,
        name: "Áo thun basic",
        description: "Áo thun cotton 100%",
        price: 199000,
        image: "https://via.placeholder.com/300",
        relevance: 0.95,
      },
      {
        id: 2,
        name: "Quần jean slim fit",
        description: "Quần jean co giãn",
        price: 499000,
        image: "https://via.placeholder.com/300",
        relevance: 0.85,
      },
      {
        id: 3,
        name: "Giày sneaker",
        description: "Giày thể thao đế cao su",
        price: 899000,
        image: "https://via.placeholder.com/300",
        relevance: 0.75,
      },
    ];
  } catch (error) {
    console.error("Error analyzing image:", error);
  } finally {
    isAnalyzing.value = false;
    blocked.value = false;
  }
};

const sortedProducts = computed(() => {
  const products = [...recommendedProducts.value];
  switch (sortBy.value) {
    case "price_asc":
      return products.sort((a, b) => a.price - b.price);
    case "price_desc":
      return products.sort((a, b) => b.price - a.price);
    default:
      return products.sort((a, b) => b.relevance - a.relevance);
  }
});

const formatPrice = (price: number) => {
  return new Intl.NumberFormat("vi-VN", {
    style: "currency",
    currency: "VND",
  }).format(price);
};

const addToStore = (product: Product) => {
  // TODO: Implement add to store functionality
  console.log("Adding product to store:", product);
};
</script>
