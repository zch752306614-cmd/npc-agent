<script setup lang="ts">
defineProps<{ title: string; visible: boolean }>();
defineEmits<{ close: [] }>();
</script>

<template>
  <div v-if="visible" class="modal" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <h3>{{ title }}</h3>
      <slot />
      <button class="close-btn" @click="$emit('close')">关闭</button>
    </div>
  </div>
</template>

<style scoped>
.modal {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  width: min(540px, 90vw);
  max-height: 80vh;
  overflow-y: auto;
  border-radius: 14px;
  background: #1a1a2e;
  color: #fff;
  padding: 20px;
}

.close-btn {
  width: 100%;
  margin-top: 14px;
  border: 0;
  padding: 10px 0;
  border-radius: 8px;
  color: #fff;
  background: #4caf50;
}
</style>
