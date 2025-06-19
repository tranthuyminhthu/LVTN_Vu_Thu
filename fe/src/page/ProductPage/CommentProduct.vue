<template>
  <div class="bg-[#f2f2f2] p-8 grid">
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
      </div>
    </div>
    <div class="col-8">
      <div class="">
        <div class="flex items-center gap-2">
          <span class="text-8xl font-bold">4.8</span>
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
        <p class="text-sm my-2">Dựa trên 44 đánh giá đến từ khách hàng</p>
      </div>
      <div class=""></div>
      <div class="flex flex-col gap-4">
        <div class="p-4 bg-white rounded-lg">
          <div class="flex items-center gap-4">
            <span class="font-bold">Minh Thu</span>
            <span class="text-[#a3a3a3]">21.03.2025</span>
          </div>
          <div class="my-2">
            <Rating :modelValue="3" readonly class="!text-black" />
          </div>
          <div class="">Mem mat de su dung</div>
          <div class="">
            <img
              src="https://mcdn.coolmate.me//image/February2025/mceclip0_64.png"
              alt=""
              class="w-[60px] h-[60px] object-contain"
            />
          </div>
        </div>
        <div class="p-4 bg-white rounded-lg">
          <div class="flex items-center gap-4">
            <span class="font-bold">Minh Thu</span>
            <span class="text-[#a3a3a3]">21.03.2025</span>
          </div>
          <div class="my-2">
            <Rating :modelValue="3" readonly class="!text-black" />
          </div>
          <div class="">Mem mat de su dung</div>
          <div class="">
            <img
              src="https://mcdn.coolmate.me//image/February2025/mceclip0_64.png"
              alt=""
              class="w-[60px] h-[60px] object-contain"
            />
          </div>
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
              <Textarea 
                v-model="userComment" 
                rows="4" 
                placeholder="Chia sẻ trải nghiệm của bạn về sản phẩm này..."
                class="w-full p-3 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>
          <div class="flex">
            <Button 
              label="Gửi đánh giá" 
              icon="pi pi-send" 
              class="p-button-primary"
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
import Textarea from "primevue/textarea";
import Button from "primevue/button";
import { ref } from "vue";

const value1 = ref("");
const star = ref("one");
const response = ref("responded");
const userRating = ref(0);
const userComment = ref("");

const submitComment = () => {
  // TODO: Implement comment submission logic
  console.log("Rating:", userRating.value);
  console.log("Comment:", userComment.value);
  // Reset form
  userRating.value = 0;
  userComment.value = "";
};
</script>

<style scoped>
:deep(.p-rating-option-active .p-rating-icon) {
  color: black !important;
}
</style>
