import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const usePermissionStore = defineStore('permission', () => {
  const permissions = ref(['live:read', 'live:action']);
  const canOperateLiveRoom = computed(() => permissions.value.includes('live:action'));

  return {
    permissions,
    canOperateLiveRoom
  };
});
