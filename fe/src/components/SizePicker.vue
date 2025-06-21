<template>
  <div class="flex gap-2">
    <label 
      v-for="item in sizeOptions" 
      :key="item.id"
      :for="'size_' + item.id" 
      :class="[
        'cursor-pointer',
        { 'cursor-not-allowed': item.disabled }
      ]"
    >
      <RadioButton
        v-model="selectedValue"
        name="size"
        :value="item.id"
        class="hidden"
        :inputId="'size_' + item.id"
        :disabled="item.disabled"
        @change="handleChange"
      />
      <div 
        class="relative rounded-lg w-[65px] h-[40px] overflow-hidden flex items-center justify-center border border-[#e6e6e680]" 
        :class="{
          'bg-black': selectedValue === item.id && !item.disabled,
          'bg-gray-100': item.disabled
        }"
      >
        <span class="text-sm" :class="{ 'text-gray-400': item.disabled, 'text-white': selectedValue === item.id && !item.disabled }">{{ item.label }}</span>
        <div 
          v-if="item.disabled" 
          class="absolute inset-0 w-full h-full bg-no-repeat bg-center"
          style="background-image: url('data:image/svg+xml,%3csvg width=\'100%25\' height=\'100%25\' xmlns=\'http://www.w3.org/2000/svg\'%3e%3cline x1=\'0\' y1=\'0\' x2=\'100%25\' y2=\'100%25\' stroke=\'%23cccccc\' stroke-width=\'1\' stroke-dasharray=\'2 2\'/%3e%3cline x1=\'100%25\' y1=\'0\' x2=\'0\' y2=\'100%25\' stroke=\'%23cccccc\' stroke-width=\'1\' stroke-dasharray=\'2 2\'/%3e%3c/svg%3e');"
        ></div>
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
  disabled?: boolean;
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

