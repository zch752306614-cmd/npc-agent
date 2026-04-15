<script setup lang="ts">
import type { BattleState } from '../../../types/live';

defineProps<{
  visible: boolean;
  battleState: BattleState;
}>();

defineEmits<{
  action: [action: string];
}>();
</script>

<template>
  <div v-if="visible" class="modal">
    <div class="modal-content">
      <h3>战斗中</h3>
      <p>回合：{{ battleState.turn }}</p>
      <p>玩家生命：{{ battleState.playerHealth }}</p>
      <p>怪物生命：{{ battleState.monsterHealth }}</p>
      <div class="logs">
        <p v-for="(log, index) in battleState.logs" :key="index">{{ log }}</p>
      </div>
      <div class="actions">
        <button @click="$emit('action', 'attack')">攻击</button>
        <button @click="$emit('action', 'defend')">防御</button>
        <button @click="$emit('action', 'skill')">技能</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.modal {
  position: fixed;
  inset: 0;
  z-index: 1100;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.85);
}

.modal-content {
  width: min(640px, 90vw);
  border-radius: 14px;
  background: #1a1a2e;
  color: #fff;
  padding: 20px;
}

.logs {
  max-height: 200px;
  overflow-y: auto;
}

.actions {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

button {
  border: 0;
  border-radius: 8px;
  padding: 10px 0;
  background: #4caf50;
  color: #fff;
}
</style>
