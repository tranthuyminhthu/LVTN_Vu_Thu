<script setup lang="ts">
import Button from "primevue/button";
import Divider from "primevue/divider";
import Tabs from "primevue/tabs";
import TabList from "primevue/tablist";
import Tab from "primevue/tab";
import TabPanels from "primevue/tabpanels";
import TabPanel from "primevue/tabpanel";

import { ref } from "vue";

const loading = ref(false);
import { useRouter } from "vue-router";

const router = useRouter();

const load = () => {
    loading.value = true;
    setTimeout(() => {
        loading.value = false;
        router.push({ name: "home" });
    }, 2000);
};
</script>
<template>
  <div class="p-4 flex flex-col items-center h-screen">
    <p class="text-center font-bold text-xl pb-28">ShopNow</p>
    <p class="text-center font-bold text-xl">Get started now</p>
    <div class="gap-4 flex items-center mt-4">
      <Button
        label="Sign up with Google"
        severity="danger"
        variant="outlined"
        icon="pi pi-google"
      />
      <Button
        label="Sign up with Facebook"
        severity="info"
        variant="outlined"
        icon="pi pi-facebook"
      />
    </div>
    <Divider align="center">
      <b>Or</b>
    </Divider>
    <p class="text-center text-lg mt-4">
      You have the option to register using either your email or phone number
    </p>
    <Tabs value="0" class="w-full">
      <TabList>
        <Tab value="0" class="flex-1">Email</Tab>
        <Tab value="1" class="flex-1">Phone Number</Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <p class="m-0 ">
            <input
              type="text"
              placeholder="Enter your email"
              class="border-2 border-gray-300 rounded-md p-2 w-full"
            />
          </p>
        </TabPanel>
        <TabPanel value="1">
          <p class="m-0">
            <input
              type="text"
              placeholder="Enter your phone number"
              class="border-2 border-gray-300 rounded-md p-2 w-full "
            />
          </p>
        </TabPanel>
      </TabPanels>
    </Tabs>
    <Button type="button" label="Continue" icon="pi pi-arrow-right" :loading="loading" @click="load" iconPos="right" class="w-full"/>
    <div class="mt-auto">Have an account</div>
  </div>
</template>
