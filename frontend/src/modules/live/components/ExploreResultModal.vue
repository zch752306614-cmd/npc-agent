<script setup lang="ts">
import BaseModal from '../../../components/BaseModal.vue';
import type { ExploreResult } from '../../../types/live';

defineProps<{
  visible: boolean;
  result: ExploreResult;
}>();

defineEmits<{ close: [] }>();
</script>

<template>
  <BaseModal title="探索结果" :visible="visible" @close="$emit('close')">
    <p>{{ result.title }}</p>
    <p>{{ result.description }}</p>
    <p v-if="result.reward">奖励：{{ result.reward }}</p>
  </BaseModal>
</template>
