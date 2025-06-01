<template>
  <AppHeader title="Product List" />
  <div class="!mt-header max-w-primary mx-auto pt-12">
    <div class="grid">
      <div class="col !pr-12 pt-8">
        <h1 class="font-bold text-2xl mb-4 text-center">Sign up with email</h1>
        <FloatLabel variant="in" class="mb-2 w-full">
          <InputText
            id="full_name"
            v-model="fullName"
            variant="filled"
            class="w-full"
          />
          <label for="full_name">Full name</label>
        </FloatLabel>
        <FloatLabel variant="in">
          <InputText
            id="email"
            v-model="email"
            variant="filled"
            class="w-full"
          />
          <label for="email">Email</label>
        </FloatLabel>
        <div class="flex items-center gap-2 my-4">
          <Checkbox
            v-model="checked"
            inputId="ingredient1"
            name="pizza"
            value="Cheese"
          />
          <label for="ingredient1">
            Send me special offers. personalized recommendations, and learning
            tips.</label
          >
        </div>
        <Button
          label="Continue with email"
          icon="pi pi-envelope"
          class="w-full"
        />
        <Divider align="center">
          <b>Other sign up options</b>
        </Divider>
        <div class="flex items-center justify-center gap-4 mt-4">
          <Button variant="outlined" class="!border-2">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/1200px-Google_%22G%22_logo.svg.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
          <Button variant="outlined" class="!border-2">
            <img
              src="https://upload.wikimedia.org/wikipedia/commons/thumb/0/05/Facebook_Logo_%282019%29.png/1200px-Facebook_Logo_%282019%29.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
          <Button variant="outlined" class="!border-2">
            <img
              src="https://www.iwarebatik.org/wp-content/uploads/2019/07/1200px-Apple_logo_black.svg.png"
              alt=""
              class="w-[24px] h-[24px]"
            />
          </Button>
        </div>
        <p class="text-center text-xs my-4">
          By signing up, you agree to our
          <a href="/" class="text-primary underline">Terms of Use</a> and
          <a href="/" class="text-primary underline">Privacy Policy</a>
        </p>
        <p class="bg-gray-100 p-4 text-center rounded">
          Already have an account?
          <a href="/login" class="text-primary font-bold underline">Log in</a>
        </p>
      </div>

      <div class="col">
        <img
          src="https://frontends.udemycdn.com/components/auth/desktop-illustration-x1.webp"
          alt=""
        />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import AppHeader from "../components/AppHeader.vue";
import InputText from "primevue/inputtext";
import FloatLabel from "primevue/floatlabel";
import Checkbox from "primevue/checkbox";
import Button from "primevue/button";
import Divider from "primevue/divider";
import Select from "primevue/select";
import { ref } from "vue";

const fullName = ref("");
const email = ref("");
const checked = ref(false);
</script>
