<script setup lang="ts">
import BaseModal from '../../../components/BaseModal.vue';
import type { PlayerState } from '../../../types/live';

defineProps<{
  visible: boolean;
  player: PlayerState;
}>();

defineEmits<{ close: [] }>();
</script>

<template>
  <BaseModal title="角色状态" :visible="visible" @close="$emit('close')">
    <p>姓名：{{ player.name }}</p>
    <p>境界：{{ player.realm }}</p>
    <p>等级：{{ player.level }}</p>
    <p>修为等级：{{ player.cultivationLevel }}</p>
    <p>攻击：{{ player.attack }}，防御：{{ player.defense }}</p>
  </BaseModal>
</template>
