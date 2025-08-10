import type { UserInfo } from '@/types';
import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null as UserInfo | null,
  }),
  actions: {
    login(userData: UserInfo) {
      this.user = userData;
    },
    logout() {
      this.user = null;
    },
    hasRole(role: string) {
      return this.user && this.user.role.includes(role);
    },
  },
});