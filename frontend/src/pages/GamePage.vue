<script setup lang="ts">
import BaseModal from '../components/BaseModal.vue';
import CombatModal from '../modules/live/components/CombatModal.vue';
import DialoguePanel from '../modules/live/components/DialoguePanel.vue';
import GameActions from '../modules/live/components/GameActions.vue';
import PlayerStatusBar from '../modules/live/components/PlayerStatusBar.vue';
import SceneSwitcher from '../modules/live/components/SceneSwitcher.vue';
import { useLiveRoom } from '../modules/live/hooks/useLiveRoom';

const liveRoom = useLiveRoom();
</script>

<template>
  <div class="game-container">
    <div class="scene-background" :class="liveRoom.player.currentScene">
      <div class="scene-overlay" />
    </div>

    <PlayerStatusBar :player="liveRoom.player" :current-scene-name="liveRoom.currentSceneName" />

    <GameActions
      :disabled="liveRoom.isLoading"
      @explore="liveRoom.handleExploreScene"
      @status="liveRoom.showStatusModal = true"
      @inventory="liveRoom.handleLoadInventory"
      @treasure="liveRoom.handleTreasureHunt"
      @story="liveRoom.handleLoadStoryProgress"
    />

    <SceneSwitcher
      :scene-names="liveRoom.sceneNames"
      :current-scene="liveRoom.player.currentScene"
      :disabled="liveRoom.isLoading"
      @change="liveRoom.handleChangeScene"
    />

    <DialoguePanel
      :loading="liveRoom.isLoading"
      :options="liveRoom.dialogueOptions"
      :history="liveRoom.dialogueHistory"
      :npc-name="liveRoom.currentNpc.name"
      :player-name="liveRoom.player.name"
      :free-input-text="liveRoom.freeInputText"
      @select-option="liveRoom.handleSelectFixedOption"
      @send-input="liveRoom.handleSendFreeInput"
      @update:free-input-text="liveRoom.freeInputText = $event"
    />

    <BaseModal title="探索结果" :visible="liveRoom.showExploreModal" @close="liveRoom.showExploreModal = false">
      <p>{{ liveRoom.exploreResult.title }}</p>
      <p>{{ liveRoom.exploreResult.description }}</p>
      <p v-if="liveRoom.exploreResult.reward">奖励：{{ liveRoom.exploreResult.reward }}</p>
    </BaseModal>

    <BaseModal title="角色状态" :visible="liveRoom.showStatusModal" @close="liveRoom.showStatusModal = false">
      <p>姓名：{{ liveRoom.player.name }}</p>
      <p>境界：{{ liveRoom.player.realm }}</p>
      <p>等级：{{ liveRoom.player.level }}</p>
      <p>修为等级：{{ liveRoom.player.cultivationLevel }}</p>
      <p>攻击：{{ liveRoom.player.attack }}，防御：{{ liveRoom.player.defense }}</p>
    </BaseModal>

    <BaseModal title="背包" :visible="liveRoom.showInventoryModal" @close="liveRoom.showInventoryModal = false">
      <p v-if="!liveRoom.inventory.length">背包是空的</p>
      <div v-for="item in liveRoom.inventory" :key="item.id" class="inventory-item">
        <span>{{ item.name }} x {{ item.quantity }}</span>
        <button @click="liveRoom.handleUseItem(item.id)">使用</button>
      </div>
    </BaseModal>

    <BaseModal title="寻宝结果" :visible="liveRoom.showTreasureModal" @close="liveRoom.showTreasureModal = false">
      <p>地点：{{ liveRoom.treasureResult.location }}</p>
      <p>奖励：{{ liveRoom.treasureResult.reward }}</p>
      <p>稀有度：{{ liveRoom.treasureResult.rarity }}</p>
    </BaseModal>

    <BaseModal title="剧情进度" :visible="liveRoom.showStoryModal" @close="liveRoom.showStoryModal = false">
      <template v-if="liveRoom.storyProgress">
        <p>当前节点：{{ liveRoom.storyProgress.currentNode }}</p>
        <p>已解锁：{{ liveRoom.storyProgress.unlockedNodes.join('，') }}</p>
      </template>
      <p v-else>暂无剧情进度</p>
    </BaseModal>

    <CombatModal
      :visible="liveRoom.showCombatModal"
      :battle-state="liveRoom.battleState"
      @action="liveRoom.handleExecuteCombatTurn"
    />
  </div>
</template>

<style scoped>
.game-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.scene-background {
  width: 100%;
  height: 64%;
}

.scene-background.village {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scene-background.mountain {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.scene-background.cave {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.scene-overlay {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.32);
}

.inventory-item {
  margin: 8px 0;
  display: flex;
  justify-content: space-between;
  gap: 8px;
}
</style>
