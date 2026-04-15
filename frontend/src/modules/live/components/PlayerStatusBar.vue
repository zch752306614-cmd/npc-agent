<script setup lang="ts">
import type { PlayerState } from '../../../types/live';

defineProps<{
  player: PlayerState;
  currentSceneName: string;
}>();
</script>

<template>
  <div class="player-status-bar">
    <div class="status-item"><span class="label">姓名：</span><span>{{ player.name }}</span></div>
    <div class="status-item"><span class="label">境界：</span><span>{{ player.realm }}</span></div>
    <div class="status-item"><span class="label">等级：</span><span>{{ player.level }}</span></div>
    <div class="status-item"><span class="label">生命：</span><span>{{ player.health }}/{{ player.maxHealth }}</span></div>
    <div class="status-item">
      <span class="label">灵力：</span><span>{{ player.spiritualPower }}/{{ player.maxSpiritualPower }}</span>
    </div>
    <div class="status-item"><span class="label">场景：</span><span>{{ currentSceneName }}</span></div>
  </div>
</template>

<style scoped>
.player-status-bar {
  position: absolute;
  top: 20px;
  left: 20px;
  padding: 14px 18px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  max-width: 480px;
}

.status-item {
  display: flex;
  gap: 4px;
}

.label {
  color: #63de6a;
  font-weight: 700;
}
</style>
