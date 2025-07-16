<template>
  <div class="p-6">
    <!-- Upload Section -->

    <div class="bg-white rounded-lg p-6 shadow-sm mb-6">
      <h2 class="text-lg font-semibold mb-4">Tải lên hình ảnh để nhận gợi ý</h2>
      <LoadingGlobal :isLoading="isLoading">
        <template #default>
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
      </LoadingGlobal>
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
        <ProductCard
          v-for="product in sortedProducts"
          :key="product.id"
          :product="product"
        >
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
import LoadingGlobal from "@/components/LoadingGlobal.vue";
import type { Product } from "@/types";
import { getRecommend } from "@/api/recommend";
const { show, hide } = useLoading();
const isLoading = ref(false);

interface Color {
  hex: string;
  name: string;
}

interface AnalysisResults {
  styles: string[];
  colors: Color[];
}

const fileInput = ref<HTMLInputElement | null>(null);
const selectedImage = ref<string | null>(null);
const selectedFile = ref<File | null>(null);
const isAnalyzing = ref(false);
const analysisResults = ref<AnalysisResults | null>(null);
const recommendedProducts = ref<Product[]>([]);
const sortedProducts = computed(() => {
  return [...recommendedProducts.value];
});

const handleFileUpload = (event: Event) => {
  const input = event.target as HTMLInputElement;
  if (input.files && input.files[0]) {
    const file = input.files[0];
    selectedFile.value = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      selectedImage.value = e.target?.result as string;
    };
    reader.readAsDataURL(file);
  }
};

const removeImage = () => {
  selectedImage.value = null;
  selectedFile.value = null;
  analysisResults.value = null;
  recommendedProducts.value = [];
  if (fileInput.value) {
    fileInput.value.value = "";
  }
};

const mapRecommendations = (recommendations: any[]): Product[] => {
  return recommendations.map((item) => ({
    id: item.product_id,
    name: item.product_name || item.category_vi,
    description: item.product_description || '',
    price: item.product_price,
    rating: (item.confidence || 0.8) * 5, // scale confidence to 5-star rating
    status: "available",
    variants: [],
    images: item.images && Array.isArray(item.images) ? item.images : [],
  }));
};

const analyzeImage = async () => {
  if (!selectedFile.value) return;
  isLoading.value = true;
  isAnalyzing.value = true;
  try {
    const response = await getRecommend(selectedFile.value);
    console.log(response);

    // Nếu response có trường recommendations thì map ra recommendedProducts
    if (response && response.recommendations) {
      recommendedProducts.value = mapRecommendations(response.recommendations);
    } else {
      recommendedProducts.value = [];
    }

    // Nếu muốn map thêm analysisResults từ response, có thể làm ở đây
    // analysisResults.value = ...

  } catch (error) {
    console.error("Error analyzing image:", error);
  } finally {
    isAnalyzing.value = false;
    isLoading.value = false;
  }
};

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
