<script setup lang="ts">
import BaseModal from '../../../components/BaseModal.vue';
import type { InventoryItem } from '../../../types/live';

defineProps<{
  visible: boolean;
  items: InventoryItem[];
}>();

defineEmits<{
  close: [];
  useItem: [itemId: string];
}>();
</script>

<template>
  <BaseModal title="背包" :visible="visible" @close="$emit('close')">
    <p v-if="!items.length">背包是空的</p>
    <div v-for="item in items" :key="item.id" class="inventory-item">
      <span>{{ item.name }} x {{ item.quantity }}</span>
      <button type="button" @click="$emit('useItem', item.id)">使用</button>
    </div>
  </BaseModal>
</template>

<style scoped>
.inventory-item {
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
</style>
