<script setup lang="ts">
import BaseModal from '../../../components/BaseModal.vue';
import type { TreasureResult } from '../../../types/live';

defineProps<{
  visible: boolean;
  result: TreasureResult;
}>();

defineEmits<{ close: [] }>();
</script>

<template>
  <BaseModal title="寻宝结果" :visible="visible" @close="$emit('close')">
    <p>地点：{{ result.location }}</p>
    <p>奖励：{{ result.reward }}</p>
    <p>稀有度：{{ result.rarity }}</p>
  </BaseModal>
</template>
