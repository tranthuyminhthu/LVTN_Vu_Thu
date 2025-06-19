import { ref } from 'vue';

const isLoading = ref(false);
const message = ref('Đang tải...');

export function useLoading() {
  const show = (msg = 'Đang tải...') => {
    message.value = msg;
    isLoading.value = true;
  };

  const hide = () => {
    isLoading.value = false;
  };

  return {
    isLoading,
    message,
    show,
    hide
  };
} 