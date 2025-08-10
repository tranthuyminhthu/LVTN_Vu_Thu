<template>
  <div class="flex gap-2">
          <label 
        v-for="item in colorOptions" 
        :key="item.id"
        :for="'color_' + (props.name || '') + '_' + item.id" 
        class="cursor-pointer"
      >
      <RadioButton
        v-model="selectedValue"
        :name="props.name || 'color'"
        :value="item.id"
        class="hidden"
        :inputId="'color_' + (props.name || '') + '_' + item.id"
        @change="handleChange"
      />
      <div 
        class="rounded-full w-[50px] h-[25px] overflow-hidden" 
        :style="selectedValue === item.id ? 'border: 2px solid #2f5acf; padding: 2px; background: white;' : ''"
      >
        <img
          v-if="item.type === 'image'"
          :src="item.src"
          :alt="item.alt"
          class="rounded-full w-full h-full cursor-pointer"
        />
        <span 
          v-else 
          class="rounded-full w-full h-full block"
          :style="{ backgroundColor: item.color }"
        ></span>
      </div>
    </label>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';
import RadioButton from 'primevue/radiobutton';

export interface ColorOption {
  id: string | number;
  type: 'image' | 'color';
  src?: string;
  alt?: string;
  color?: string;
}

const props = defineProps<{
  modelValue: string | number;
  colorOptions: ColorOption[];
  name?: string;
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