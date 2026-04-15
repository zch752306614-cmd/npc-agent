<script setup lang="ts">
defineProps<{
  sceneNames: Record<string, string>;
  currentScene: string;
  disabled: boolean;
}>();

defineEmits<{ change: [sceneCode: string] }>();
</script>

<template>
  <div class="scene-switcher">
    <button
      v-for="(name, code) in sceneNames"
      :key="code"
      class="scene-btn"
      :class="{ active: currentScene === code }"
      :disabled="disabled || currentScene === code"
      @click="$emit('change', code)"
    >
      {{ name }}
    </button>
  </div>
</template>

<style scoped>
.scene-switcher {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}

.scene-btn {
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 16px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  cursor: pointer;
}

.scene-btn.active {
  background: rgba(76, 175, 80, 0.92);
}

.scene-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
