<script setup lang="ts">
import BaseModal from '../../../components/BaseModal.vue';
import type { StoryProgress } from '../../../types/live';

defineProps<{
  visible: boolean;
  progress: StoryProgress | null;
}>();

defineEmits<{ close: [] }>();
</script>

<template>
  <BaseModal title="剧情进度" :visible="visible" @close="$emit('close')">
    <template v-if="progress">
      <p>当前节点：{{ progress.currentNode }}</p>
      <p>已解锁：{{ progress.unlockedNodes.join('，') }}</p>
    </template>
    <p v-else>暂无剧情进度</p>
  </BaseModal>
</template>
