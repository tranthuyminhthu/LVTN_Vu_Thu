<template>
  <div class="bg-[#f2f2f2] p-8 grid">
    <Toast />
    <div class="col-3 flex flex-col gap-4">
      <p class="text-5xl font-bold uppercase">Đánh giá</p>
      <p class="text-5xl font-bold uppercase">Sản phẩm</p>
      <IconField>
        <InputIcon class="pi pi-search" />
        <InputText
          v-model="value1"
          placeholder="Tìm kiếm sản phẩm"
          size="small"
          class="!rounded-full w-full"
        />
      </IconField>
      <div class="">
        <p class="text-[#a3a3a3] font-bold">Phân loại sếp hạng</p>
        <div class="flex gap-2 flex-col mt-2">
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="star"
              inputId="fiveStar"
              name="star"
              value="five"
            />
            <label for="fiveStar" class="flex items-center gap-2">
              <span>5</span>
              <Rating :modelValue="5" readonly class="!text-black" />
              <span class="text-gray-500">({{ starStats.five }})</span>
            </label>
          </div>
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="star"
              inputId="fourStar"
              name="star"
              value="four"
            />
            <label for="fourStar" class="flex items-center gap-2">
              <span>4</span>
              <Rating :modelValue="4" readonly class="!text-black" />
              <span class="text-gray-500">({{ starStats.four }})</span>
            </label>
          </div>

          <div class="flex items-center gap-2">
            <RadioButton
              v-model="star"
              inputId="threeStar"
              name="star"
              value="three"
            />
            <label for="threeStar" class="flex items-center gap-2">
              <span>3</span>
              <Rating :modelValue="3" readonly class="!text-black" />
              <span class="text-gray-500">({{ starStats.three }})</span>
            </label>
          </div>
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="star"
              inputId="twoStar"
              name="star"
              value="two"
            />
            <label for="twoStar" class="flex items-center gap-2">
              <span>2</span>
              <Rating :modelValue="2" readonly class="!text-black" />
              <span class="text-gray-500">({{ starStats.two }})</span>
            </label>
          </div>
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="star"
              inputId="oneStar"
              name="star"
              value="one"
            />
            <label for="oneStar" class="flex items-center gap-2">
              <span>1</span>
              <Rating :modelValue="1" readonly class="!text-black" />
              <span class="text-gray-500">({{ starStats.one }})</span>
            </label>
          </div>
        </div>
      </div>
      <div class="flex gap-2 bg-[#e3e6fa] rounded-md p-2 w-[80%]">
        <img
          src="https://mcdn.coolmate.me//image/February2025/mceclip0_64.png"
          alt=""
          class="w-[26px] object-contain"
        />
        <p class="text-[#273bcd] text-sm">
          Các review đều đến từ khách hàng đã thực sự mua hàng của Coolmate
        </p>
      </div>
      <div class="">
        <p class="text-[#a3a3a3] font-bold">Lọc phản hồi</p>
        <div class="flex gap-2 flex-col mt-2">
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="response"
              inputId="responded"
              name="response"
              value="responded"
            />
            <label for="responded" class="flex items-center gap-2">
              <span>Đã phản hồi</span>
            </label>
          </div>
          <div class="flex items-center gap-2">
            <RadioButton
              v-model="response"
              inputId="unresponded"
              name="response"
              value="unresponded"
            />
            <label for="unresponded" class="flex items-center gap-2">
              <span>Có hình ảnh</span>
            </label>
          </div>
        </div>
        <!-- Clear filters button -->
        <div v-if="star || response" class="mt-4">
          <Button 
            label="Xóa bộ lọc" 
            icon="pi pi-times" 
            size="small"
            severity="secondary"
            @click="clearFilters"
            class="w-full"
          />
        </div>
      </div>
    </div>
    <div class="col-8">
      <div class="">
        <div class="flex items-center gap-2">
          <span class="text-8xl font-bold">{{ averageRating.toFixed(1) }}</span>
          <div class="">
            <span
              class="h-[32px] w-[32px] bg-no-repeat bg-contain inline-block"
              style="
                background-image: url(&quot;https://www.coolmate.me/images/star-yellow.svg?f45857fa30ff48521e7bc19ed0871508&quot;);
              "
            ></span>
            <span
              class="h-[32px] w-[32px] bg-no-repeat bg-contain inline-block"
              style="
                background-image: url(&quot;https://www.coolmate.me/images/star-yellow.svg?f45857fa30ff48521e7bc19ed0871508&quot;);
              "
            ></span>
            <span
              class="h-[32px] w-[32px] bg-no-repeat bg-contain inline-block"
              style="
                background-image: url(&quot;https://www.coolmate.me/images/star-yellow.svg?f45857fa30ff48521e7bc19ed0871508&quot;);
              "
            ></span>
            <span
              class="h-[32px] w-[32px] bg-no-repeat bg-contain inline-block"
              style="
                background-image: url(&quot;https://www.coolmate.me/images/star-yellow.svg?f45857fa30ff48521e7bc19ed0871508&quot;);
              "
            ></span>
            <span
              class="h-[32px] w-[32px] bg-no-repeat bg-contain inline-block"
              style="
                background-image: url(https://www.coolmate.me/images/star-yellow-half.svg?c15aca386e56880113c78217141727a4);
              "
            ></span>
          </div>
        </div>
        <p class="text-sm my-2">Dựa trên {{ comments.length }} đánh giá đến từ khách hàng</p>
      </div>
      <div class=""></div>
      
      <!-- Loading state -->
      <div v-if="loading" class="flex justify-center items-center py-8">
        <ProgressSpinner />
      </div>
      
      <!-- Comments list -->
      <div v-else class="flex flex-col gap-4">
        <div v-if="filteredComments.length === 0" class="text-center py-8 text-gray-500">
          {{ (star || response) ? 'Không có đánh giá nào phù hợp với bộ lọc' : 'Chưa có đánh giá nào cho sản phẩm này' }}
        </div>
        <div 
          v-for="comment in filteredComments" 
          :key="comment.id" 
          class="p-4 bg-white rounded-lg"
        >
          <div class="flex items-center gap-4">
            <span class="font-bold">{{ comment.userName }}</span>
            <span class="text-[#a3a3a3]">{{ formatDate(comment.createdAt) }}</span>
          </div>
          <div class="my-2">
            <Rating :modelValue="comment.rating" readonly class="!text-black" />
          </div>
          <div class="mt-2 comment-content" v-html="comment.content"></div>
        </div>
      </div>
      
      <div class="mt-6">
        <div class="bg-white p-6 rounded-lg">
          <h3 class="text-xl font-bold">Viết đánh giá của bạn</h3>
          <div class="flex items-center gap-4 mb-4">
            <div class="flex-1">
              <div class="my-4">
                <Rating v-model="userRating" :cancel="false" />
              </div>
              <Editor 
                v-model="userComment" 
                editorStyle="height: 200px"
                placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                class="w-full"
              />
            </div>
          </div>
          <div class="flex">
            <Button 
              label="Gửi đánh giá" 
              icon="pi pi-send" 
              class="p-button-primary"
              :loading="submitting"
              @click="submitComment"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import IconField from "primevue/iconfield";
import InputIcon from "primevue/inputicon";
import InputText from "primevue/inputtext";
import RadioButton from "primevue/radiobutton";
import Rating from "primevue/rating";
import Editor from "primevue/editor";
import Button from "primevue/button";
import ProgressSpinner from "primevue/progressspinner";
import Toast from "primevue/toast";
import { ref, computed, onMounted } from "vue";
import { getProductComments, createProductComment } from "@/api/product";
import { useToast } from "primevue/usetoast";
import type { Comment } from "@/types";

// Props
interface Props {
  productId: number;
}

const props = defineProps<Props>();

// Toast setup
const toast = useToast();

// Reactive data
const value1 = ref("");
const star = ref("");
const response = ref("");
const userRating = ref(0);
const userComment = ref("");
const comments = ref<Comment[]>([]);
const loading = ref(false);
const submitting = ref(false);

// Computed
const averageRating = computed(() => {
  if (comments.value.length === 0) return 0;
  const total = comments.value.reduce((sum, comment) => sum + comment.rating, 0);
  return total / comments.value.length;
});

// Filtered comments based on selected filters
const filteredComments = computed(() => {
  let filtered = [...comments.value];

  // Filter by star rating
  if (star.value) {
    const ratingMap: { [key: string]: number } = {
      'five': 5,
      'four': 4,
      'three': 3,
      'two': 2,
      'one': 1
    };
    
    const selectedRating = ratingMap[star.value];
    if (selectedRating) {
      filtered = filtered.filter(comment => comment.rating === selectedRating);
    }
  }

  // Filter by response type
  if (response.value === 'responded') {
    // TODO: Add logic for responded comments when API supports it
    // For now, show all comments
  } else if (response.value === 'unresponded') {
    // TODO: Add logic for unresponded comments when API supports it
    // For now, show all comments
  }

  return filtered;
});

// Star rating statistics
const starStats = computed(() => {
  const stats = {
    five: 0,
    four: 0,
    three: 0,
    two: 0,
    one: 0
  };

  comments.value.forEach(comment => {
    switch (comment.rating) {
      case 5:
        stats.five++;
        break;
      case 4:
        stats.four++;
        break;
      case 3:
        stats.three++;
        break;
      case 2:
        stats.two++;
        break;
      case 1:
        stats.one++;
        break;
    }
  });

  return stats;
});

// Methods
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric'
  });
};

const fetchComments = async () => {
  try {
    loading.value = true;
    const data = await getProductComments(props.productId);
    comments.value = data;
  } catch (error) {
    console.error('Error fetching comments:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể tải danh sách đánh giá',
      life: 3000
    });
  } finally {
    loading.value = false;
  }
};

const submitComment = async () => {
  if (userRating.value === 0) {
    toast.add({
      severity: 'warn',
      summary: 'Cảnh báo',
      detail: 'Vui lòng chọn số sao đánh giá',
      life: 3000
    });
    return;
  }

  if (!userComment.value.trim()) {
    toast.add({
      severity: 'warn',
      summary: 'Cảnh báo',
      detail: 'Vui lòng nhập nội dung đánh giá',
      life: 3000
    });
    return;
  }
  
  try {
    submitting.value = true;
    
    const payload = {
      productId: props.productId,
      content: userComment.value,
      rating: userRating.value
    };

    await createProductComment(payload);
    
    toast.add({
      severity: 'success',
      summary: 'Thành công',
      detail: 'Đánh giá của bạn đã được gửi thành công',
      life: 3000
    });
    
    // Reset form
    userRating.value = 0;
    userComment.value = "";
    
    // Refresh comments
    await fetchComments();
  } catch (error) {
    console.error('Error submitting comment:', error);
    toast.add({
      severity: 'error',
      summary: 'Lỗi',
      detail: 'Không thể gửi đánh giá. Vui lòng thử lại sau.',
      life: 3000
    });
  } finally {
    submitting.value = false;
  }
};

const clearFilters = () => {
  star.value = "";
  response.value = "";
};

// Lifecycle
onMounted(() => {
  fetchComments();
});
</script>

<style scoped>
:deep(.p-rating-option-active .p-rating-icon) {
  color: black !important;
}

:deep(.p-editor-container) {
  border-radius: 0.5rem;
}

:deep(.p-editor-content) {
  min-height: 200px;
}

/* Handle large images in comments */
:deep(.p-editor-content img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 8px 0;
}

/* Specific styling for comment content images */
.comment-content :deep(img) {
  max-width: 300px;
  max-height: 300px;
  object-fit: cover;
  border-radius: 8px;
  margin: 8px 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* Responsive image sizing */
@media (max-width: 768px) {
  .comment-content :deep(img) {
    max-width: 200px;
    max-height: 200px;
  }
}

@media (max-width: 480px) {
  .comment-content :deep(img) {
    max-width: 150px;
    max-height: 150px;
  }
}
</style>
