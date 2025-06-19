<template>
  <div class="flex gap-2">
    <label 
      v-for="item in sizeOptions" 
      :key="item.id"
      :for="'size_' + item.id" 
      class="cursor-pointer"
    >
      <RadioButton
        v-model="selectedValue"
        name="size"
        :value="item.id"
        class="hidden"
        :inputId="'size_' + item.id"
        @change="handleChange"
      />
      <div 
        class="rounded-full w-[50px] h-[25px] overflow-hidden flex items-center justify-center border border-[#e6e6e680]" 
        :style="selectedValue === item.id ? 'border: 2px solid #2f5acf; padding: 2px; background: white;' : ''"
      >
        <span class="text-sm">{{ item.label }}</span>
      </div>
    </label>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';
import RadioButton from 'primevue/radiobutton';

export interface SizeOption {
  id: string | number;
  label: string;
}

const props = defineProps<{
  modelValue: string | number;
  sizeOptions: SizeOption[];
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: string | number): void;
}>();

const selectedValue = ref(props.modelValue);

watch(() => props.modelValue, (newValue) => {
  selectedValue.value = newValue;
});

const handleChange = () => {
  emit('update:modelValue', selectedValue.value);
};
</script>

