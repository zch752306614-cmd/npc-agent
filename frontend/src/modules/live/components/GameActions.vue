<script setup lang="ts">
import { type Ref } from 'vue';

defineProps<{ disabled: Ref<boolean> }>();

defineEmits<{
  explore: [];
  status: [];
  inventory: [];
  treasure: [];
  story: [];
}>();
</script>

<template>
  <div class="game-actions">
    <button class="action-btn" :disabled="disabled.value" @click="$emit('explore')">🔍 探索</button>
    <button class="action-btn" @click="$emit('status')">📊 状态</button>
    <button class="action-btn" :disabled="disabled.value" @click="$emit('inventory')">🎒 背包</button>
    <button class="action-btn" :disabled="disabled.value" @click="$emit('treasure')">💎 寻宝</button>
    <button class="action-btn" :disabled="disabled.value" @click="$emit('story')">📖 剧情</button>
  </div>
</template>

<style scoped>
.game-actions {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-btn {
  border: none;
  border-radius: 8px;
  padding: 10px 16px;
  background: rgba(76, 175, 80, 0.92);
  color: #fff;
  cursor: pointer;
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
